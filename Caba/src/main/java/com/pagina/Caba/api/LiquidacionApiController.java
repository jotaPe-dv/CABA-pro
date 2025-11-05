package com.pagina.Caba.api;

import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.service.LiquidacionService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * API REST para gestión de Liquidaciones
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/liquidaciones")
@Tag(name = "Liquidaciones", description = "Operaciones de gestión de liquidaciones de pagos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class LiquidacionApiController {

    @Autowired
    private LiquidacionService liquidacionService;

    /**
     * Lista todas las liquidaciones
     */
    @GetMapping
    @Operation(summary = "Listar todas las liquidaciones", 
               description = "Obtiene la lista completa de liquidaciones en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                     content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = LiquidacionDto.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LiquidacionDto>> listarLiquidaciones(
            @Parameter(description = "Filtrar solo pendientes de pago") 
            @RequestParam(required = false) Boolean pendiente) {
        
        List<Liquidacion> liquidaciones = liquidacionService.obtenerTodas();
        
        if (pendiente != null && pendiente) {
            liquidaciones = liquidaciones.stream()
                    .filter(Liquidacion::estaPendiente)
                    .collect(Collectors.toList());
        }
        
        List<LiquidacionDto> dtos = liquidaciones.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene una liquidación por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener liquidación por ID",
               description = "Obtiene los datos completos de una liquidación específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liquidación encontrada",
                     content = @Content(schema = @Schema(implementation = LiquidacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Liquidación no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<LiquidacionDto> obtenerLiquidacion(
            @Parameter(description = "ID de la liquidación", required = true) 
            @PathVariable Long id) {
        
        Liquidacion liquidacion = liquidacionService.obtenerPorId(id).orElse(null);
        if (liquidacion == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertirADto(liquidacion));
    }

    /**
     * Marca una liquidación como pagada
     */
    @PostMapping("/{id}/marcar-pagada")
    @Operation(summary = "Marcar liquidación como pagada", 
               description = "Registra el pago de una liquidación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liquidación marcada como pagada",
                     content = @Content(schema = @Schema(implementation = LiquidacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Liquidación no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LiquidacionDto> marcarComoPagada(
            @Parameter(description = "ID de la liquidación", required = true) 
            @PathVariable Long id) {
        
        Liquidacion liquidacion = liquidacionService.obtenerPorId(id).orElse(null);
        if (liquidacion == null) {
            return ResponseEntity.notFound().build();
        }
        
        liquidacion.marcarComoPagada("TRANSFERENCIA", "REF-" + id);
        liquidacion = liquidacionService.guardar(liquidacion);
        
        return ResponseEntity.ok(convertirADto(liquidacion));
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Convierte una entidad Liquidacion a DTO
     */
    private LiquidacionDto convertirADto(Liquidacion liquidacion) {
        LiquidacionDto dto = new LiquidacionDto();
        dto.setId(liquidacion.getId());
        dto.setAsignacionId(liquidacion.getAsignacion().getId());
        dto.setMonto(liquidacion.getMonto());
        dto.setEstado(liquidacion.getEstado().name());
        dto.setFechaCreacion(liquidacion.getFechaCreacion());
        dto.setFechaPago(liquidacion.getFechaPago());
        dto.setMetodoPago(liquidacion.getMetodoPago());
        dto.setReferenciaPago(liquidacion.getReferenciaPago());
        dto.setObservaciones(liquidacion.getObservaciones());
        return dto;
    }
}
