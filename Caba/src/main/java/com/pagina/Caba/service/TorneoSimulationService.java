package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TorneoSimulationService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private ArbitroRepository arbitroRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    /**
     * Genera un bracket simple para el torneo usando la lista de equipos dada.
     * Si la lista de equipos es menor que 2 se crearán equipos ficticios hasta 4.
     * No modifica la estructura del modelo existente: crea Partidos y Asignaciones
     * en la base de datos usando los repositorios existentes.
     */
    public void generarBracket(Long torneoId, List<String> equipos) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        // Normalizar equipos a al menos 4 participantes (semifinales -> final)
        List<String> equiposNorm = new ArrayList<>(equipos == null ? Collections.emptyList() : equipos);
        char suffix = 'A';
        while (equiposNorm.size() < 4) {
            equiposNorm.add("Equipo " + suffix);
            suffix++;
        }

        // Crear semifinales: pares (0 vs 1), (2 vs 3)
        LocalDateTime fechaBase = LocalDateTime.now().plusDays(1);
        List<Partido> creados = new ArrayList<>();
        for (int i = 0; i < 4; i += 2) {
            Partido p = new Partido(equiposNorm.get(i), equiposNorm.get(i+1), fechaBase.plusHours(i), "Sede Central", torneo);
            p.setTipoPartido("Semifinal");
            partidoRepository.save(p);
            creados.add(p);
            // crear asignaciones provisionales (pendientes)
            asignarArbitrosAlPartido(p);
        }

        // Crear final (sin equipos asignados, se resolverá tras semifinales)
        Partido finalP = new Partido("TBD", "TBD", fechaBase.plusDays(1), "Sede Central", torneo);
        finalP.setTipoPartido("Final");
        partidoRepository.save(finalP);
        asignarArbitrosAlPartido(finalP);
    }

    private void asignarArbitrosAlPartido(Partido partido) {
        List<Arbitro> arbitros = arbitroRepository.findAll();
        if (arbitros.isEmpty()) return;

        // Obtener tarifa por defecto (la primera activa)
        Optional<Tarifa> tarifaOpt = tarifaRepository.findAll().stream().findFirst();
        Tarifa tarifa = tarifaOpt.orElse(null);

        // Roles a asignar (intencionalmente simples)
        String[] roles = new String[] {"Principal", "Auxiliar", "Asistente", "Mesa"};

        for (int i = 0; i < Math.min(arbitros.size(), roles.length); i++) {
            Arbitro a = arbitros.get(i);
            Asignacion asig = new Asignacion(partido, a, tarifa != null ? tarifa : null);
            asig.setRolEspecifico(roles[i]);
            // por defecto queda PENDIENTE -> los árbitros deben aceptar manualmente
            asignacionRepository.save(asig);
        }
    }

    /**
     * Simula el siguiente partido no completado del torneo.
     * Solo se simula si todas las asignaciones del partido están en estado ACEPTADA.
     * Devuelve un mapa con el resultado y mensajes.
     */
    public Map<String, Object> simularSiguientePartido(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        // buscar el siguiente partido no completado ordenado por id
        Optional<Partido> siguienteOpt = torneo.getPartidos().stream()
                .filter(p -> p.getCompletado() == null || !p.getCompletado())
                .sorted(Comparator.comparing(Partido::getFechaCreacion))
                .findFirst();

        Map<String, Object> res = new HashMap<>();
        if (siguienteOpt.isEmpty()) {
            res.put("ok", false);
            res.put("message", "No hay partidos por simular en este torneo");
            return res;
        }

        Partido p = siguienteOpt.get();
        List<Asignacion> asignaciones = asignacionRepository.findByPartido(p);
        if (asignaciones.isEmpty()) {
            res.put("ok", false);
            res.put("message", "El partido no tiene asignaciones. No se puede simular.");
            return res;
        }

        // Verificar que todas las asignaciones estén aceptadas
        boolean todasAceptadas = asignaciones.stream().allMatch(a -> a.getEstado() == EstadoAsignacion.ACEPTADA);
        if (!todasAceptadas) {
            res.put("ok", false);
            res.put("message", "No todas las asignaciones están aceptadas. No se puede simular el partido.");
            res.put("partidoId", p.getId());
            res.put("asignacionesPendientes", asignaciones.stream().filter(a -> a.getEstado() != EstadoAsignacion.ACEPTADA).count());
            return res;
        }

        // Simular resultado simple: aleatorio entre 0-5 con ligera ventaja aleatoria
        Random rnd = new Random();
        int local = rnd.nextInt(6);
        int visitante = rnd.nextInt(6);
        // evitar empate en fases eliminatorias: desempatar aleatoriamente
        if (local == visitante) {
            if (rnd.nextBoolean()) local++;
            else visitante++;
        }

        p.completarPartido(local, visitante);
        partidoRepository.save(p);

        // completar asignaciones
        for (Asignacion a : asignaciones) {
            try { a.completar(); } catch (Exception ignored) {}
            asignacionRepository.save(a);
        }

        String ganador = local > visitante ? p.getEquipoLocal() : p.getEquipoVisitante();
        res.put("ok", true);
        res.put("partidoId", p.getId());
        res.put("equipoLocal", p.getEquipoLocal());
        res.put("equipoVisitante", p.getEquipoVisitante());
        res.put("marcadorLocal", local);
        res.put("marcadorVisitante", visitante);
        res.put("ganador", ganador);
        res.put("message", "Partido simulado");
        return res;
    }
}
