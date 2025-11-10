package com.pagina.Caba.api;

import com.pagina.Caba.dto.AsignacionDto;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST para gestión de Asignaciones
 * 
 * Proporciona endpoints para:
 * - Consultar asignaciones
 * - Aceptar/Rechazar asignaciones (árbitros)
 * - Crear/Modificar asignaciones (admin)
 * - Consultar estadísticas
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "Operaciones de gestión de asignaciones de árbitros a partidos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AsignacionApiController {

    @Autowired
    private AsignacionService asignacionService;

    /**
     * Lista todas las asignaciones
     * 
     * @return Lista de asignaciones
     */
    @GetMapping
    @Operation(summary = "Listar todas las asignaciones", 
               description = "Obtiene la lista completa de asignaciones en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                     content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = AsignacionDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AsignacionDto>> listarAsignaciones(
            @Parameter(description = "Filtrar por estado") 
            @RequestParam(required = false) EstadoAsignacion estado) {
        
        List<Asignacion> asignaciones = asignacionService.obtenerTodas();
        
        // Aplicar filtro por estado si se proporciona
        if (estado != null) {
            asignaciones = asignaciones.stream()
                    .filter(a -> a.getEstado() == estado)
                    .collect(Collectors.toList());
        }
        
        List<AsignacionDto> dtos = asignaciones.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene una asignación por ID
     * 
     * @param id ID de la asignación
     * @return Datos de la asignación
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignación por ID", 
               description = "Obtiene los datos completos de una asignación específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asignación encontrada",
                     content = @Content(schema = @Schema(implementation = AsignacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<AsignacionDto> obtenerAsignacion(
            @Parameter(description = "ID de la asignación", required = true) 
            @PathVariable Long id) {
        
        Asignacion asignacion = asignacionService.obtenerPorId(id).orElse(null);
        if (asignacion == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertirADto(asignacion));
    }

    /**
     * Acepta una asignación
     * 
     * @param id ID de la asignación
     * @return Asignación actualizada
     */
    @PostMapping("/{id}/aceptar")
    @Operation(summary = "Aceptar asignación", 
               description = "El árbitro acepta una asignación pendiente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asignación aceptada",
                     content = @Content(schema = @Schema(implementation = AsignacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
        @ApiResponse(responseCode = "400", description = "La asignación no puede ser aceptada (ya fue procesada)"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<?> aceptarAsignacion(
            @Parameter(description = "ID de la asignación", required = true) 
            @PathVariable Long id) {
        
        Asignacion asignacion = asignacionService.obtenerPorId(id).orElse(null);
        if (asignacion == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar que está pendiente
        if (asignacion.getEstado() != EstadoAsignacion.PENDIENTE) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La asignación no está pendiente");
            error.put("estadoActual", asignacion.getEstado().toString());
            return ResponseEntity.badRequest().body(error);
        }
        
        asignacion.setEstado(EstadoAsignacion.ACEPTADA);
        asignacion = asignacionService.guardar(asignacion);
        
        return ResponseEntity.ok(convertirADto(asignacion));
    }

    /**
     * Rechaza una asignación
     * 
     * @param id ID de la asignación
     * @param motivo Motivo del rechazo
     * @return Asignación actualizada
     */
    @PostMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar asignación", 
               description = "El árbitro rechaza una asignación pendiente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Asignación rechazada",
                     content = @Content(schema = @Schema(implementation = AsignacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada"),
        @ApiResponse(responseCode = "400", description = "La asignación no puede ser rechazada"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<?> rechazarAsignacion(
            @Parameter(description = "ID de la asignación", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Motivo del rechazo") 
            @RequestParam(required = false) String motivo) {
        
        Asignacion asignacion = asignacionService.obtenerPorId(id).orElse(null);
        if (asignacion == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar que está pendiente
        if (asignacion.getEstado() != EstadoAsignacion.PENDIENTE) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La asignación no está pendiente");
            error.put("estadoActual", asignacion.getEstado().toString());
            return ResponseEntity.badRequest().body(error);
        }
        
        asignacion.setEstado(EstadoAsignacion.RECHAZADA);
        if (motivo != null && !motivo.isEmpty()) {
            asignacion.setComentarios(motivo);
        }
        asignacion = asignacionService.guardar(asignacion);
        
        return ResponseEntity.ok(convertirADto(asignacion));
    }

    /**
     * Obtiene estadísticas de asignaciones
     * 
     * @return Estadísticas generales
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de asignaciones", 
               description = "Obtiene métricas generales sobre las asignaciones en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        
        List<Asignacion> asignaciones = asignacionService.obtenerTodas();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", asignaciones.size());
        estadisticas.put("pendientes", 
                asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.PENDIENTE).count());
        estadisticas.put("aceptadas", 
                asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.ACEPTADA).count());
        estadisticas.put("rechazadas", 
                asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.RECHAZADA).count());
        estadisticas.put("completadas", 
                asignaciones.stream().filter(a -> a.getEstado() == EstadoAsignacion.COMPLETADA).count());
        
        return ResponseEntity.ok(estadisticas);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Convierte una entidad Asignacion a DTO
     */
    private AsignacionDto convertirADto(Asignacion asignacion) {
        AsignacionDto dto = new AsignacionDto();
        dto.setId(asignacion.getId());
        dto.setArbitroId(asignacion.getArbitro().getId());
        dto.setArbitroNombre(asignacion.getArbitro().getNombre() + " " + asignacion.getArbitro().getApellido());
        dto.setArbitroEmail(asignacion.getArbitro().getEmail());
        dto.setPartidoId(asignacion.getPartido().getId());
        dto.setPartidoDescripcion(asignacion.getPartido().getEquipoLocal() + " vs " + asignacion.getPartido().getEquipoVisitante());
        dto.setTorneoNombre(asignacion.getPartido().getTorneo().getNombre());
        dto.setFechaPartido(asignacion.getPartido().getFechaPartido());
        dto.setLugarPartido(asignacion.getPartido().getUbicacion());
        dto.setRolEspecifico(asignacion.getRolEspecifico());
        dto.setEstado(asignacion.getEstado().name()); // Convertir enum a String
        dto.setMontoCalculado(asignacion.getMontoCalculado());
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setFechaRespuesta(asignacion.getFechaRespuesta());
        dto.setComentarios(asignacion.getComentarios());
        
        return dto;
    }
}
