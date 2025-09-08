package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.model.UserPrincipal;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arbitro/asignaciones")
public class ArbitroAsignacionController {
    
    @Autowired
    private AsignacionService asignacionService;
    
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarAsignacion(@PathVariable Long id, Authentication auth) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            asignacionService.aceptarAsignacion(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al aceptar asignación: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarAsignacion(@PathVariable Long id, Authentication auth) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            asignacionService.rechazarAsignacion(id, userPrincipal.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al rechazar asignación: " + e.getMessage());
        }
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<Asignacion>> getAsignacionesPendientes(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        List<Asignacion> asignaciones = asignacionService
            .findByArbitroIdAndEstado(userPrincipal.getId(), EstadoAsignacion.PENDIENTE);
        return ResponseEntity.ok(asignaciones);
    }
    
    @GetMapping("/historial")
    public ResponseEntity<List<Asignacion>> getHistorialAsignaciones(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        List<Asignacion> asignaciones = asignacionService.findByArbitroId(userPrincipal.getId());
        return ResponseEntity.ok(asignaciones);
    }
}
