package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de predicción baseline para partidos y torneos.
 * Usa heurísticas simples basadas en datos históricos simulados.
 */
@Service
@Transactional(readOnly = true)
public class TorneoPredictionService {

    @Autowired
    private PartidoRepository partidoRepository;

    /**
     * Predice el resultado de un partido usando heurísticas simples.
     * 
     * @param equipoLocal Nombre del equipo local
     * @param equipoVisitante Nombre del equipo visitante
     * @param fase Fase del torneo (opcional)
     * @return Mapa con predicciones
     */
    public Map<String, Object> predecirPartido(String equipoLocal, String equipoVisitante, String fase) {
        // Obtener historial reciente de ambos equipos
        List<Partido> partidosLocal = obtenerPartidosRecientes(equipoLocal, 5);
        List<Partido> partidosVisitante = obtenerPartidosRecientes(equipoVisitante, 5);

        // Calcular estadísticas
        double promedioGolesLocal = calcularPromedioGoles(partidosLocal, equipoLocal);
        double promedioGolesVisitante = calcularPromedioGoles(partidosVisitante, equipoVisitante);
        double winRateLocal = calcularWinRate(partidosLocal, equipoLocal);
        double winRateVisitante = calcularWinRate(partidosVisitante, equipoVisitante);

        // Ventaja de local (+0.15 en probabilidad)
        double ventajaLocal = 0.15;
        
        // Calcular probabilidades (modelo simple baseline)
        double rawProbLocal = 0.5 + ventajaLocal + (winRateLocal - winRateVisitante) * 0.3;

        // Normalizar
        double pLocal = Math.max(0.1, Math.min(0.9, rawProbLocal));
        double pVisitante = 1.0 - pLocal;

        // Predecir marcadores esperados
        double expectedScoreLocal = promedioGolesLocal * 1.1; // factor local
        double expectedScoreVisitante = promedioGolesVisitante * 0.9; // factor visitante

        int predictedMargin = (int) Math.round(expectedScoreLocal - expectedScoreVisitante);
        String predictedWinner = expectedScoreLocal > expectedScoreVisitante ? equipoLocal : equipoVisitante;

        Map<String, Object> prediccion = new LinkedHashMap<>();
        prediccion.put("equipoLocal", equipoLocal);
        prediccion.put("equipoVisitante", equipoVisitante);
        prediccion.put("fase", fase);
        prediccion.put("p_local", Math.round(pLocal * 100.0) / 100.0);
        prediccion.put("p_visitante", Math.round(pVisitante * 100.0) / 100.0);
        prediccion.put("expected_score_local", Math.round(expectedScoreLocal * 10.0) / 10.0);
        prediccion.put("expected_score_visitante", Math.round(expectedScoreVisitante * 10.0) / 10.0);
        prediccion.put("predicted_winner", predictedWinner);
        prediccion.put("predicted_margin", predictedMargin);
        prediccion.put("modelo", "baseline_heuristic");

        return prediccion;
    }

