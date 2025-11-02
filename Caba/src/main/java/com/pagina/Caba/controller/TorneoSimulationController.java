package com.pagina.Caba.controller;

import com.pagina.Caba.service.TorneoSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/torneos/simulacion")
public class TorneoSimulationController {

    @Autowired
    private TorneoSimulationService simulationService;

    @PostMapping("/generar/{torneoId}")
    public ResponseEntity<?> generarBracket(@PathVariable Long torneoId, @RequestBody(required = false) List<String> equipos) {
        try {
            Map<String, Object> resultado = simulationService.generarBracket(torneoId, equipos);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/simular-siguiente/{torneoId}")
    public ResponseEntity<?> simularSiguiente(@PathVariable Long torneoId) {
        try {
            Map<String, Object> res = simulationService.simularSiguientePartido(torneoId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("ok", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/simular-partido/{partidoId}")
    public ResponseEntity<?> simularPartido(@PathVariable Long partidoId) {
        try {
            Map<String, Object> res = simulationService.simularPartido(partidoId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("ok", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/estado/{torneoId}")
    public ResponseEntity<?> obtenerEstado(@PathVariable Long torneoId) {
        try {
            Map<String, Object> estado = simulationService.obtenerEstadoTorneo(torneoId);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("ok", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/configurar-semifinales/{torneoId}")
    public ResponseEntity<?> configurarSemifinales(@PathVariable Long torneoId, @RequestBody List<String> equipos) {
        try {
            Map<String, Object> resultado = simulationService.configurarSemifinales(torneoId, equipos);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/configurar-final/{torneoId}")
    public ResponseEntity<?> configurarFinal(@PathVariable Long torneoId, @RequestBody List<String> equipos) {
        try {
            Map<String, Object> resultado = simulationService.configurarFinal(torneoId, equipos);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "error", e.getMessage()));
        }
    }
}
