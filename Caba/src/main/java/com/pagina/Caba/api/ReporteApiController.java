package com.pagina.Caba.api;

import com.pagina.Caba.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API REST para generación y descarga de reportes
 * 
 * Este controlador delega la lógica de negocio al ReporteService,
 * manteniendo la responsabilidad única de manejar requests HTTP.
 * 
 * Implementa el principio de Inversión de Dependencias (DIP):
 * - Depende de abstracciones (ReporteService, interfaces)
 * - No conoce los detalles de implementación (PDF/Excel generators)
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Operaciones de generación y descarga de reportes en PDF y Excel")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class ReporteApiController {
    
    @Autowired
    private ReporteService reporteService;
    
    /**
     * Genera reporte de liquidaciones
     */
    @GetMapping("/liquidaciones")
    @Operation(summary = "Generar reporte de liquidaciones",
               description = "Genera un reporte de liquidaciones en formato PDF o Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Formato inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generarReporteLiquidaciones(
            @Parameter(description = "Formato del reporte: pdf o excel", required = true)
            @RequestParam(defaultValue = "pdf") String formato,
            @Parameter(description = "Estado de liquidación: PENDIENTE, PAGADA, CANCELADA")
            @RequestParam(required = false) String estado) {
        
        try {
            ReporteService.ReporteResultado resultado = reporteService.generarReporteLiquidaciones(formato, estado);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + resultado.getNombreArchivo());
            headers.add("Content-Type", resultado.getContentType());
            
            return new ResponseEntity<>(resultado.getContenido(), headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Genera reporte de árbitros
     */
    @GetMapping("/arbitros")
    @Operation(summary = "Generar reporte de árbitros",
               description = "Genera un reporte de árbitros en formato PDF o Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Formato inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generarReporteArbitros(
            @Parameter(description = "Formato del reporte: pdf o excel", required = true)
            @RequestParam(defaultValue = "pdf") String formato,
            @Parameter(description = "Filtrar por disponibilidad: true o false")
            @RequestParam(required = false) Boolean disponible,
            @Parameter(description = "Filtrar por especialidad")
            @RequestParam(required = false) String especialidad) {
        
        try {
            ReporteService.ReporteResultado resultado = reporteService.generarReporteArbitros(formato, disponible, especialidad);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + resultado.getNombreArchivo());
            headers.add("Content-Type", resultado.getContentType());
            
            return new ResponseEntity<>(resultado.getContenido(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Genera reporte de partidos
     */
    @GetMapping("/partidos")
    @Operation(summary = "Generar reporte de partidos",
               description = "Genera un reporte de partidos en formato PDF o Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Formato inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generarReportePartidos(
            @Parameter(description = "Formato del reporte: pdf o excel", required = true)
            @RequestParam(defaultValue = "pdf") String formato,
            @Parameter(description = "ID del torneo")
            @RequestParam(required = false) Long torneoId,
            @Parameter(description = "Filtrar por completado: true o false")
            @RequestParam(required = false) Boolean completado) {
        
        try {
            ReporteService.ReporteResultado resultado = reporteService.generarReportePartidos(formato, torneoId, completado);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + resultado.getNombreArchivo());
            headers.add("Content-Type", resultado.getContentType());
            
            return new ResponseEntity<>(resultado.getContenido(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Genera reporte de asignaciones
     */
    @GetMapping("/asignaciones")
    @Operation(summary = "Generar reporte de asignaciones",
               description = "Genera un reporte de asignaciones en formato PDF o Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Formato inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generarReporteAsignaciones(
            @Parameter(description = "Formato del reporte: pdf o excel", required = true)
            @RequestParam(defaultValue = "pdf") String formato,
            @Parameter(description = "Estado de asignación: PENDIENTE, ACEPTADA, RECHAZADA, COMPLETADA")
            @RequestParam(required = false) String estado,
            @Parameter(description = "ID del árbitro")
            @RequestParam(required = false) Long arbitroId) {
        
        try {
            ReporteService.ReporteResultado resultado = reporteService.generarReporteAsignaciones(formato, estado, arbitroId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + resultado.getNombreArchivo());
            headers.add("Content-Type", resultado.getContentType());
            
            return new ResponseEntity<>(resultado.getContenido(), headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene los formatos disponibles
     */
    @GetMapping("/formatos")
    @Operation(summary = "Listar formatos disponibles",
               description = "Obtiene la lista de formatos de reporte disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de formatos obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<Map<String, Map<String, String>>> listarFormatos() {
        Map<String, Map<String, String>> formatos = reporteService.obtenerFormatosDisponibles();
        return ResponseEntity.ok(formatos);
    }
}