    /**
     * Predice el campeón del torneo usando simulación Monte Carlo.
     * 
     * @param torneoId ID del torneo
     * @param numSimulaciones Número de simulaciones a realizar
     * @return Mapa con probabilidades por equipo
     */
    public Map<String, Object> predecirCampeon(Long torneoId, int numSimulaciones) {
        // Obtener partidos pendientes del torneo
        List<Partido> partidosPendientes = partidoRepository.findAll().stream()
                .filter(p -> p.getTorneo() != null && p.getTorneo().getId().equals(torneoId))
                .filter(p -> p.getCompletado() == null || !p.getCompletado())
                .sorted(Comparator.comparing(Partido::getFechaCreacion))
                .collect(Collectors.toList());

        if (partidosPendientes.isEmpty()) {
            return Map.of("error", "No hay partidos pendientes en el torneo");
        }

        // Extraer equipos únicos
        Set<String> equipos = new HashSet<>();
        for (Partido p : partidosPendientes) {
            if (!p.getEquipoLocal().equals("TBD")) equipos.add(p.getEquipoLocal());
            if (!p.getEquipoVisitante().equals("TBD")) equipos.add(p.getEquipoVisitante());
        }

        // Contador de victorias por equipo
        Map<String, Integer> victoriasSimuladas = new HashMap<>();
        for (String eq : equipos) {
            victoriasSimuladas.put(eq, 0);
        }

        Random rnd = new Random();
        
        // Simulación Monte Carlo
        for (int sim = 0; sim < numSimulaciones; sim++) {
            Map<String, String> resultadosPartidos = new HashMap<>();
            
            // Simular cada partido pendiente
            for (Partido p : partidosPendientes) {
                String local = p.getEquipoLocal();
                String visitante = p.getEquipoVisitante();
                
                // Saltar partidos con TBD (se resolverán en simulaciones)
                if (local.equals("TBD") || visitante.equals("TBD")) {
                    continue;
                }

                // Predecir con modelo baseline
                Map<String, Object> pred = predecirPartido(local, visitante, p.getTipoPartido());
                double pLocal = (Double) pred.get("p_local");
                
                // Simular resultado estocástico
                String ganador = rnd.nextDouble() < pLocal ? local : visitante;
                resultadosPartidos.put(p.getId().toString(), ganador);
            }

            // Determinar campeón (el ganador del último partido simulado o heurística)
            if (!resultadosPartidos.isEmpty()) {
                String campeon = resultadosPartidos.values().stream()
                        .findFirst()
                        .orElse(equipos.stream().findFirst().orElse(null));
                if (campeon != null) {
                    victoriasSimuladas.put(campeon, victoriasSimuladas.getOrDefault(campeon, 0) + 1);
                }
            }
        }

        // Calcular probabilidades
        Map<String, Double> probabilidades = new LinkedHashMap<>();
        for (String eq : equipos) {
            double prob = victoriasSimuladas.getOrDefault(eq, 0) / (double) numSimulaciones;
            probabilidades.put(eq, Math.round(prob * 100.0) / 100.0);
        }

        // Ordenar por probabilidad descendente
        List<Map.Entry<String, Double>> sorted = probabilidades.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .toList();

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("torneoId", torneoId);
        resultado.put("numSimulaciones", numSimulaciones);
        resultado.put("equipos", equipos.size());
        resultado.put("probabilidades", sorted.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new)));
        resultado.put("favoritoPrediccion", sorted.isEmpty() ? null : sorted.get(0).getKey());
        resultado.put("probabilidadFavorito", sorted.isEmpty() ? 0.0 : sorted.get(0).getValue());
        resultado.put("modelo", "monte_carlo_baseline");

        return resultado;
    }

    private List<Partido> obtenerPartidosRecientes(String equipo, int limit) {
        return partidoRepository.findAll().stream()
                .filter(p -> p.getCompletado() != null && p.getCompletado())
                .filter(p -> p.getEquipoLocal().equals(equipo) || p.getEquipoVisitante().equals(equipo))
                .sorted(Comparator.comparing(Partido::getFechaPartido).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calcularPromedioGoles(List<Partido> partidos, String equipo) {
        if (partidos.isEmpty()) return 1.5; // default
        
        double sumaGoles = 0.0;
        for (Partido p : partidos) {
            if (p.getMarcadorLocal() == null || p.getMarcadorVisitante() == null) continue;
            if (p.getEquipoLocal().equals(equipo)) {
                sumaGoles += p.getMarcadorLocal();
            } else {
                sumaGoles += p.getMarcadorVisitante();
            }
        }
        return partidos.isEmpty() ? 1.5 : sumaGoles / partidos.size();
    }

    private double calcularWinRate(List<Partido> partidos, String equipo) {
        if (partidos.isEmpty()) return 0.5;
        
        long victorias = partidos.stream().filter(p -> {
            if (p.getMarcadorLocal() == null || p.getMarcadorVisitante() == null) return false;
            if (p.getEquipoLocal().equals(equipo)) {
                return p.getMarcadorLocal() > p.getMarcadorVisitante();
            } else {
                return p.getMarcadorVisitante() > p.getMarcadorLocal();
            }
        }).count();
        
        return (double) victorias / partidos.size();
    }
}
