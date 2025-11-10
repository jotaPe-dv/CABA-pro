package com.pagina.Caba.api;

import com.pagina.Caba.dto.ArbitroDto;
import com.pagina.Caba.dto.AsignacionDto;
import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API REST para el perfil del árbitro autenticado
 * Todos los endpoints devuelven información del usuario logueado (desde el JWT)
 */
@RestController
@RequestMapping("/api/arbitro")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:5173"})
public class ArbitroProfileApiController {

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private LiquidacionService liquidacionService;

    /**
     * GET /api/arbitro/perfil
     * Obtiene el perfil completo del árbitro autenticado
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerMiPerfil() {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            ArbitroDto dto = arbitroService.convertirADto(arbitroOpt.get());
            return ResponseEntity.ok(dto);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener perfil: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/arbitro/perfil
     * Actualiza el perfil del árbitro autenticado
     * Solo puede actualizar: teléfono, dirección, disponibilidad
     */
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarMiPerfil(@RequestBody ArbitroDto arbitroDto) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            Arbitro arbitro = arbitroOpt.get();
            
            // Solo permitir actualizar estos campos
            if (arbitroDto.getTelefono() != null) {
                arbitro.setTelefono(arbitroDto.getTelefono());
            }
            if (arbitroDto.getDireccion() != null) {
                arbitro.setDireccion(arbitroDto.getDireccion());
            }
            if (arbitroDto.getFotoUrl() != null) {
                arbitro.setFotoUrl(arbitroDto.getFotoUrl());
            }
            
            Arbitro arbitroActualizado = arbitroService.actualizar(arbitro.getId(), arbitro);
            ArbitroDto dto = arbitroService.convertirADto(arbitroActualizado);
            
            return ResponseEntity.ok(Map.of(
                "message", "Perfil actualizado exitosamente",
                "arbitro", dto
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar perfil: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/arbitro/disponibilidad
     * Toggle de disponibilidad del árbitro autenticado
     */
    @PutMapping("/disponibilidad")
    public ResponseEntity<?> toggleDisponibilidad(@RequestBody Map<String, Boolean> payload) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            Arbitro arbitro = arbitroOpt.get();
            Boolean nuevaDisponibilidad = payload.get("disponible");
            
            if (nuevaDisponibilidad == null) {
                // Toggle automático
                arbitro.setDisponible(!arbitro.isDisponible());
            } else {
                arbitro.setDisponible(nuevaDisponibilidad);
            }
            
            Arbitro arbitroActualizado = arbitroService.actualizar(arbitro.getId(), arbitro);
            
            return ResponseEntity.ok(Map.of(
                "message", arbitroActualizado.isDisponible() ? 
                    "Ahora estás disponible para asignaciones" : 
                    "Ahora estás NO disponible para asignaciones",
                "disponible", arbitroActualizado.isDisponible()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al cambiar disponibilidad: " + e.getMessage()));
        }
    }

    /**
     * GET /api/arbitro/mis-asignaciones
     * Obtiene todas las asignaciones del árbitro autenticado
     */
    @GetMapping("/mis-asignaciones")
    public ResponseEntity<?> obtenerMisAsignaciones(
            @RequestParam(required = false) String estado) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            List<AsignacionDto> asignaciones;
            
            if (estado != null && !estado.isEmpty()) {
                // Filtrar por estado
                asignaciones = asignacionService.obtenerPorArbitroYEstado(
                    arbitroOpt.get().getId(), 
                    estado
                );
            } else {
                // Todas las asignaciones
                asignaciones = asignacionService.obtenerPorArbitro(arbitroOpt.get().getId());
            }
            
            return ResponseEntity.ok(Map.of(
                "total", asignaciones.size(),
                "asignaciones", asignaciones
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener asignaciones: " + e.getMessage()));
        }
    }

    /**
     * GET /api/arbitro/mis-liquidaciones
     * Obtiene todas las liquidaciones del árbitro autenticado
     */
    @GetMapping("/mis-liquidaciones")
    public ResponseEntity<?> obtenerMisLiquidaciones(
            @RequestParam(required = false) String estado) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            List<LiquidacionDto> liquidaciones;
            
            if (estado != null && !estado.isEmpty()) {
                // Filtrar por estado
                liquidaciones = liquidacionService.obtenerPorArbitroYEstado(
                    arbitroOpt.get().getId(),
                    estado
                );
            } else {
                // Todas las liquidaciones
                liquidaciones = liquidacionService.obtenerPorArbitro(arbitroOpt.get().getId());
            }
            
            return ResponseEntity.ok(Map.of(
                "total", liquidaciones.size(),
                "liquidaciones", liquidaciones
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener liquidaciones: " + e.getMessage()));
        }
    }

    /**
     * POST /api/arbitro/asignacion/{id}/aceptar
     * Aceptar una asignación
     */
    @PostMapping("/asignacion/{id}/aceptar")
    public ResponseEntity<?> aceptarAsignacion(@PathVariable Long id) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            // Verificar que la asignación pertenezca al árbitro
            AsignacionDto asignacion = asignacionService.obtenerAsignacionDto(id);
            
            if (!asignacion.getArbitro().getId().equals(arbitroOpt.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para aceptar esta asignación"));
            }
            
            AsignacionDto asignacionActualizada = asignacionService.aceptarAsignacionDto(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "Asignación aceptada exitosamente",
                "asignacion", asignacionActualizada
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al aceptar asignación: " + e.getMessage()));
        }
    }

    /**
     * POST /api/arbitro/asignacion/{id}/rechazar
     * Rechazar una asignación
     */
    @PostMapping("/asignacion/{id}/rechazar")
    public ResponseEntity<?> rechazarAsignacion(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            // Verificar que la asignación pertenezca al árbitro
            AsignacionDto asignacion = asignacionService.obtenerAsignacionDto(id);
            
            if (!asignacion.getArbitro().getId().equals(arbitroOpt.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para rechazar esta asignación"));
            }
            
            String comentario = payload != null ? payload.get("comentario") : "Rechazada por el árbitro";
            
            AsignacionDto asignacionActualizada = asignacionService.rechazarAsignacionDto(id, comentario);
            
            return ResponseEntity.ok(Map.of(
                "message", "Asignación rechazada",
                "asignacion", asignacionActualizada
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al rechazar asignación: " + e.getMessage()));
        }
    }

    /**
     * Método auxiliar para obtener el email del usuario autenticado
     */
    private String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // El nombre es el email en nuestro caso
    }
}
