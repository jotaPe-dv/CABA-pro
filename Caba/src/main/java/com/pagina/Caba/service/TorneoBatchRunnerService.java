package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Servicio para generar múltiples torneos simulados automáticamente (batch).
 * Útil para crear datasets de entrenamiento.
 */
@Service
@Transactional
public class TorneoBatchRunnerService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private TorneoSimulationService simulationService;

    @Autowired
    private AsignacionRepository asignacionRepository;

    /**
     * Genera N torneos con brackets completos, acepta asignaciones automáticamente
     * y simula todos los partidos posibles.
     * 
     * @param numTorneos Número de torneos a crear
     * @param autoAceptarAsignaciones Si true, auto-acepta todas las asignaciones para permitir simulación
     * @return Resumen de la operación
     */
    public Map<String, Object> generarTorneosAutomaticos(int numTorneos, boolean autoAceptarAsignaciones) {
        List<String> equiposBase = Arrays.asList("Tigres FC", "Leones SC", "Águilas United", "Pumas City");
        
        Map<String, Object> resultado = new LinkedHashMap<>();
        List<Long> torneosCreados = new ArrayList<>();
        int partidosSimulados = 0;

        Administrador admin = administradorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay administradores en el sistema"));

        for (int i = 0; i < numTorneos; i++) {
            // Crear torneo
            Torneo torneo = new Torneo();
            torneo.setNombre("Torneo Simulado #" + (i + 1));
            torneo.setDescripcion("Torneo generado automáticamente para entrenamiento de IA");
            torneo.setFechaInicio(LocalDate.now().plusDays(i * 7));
            torneo.setFechaFin(LocalDate.now().plusDays(i * 7 + 5));
            torneo.setActivo(true);
            torneo.setAdministrador(admin);
            Torneo torneoCreado = torneoRepository.save(torneo);
            final Long torneoId = torneoCreado.getId();

            // Generar bracket
            List<String> equiposVariados = variarEquipos(equiposBase, i);
            simulationService.generarBracket(torneoId, equiposVariados);

            // Auto-aceptar asignaciones si está habilitado
            if (autoAceptarAsignaciones) {
                List<Asignacion> asignaciones = asignacionRepository.findAll().stream()
                        .filter(a -> a.getPartido() != null && a.getPartido().getTorneo().getId().equals(torneoId))
                        .filter(a -> a.getEstado() == EstadoAsignacion.PENDIENTE)
                        .toList();
                
                for (Asignacion a : asignaciones) {
                    try {
                        a.aceptar();
                        asignacionRepository.save(a);
                    } catch (Exception ignored) {}
                }
            }

            // Simular partidos del torneo
            int intentos = 0;
            int maxIntentos = 10; // Máximo de partidos en un bracket simple
            while (intentos < maxIntentos) {
                Map<String, Object> res = simulationService.simularSiguientePartido(torneoId);
                if (res.get("ok") == Boolean.TRUE) {
                    partidosSimulados++;
                    intentos++;
                } else {
                    // No hay más partidos simulables
                    break;
                }
            }

            torneosCreados.add(torneoId);
        }

        resultado.put("torneosCreados", torneosCreados);
        resultado.put("numTorneos", numTorneos);
        resultado.put("partidosSimulados", partidosSimulados);
        resultado.put("autoAceptarAsignaciones", autoAceptarAsignaciones);
        return resultado;
    }

    private List<String> variarEquipos(List<String> base, int seed) {
        List<String> variados = new ArrayList<>(base);
        // Pequeña variación en nombres para dar diversidad
        String suffix = String.valueOf((char) ('A' + (seed % 26)));
        variados.set(0, variados.get(0) + " " + suffix);
        return variados;
    }
}
