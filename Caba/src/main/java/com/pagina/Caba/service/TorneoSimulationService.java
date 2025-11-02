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
     * Genera la estructura de play-offs de baloncesto: Cuartos (4 partidos), Semifinales (2 partidos), Final (1 partido)
     * Los partidos se crean en diferentes días para permitir simulación progresiva.
     * @return Map con estadísticas de creación
     */
    public Map<String, Object> generarBracket(Long torneoId, List<String> equipos) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        if (equipos == null || equipos.size() != 8) {
            throw new IllegalArgumentException("Se requieren exactamente 8 equipos para los play-offs");
        }

        LocalDateTime fechaBase = LocalDateTime.now().plusDays(1);
        int partidosCreados = 0;
        int asignacionesCreadas = 0;

        // FASE 1: Cuartos de Final (4 partidos en 4 días diferentes)
        for (int i = 0; i < 4; i++) {
            Partido p = new Partido(
                equipos.get(i * 2),
                equipos.get(i * 2 + 1),
                fechaBase.plusDays(i),  // Cada partido en un día diferente
                "Estadio Central",
                torneo
            );
            p.setTipoPartido("Cuartos de Final");
            partidoRepository.save(p);
            partidosCreados++;
            asignacionesCreadas += asignarArbitrosAlPartido(p);
        }

        // FASE 2: Semifinales (2 partidos con equipos TBD)
        LocalDateTime fechaSemis = fechaBase.plusDays(5);  // 5 días después
        for (int i = 0; i < 2; i++) {
            Partido p = new Partido("TBD", "TBD", fechaSemis.plusDays(i), "Estadio Central", torneo);
            p.setTipoPartido("Semifinales");
            partidoRepository.save(p);
            partidosCreados++;
            asignacionesCreadas += asignarArbitrosAlPartido(p);
        }

        // FASE 3: Final (1 partido con equipos TBD)
        LocalDateTime fechaFinal = fechaBase.plusDays(8);  // 8 días después del inicio
        Partido finalP = new Partido("TBD", "TBD", fechaFinal, "Estadio Nacional", torneo);
        finalP.setTipoPartido("Final");
        partidoRepository.save(finalP);
        partidosCreados++;
        asignacionesCreadas += asignarArbitrosAlPartido(finalP);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("partidosCreados", partidosCreados);
        resultado.put("asignacionesCreadas", asignacionesCreadas);
        resultado.put("fases", Map.of(
            "cuartos", 4,
            "semifinales", 2,
            "final", 1
        ));
        return resultado;
    }

    private int asignarArbitrosAlPartido(Partido partido) {
        List<Arbitro> arbitros = arbitroRepository.findAll();
        if (arbitros.isEmpty()) return 0;

        // Obtener tarifa por defecto (la primera activa)
        Optional<Tarifa> tarifaOpt = tarifaRepository.findAll().stream().findFirst();
        Tarifa tarifa = tarifaOpt.orElse(null);

        // Roles a asignar (intencionalmente simples)
        String[] roles = new String[] {"Principal", "Auxiliar", "Asistente", "Mesa"};

        int count = 0;
        for (int i = 0; i < Math.min(arbitros.size(), roles.length); i++) {
            Arbitro a = arbitros.get(i);
            Asignacion asig = new Asignacion(partido, a, tarifa != null ? tarifa : null);
            asig.setRolEspecifico(roles[i]);
            // por defecto queda PENDIENTE -> los árbitros deben aceptar manualmente
            asignacionRepository.save(asig);
            count++;
        }
        return count;
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

        // Simular resultado REALISTA de baloncesto (rangos 70-120 puntos)
        Random rnd = new Random();
        
        // Base: puntaje promedio entre 80-110 puntos
        int baseLocal = 80 + rnd.nextInt(30);  // 80-110
        int baseVisitante = 80 + rnd.nextInt(30);  // 80-110
        
        // Variación adicional: ±10 puntos para hacer más interesante
        int variacionLocal = rnd.nextInt(21) - 10;  // -10 a +10
        int variacionVisitante = rnd.nextInt(21) - 10;  // -10 a +10
        
        int local = Math.max(70, Math.min(120, baseLocal + variacionLocal));
        int visitante = Math.max(70, Math.min(120, baseVisitante + variacionVisitante));
        
        // Evitar empate en fases eliminatorias (muy raro en baloncesto, pero posible)
        if (local == visitante) {
            if (rnd.nextBoolean()) local += rnd.nextInt(5) + 1;  // +1 a +5
            else visitante += rnd.nextInt(5) + 1;
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

    /**
     * Simula un partido específico por ID
     */
    @Transactional
    public Map<String, Object> simularPartido(Long partidoId) {
        Partido p = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado: " + partidoId));

        Map<String, Object> res = new HashMap<>();
        
        // Verificar que el torneo no esté cerrado
        Torneo torneo = p.getTorneo();
        if (torneo.estaCerrado()) {
            res.put("ok", false);
            res.put("message", "El torneo ya está cerrado. Campeón: " + torneo.getCampeon());
            return res;
        }

        // Verificar que no esté ya completado
        if (p.getCompletado() != null && p.getCompletado()) {
            res.put("ok", false);
            res.put("message", "El partido ya fue simulado");
            return res;
        }

        // Verificar que no tenga equipos TBD
        if ("TBD".equals(p.getEquipoLocal()) || "TBD".equals(p.getEquipoVisitante())) {
            res.put("ok", false);
            res.put("message", "El partido tiene equipos sin definir (TBD). Complete la fase anterior primero.");
            return res;
        }

        List<Asignacion> asignaciones = asignacionRepository.findByPartido(p);
        
        // Verificar que el partido tenga asignaciones
        if (asignaciones.isEmpty()) {
            res.put("ok", false);
            res.put("message", "El partido no tiene asignaciones. No se puede simular.");
            return res;
        }
        
        // Verificar que todas las asignaciones estén aceptadas (los 3 árbitros)
        boolean todasAceptadas = asignaciones.stream().allMatch(a -> a.getEstado() == EstadoAsignacion.ACEPTADA);
        if (!todasAceptadas) {
            long pendientes = asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.PENDIENTE).count();
            long rechazadas = asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.RECHAZADA).count();
            
            String mensaje = "No se puede simular el partido. ";
            if (rechazadas > 0) {
                mensaje += rechazadas + " árbitro(s) rechazaron la asignación. ";
            }
            if (pendientes > 0) {
                mensaje += pendientes + " árbitro(s) aún no han respondido.";
            }
            
            res.put("ok", false);
            res.put("message", mensaje);
            res.put("partidoId", p.getId());
            res.put("asignacionesPendientes", pendientes);
            res.put("asignacionesRechazadas", rechazadas);
            return res;
        }

        // Simular resultado REALISTA de baloncesto (rangos 70-120 puntos)
        Random rnd = new Random();
        
        int baseLocal = 80 + rnd.nextInt(30);  // 80-110
        int baseVisitante = 80 + rnd.nextInt(30);  // 80-110
        
        int variacionLocal = rnd.nextInt(21) - 10;  // -10 a +10
        int variacionVisitante = rnd.nextInt(21) - 10;  // -10 a +10
        
        int local = Math.max(70, Math.min(120, baseLocal + variacionLocal));
        int visitante = Math.max(70, Math.min(120, baseVisitante + variacionVisitante));
        
        // Evitar empate
        if (local == visitante) {
            if (rnd.nextBoolean()) local += rnd.nextInt(5) + 1;
            else visitante += rnd.nextInt(5) + 1;
        }

        p.completarPartido(local, visitante);
        partidoRepository.save(p);

        // Completar asignaciones
        for (Asignacion a : asignaciones) {
            try { a.completar(); } catch (Exception ignored) {}
            asignacionRepository.save(a);
        }

        String ganador = local > visitante ? p.getEquipoLocal() : p.getEquipoVisitante();
        
        // Si es la FINAL, cerrar automáticamente el torneo
        if ("Final".equalsIgnoreCase(p.getTipoPartido())) {
            torneo.cerrarTorneo(ganador);
            torneoRepository.save(torneo);
            res.put("torneoCerrado", true);
            res.put("campeon", ganador);
        } else {
            res.put("torneoCerrado", false);
        }
        
        res.put("ok", true);
        res.put("partidoId", p.getId());
        res.put("equipoLocal", p.getEquipoLocal());
        res.put("equipoVisitante", p.getEquipoVisitante());
        res.put("marcadorLocal", local);
        res.put("marcadorVisitante", visitante);
        res.put("ganador", ganador);
        res.put("tipoPartido", p.getTipoPartido());
        res.put("message", "Partido simulado exitosamente");
        return res;
    }

    /**
     * Obtiene el estado actual del torneo: qué fase está activa y qué partidos están pendientes/completados
     */
    public Map<String, Object> obtenerEstadoTorneo(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        List<Partido> partidos = torneo.getPartidos().stream()
                .sorted(Comparator.comparing(Partido::getFechaPartido))
                .toList();

        // Agrupar por fase
        Map<String, List<Map<String, Object>>> fases = new HashMap<>();
        fases.put("Cuartos de Final", new ArrayList<>());
        fases.put("Semifinales", new ArrayList<>());
        fases.put("Final", new ArrayList<>());

        String faseActual = null;
        int partidosPendientes = 0;

        for (Partido p : partidos) {
            String fase = p.getTipoPartido();
            Map<String, Object> partidoInfo = new HashMap<>();
            partidoInfo.put("id", p.getId());
            partidoInfo.put("equipoLocal", p.getEquipoLocal());
            partidoInfo.put("equipoVisitante", p.getEquipoVisitante());
            partidoInfo.put("fecha", p.getFechaPartido().toString());
            partidoInfo.put("completado", p.getCompletado());
            partidoInfo.put("marcadorLocal", p.getMarcadorLocal());
            partidoInfo.put("marcadorVisitante", p.getMarcadorVisitante());
            
            if (p.getCompletado() && p.getMarcadorLocal() != null && p.getMarcadorVisitante() != null) {
                partidoInfo.put("ganador", p.getMarcadorLocal() > p.getMarcadorVisitante() 
                    ? p.getEquipoLocal() : p.getEquipoVisitante());
            }

            fases.get(fase).add(partidoInfo);

            // Determinar fase actual (la primera con partidos pendientes)
            if (!p.getCompletado() && faseActual == null) {
                faseActual = fase;
            }
            if (!p.getCompletado()) {
                partidosPendientes++;
            }
        }

        // Verificar si una fase está completa y la siguiente necesita configuración
        boolean cuartosCompletos = fases.get("Cuartos de Final").stream()
                .allMatch(p -> (Boolean) p.get("completado"));
        boolean semisCompletas = fases.get("Semifinales").stream()
                .allMatch(p -> (Boolean) p.get("completado"));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("torneoId", torneoId);
        resultado.put("torneoNombre", torneo.getNombre());
        resultado.put("faseActual", faseActual != null ? faseActual : "Finalizado");
        resultado.put("partidosPendientes", partidosPendientes);
        resultado.put("fases", fases);
        resultado.put("cuartosCompletos", cuartosCompletos);
        resultado.put("semisCompletas", semisCompletas);
        
        // Obtener ganadores de cuartos si están completos
        if (cuartosCompletos) {
            List<String> ganadoresCuartos = fases.get("Cuartos de Final").stream()
                    .map(p -> (String) p.get("ganador"))
                    .toList();
            resultado.put("ganadoresCuartos", ganadoresCuartos);
        }

        // Obtener ganadores de semis si están completas
        if (semisCompletas) {
            List<String> ganadoresSemis = fases.get("Semifinales").stream()
                    .map(p -> (String) p.get("ganador"))
                    .toList();
            resultado.put("ganadoresSemis", ganadoresSemis);
        }

        return resultado;
    }

    /**
     * Configura las semifinales con los 4 ganadores de cuartos de final
     */
    @Transactional
    public Map<String, Object> configurarSemifinales(Long torneoId, List<String> equipos) {
        if (equipos == null || equipos.size() != 4) {
            throw new IllegalArgumentException("Se requieren exactamente 4 equipos ganadores de cuartos");
        }

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        // Obtener partidos de semifinales (los que tienen equipos TBD)
        List<Partido> semifinales = torneo.getPartidos().stream()
                .filter(p -> "Semifinales".equals(p.getTipoPartido()))
                .filter(p -> "TBD".equals(p.getEquipoLocal()))
                .sorted(Comparator.comparing(Partido::getFechaPartido))
                .toList();

        if (semifinales.size() != 2) {
            throw new IllegalStateException("No se encontraron 2 semifinales configurables");
        }

        // Configurar Semi 1: equipos[0] vs equipos[1]
        Partido semi1 = semifinales.get(0);
        semi1.setEquipoLocal(equipos.get(0));
        semi1.setEquipoVisitante(equipos.get(1));
        partidoRepository.save(semi1);

        // Configurar Semi 2: equipos[2] vs equipos[3]
        Partido semi2 = semifinales.get(1);
        semi2.setEquipoLocal(equipos.get(2));
        semi2.setEquipoVisitante(equipos.get(3));
        partidoRepository.save(semi2);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ok", true);
        resultado.put("message", "Semifinales configuradas correctamente");
        resultado.put("semi1", Map.of(
            "id", semi1.getId(),
            "equipoLocal", semi1.getEquipoLocal(),
            "equipoVisitante", semi1.getEquipoVisitante()
        ));
        resultado.put("semi2", Map.of(
            "id", semi2.getId(),
            "equipoLocal", semi2.getEquipoLocal(),
            "equipoVisitante", semi2.getEquipoVisitante()
        ));
        return resultado;
    }

    /**
     * Configura la final con los 2 ganadores de semifinales
     */
    @Transactional
    public Map<String, Object> configurarFinal(Long torneoId, List<String> equipos) {
        if (equipos == null || equipos.size() != 2) {
            throw new IllegalArgumentException("Se requieren exactamente 2 equipos ganadores de semifinales");
        }

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + torneoId));

        // Obtener partido de final (el que tiene equipos TBD)
        Partido finalPartido = torneo.getPartidos().stream()
                .filter(p -> "Final".equals(p.getTipoPartido()))
                .filter(p -> "TBD".equals(p.getEquipoLocal()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró la final configurable"));

        finalPartido.setEquipoLocal(equipos.get(0));
        finalPartido.setEquipoVisitante(equipos.get(1));
        partidoRepository.save(finalPartido);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ok", true);
        resultado.put("message", "Final configurada correctamente");
        resultado.put("final", Map.of(
            "id", finalPartido.getId(),
            "equipoLocal", finalPartido.getEquipoLocal(),
            "equipoVisitante", finalPartido.getEquipoVisitante()
        ));
        return resultado;
    }
}
