package com.pagina.Caba.controller;

import com.pagina.Caba.service.TorneoBatchRunnerService;
import com.pagina.Caba.service.TorneoDataExportService;
import com.pagina.Caba.service.TorneoPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * Controlador REST para funcionalidad de IA: generación batch, exportación de datos y predicciones.
 */
@RestController
@RequestMapping("/api/torneos/ai")
public class TorneoAIController {

    @Autowired
    private TorneoBatchRunnerService batchRunner;

    @Autowired
    private TorneoDataExportService dataExport;

    @Autowired
    private TorneoPredictionService predictionService;

    /**
     * Genera N torneos automáticamente con simulación completa.
     * Ejemplo: POST /api/torneos/ai/batch?numTorneos=10&autoAceptar=true
     */
    @PostMapping("/batch")
    public ResponseEntity<?> generarTorneosBatch(
            @RequestParam(defaultValue = "5") int numTorneos,
            @RequestParam(defaultValue = "true") boolean autoAceptar) {
        try {
            Map<String, Object> resultado = batchRunner.generarTorneosAutomaticos(numTorneos, autoAceptar);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Exporta partidos completados a JSONL.
     * Ejemplo: GET /api/torneos/ai/export?outputPath=data/torneos.jsonl
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportarDatos(@RequestParam(defaultValue = "data/torneos_export.jsonl") String outputPath) {
        try {
            String resultado = dataExport.exportarPartidosCompletados(outputPath);
            return ResponseEntity.ok(Map.of("message", resultado, "path", outputPath));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al exportar: " + e.getMessage()));
        }
    }

    /**
     * Exporta partidos de un torneo específico.
     * Ejemplo: GET /api/torneos/ai/export/1?outputPath=data/torneo1.jsonl
     */
    @GetMapping("/export/{torneoId}")
    public ResponseEntity<?> exportarTorneo(
            @PathVariable Long torneoId,
            @RequestParam(defaultValue = "data/torneo_export.jsonl") String outputPath) {
        try {
            String resultado = dataExport.exportarPartidosDeTorneo(torneoId, outputPath);
            return ResponseEntity.ok(Map.of("message", resultado, "path", outputPath));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al exportar: " + e.getMessage()));
        }
    }

    /**
     * Obtiene estadísticas de datos exportables.
     * Ejemplo: GET /api/torneos/ai/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        Map<String, Object> stats = dataExport.obtenerEstadisticasExportacion();
        return ResponseEntity.ok(stats);
    }

    /**
     * Predice el resultado de un partido.
     * Ejemplo: POST /api/torneos/ai/predict
     * Body: {"equipoLocal":"Equipo A","equipoVisitante":"Equipo B","fase":"Semifinal"}
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predecirPartido(@RequestBody Map<String, String> request) {
        String local = request.get("equipoLocal");
        String visitante = request.get("equipoVisitante");
        String fase = request.getOrDefault("fase", "Regular");

        if (local == null || visitante == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Equipos requeridos"));
        }

        Map<String, Object> prediccion = predictionService.predecirPartido(local, visitante, fase);
        return ResponseEntity.ok(prediccion);
    }

    /**
     * Predice el campeón del torneo usando Monte Carlo.
     * Ejemplo: GET /api/torneos/ai/predict-champion/1?simulations=1000
     */
    @GetMapping("/predict-champion/{torneoId}")
    public ResponseEntity<?> predecirCampeon(
            @PathVariable Long torneoId,
            @RequestParam(defaultValue = "1000") int simulations) {
        try {
            Map<String, Object> prediccion = predictionService.predecirCampeon(torneoId, simulations);
            return ResponseEntity.ok(prediccion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
