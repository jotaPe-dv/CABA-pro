package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private AsignacionService asignacionService;
    
    @GetMapping("/asignaciones")
    public ResponseEntity<Map<String, Object>> debugAsignaciones() {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            List<Asignacion> asignaciones = asignacionService.obtenerTodas();
            debug.put("totalAsignaciones", asignaciones.size());
            debug.put("asignaciones", asignaciones.stream().map(a -> {
                Map<String, Object> asignacionInfo = new HashMap<>();
                asignacionInfo.put("id", a.getId());
                asignacionInfo.put("rolEspecifico", a.getRolEspecifico());
                asignacionInfo.put("estado", a.getEstado());
                asignacionInfo.put("partidoId", a.getPartido() != null ? a.getPartido().getId() : "NULL");
                asignacionInfo.put("arbitroNombre", a.getArbitro() != null ? a.getArbitro().getNombre() : "NULL");
                asignacionInfo.put("fechaAsignacion", a.getFechaAsignacion());
                return asignacionInfo;
            }).toList());
            
            debug.put("success", true);
        } catch (Exception e) {
            debug.put("success", false);
            debug.put("error", e.getMessage());
            debug.put("stackTrace", e.getStackTrace());
        }
        
        return ResponseEntity.ok(debug);
    }
}