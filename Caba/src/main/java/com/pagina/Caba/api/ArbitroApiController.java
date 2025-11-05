package com.pagina.Caba.api;

import com.pagina.Caba.dto.ArbitroDto;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.service.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API REST para gestión de Árbitros
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/arbitros")
@Tag(name = "Árbitros", description = "Operaciones de gestión de árbitros")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class ArbitroApiController {

    @Autowired
    private ArbitroService arbitroService;

    /**
     * Lista todos los árbitros con filtros opcionales
     */
    @GetMapping
    @Operation(summary = "Listar todos los árbitros",
               description = "Obtiene la lista completa de árbitros con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ArbitroDto>> listarArbitros(
            @Parameter(description = "Filtrar solo disponibles")
            @RequestParam(required = false) Boolean disponible,
            @Parameter(description = "Filtrar por especialidad")
            @RequestParam(required = false) String especialidad) {
        
        List<Arbitro> arbitros = arbitroService.obtenerTodos();
        
        // Aplicar filtros si se proporcionan
        if (disponible != null) {
            arbitros = arbitros.stream()
                    .filter(a -> disponible.equals(a.getDisponible()))
                    .collect(Collectors.toList());
        }
        
        if (especialidad != null && !especialidad.isEmpty()) {
            arbitros = arbitros.stream()
                    .filter(a -> especialidad.equalsIgnoreCase(a.getEspecialidad()))
                    .collect(Collectors.toList());
        }
        
        List<ArbitroDto> dtos = arbitros.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene un árbitro por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener árbitro por ID",
               description = "Obtiene los datos completos de un árbitro específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Árbitro encontrado"),
        @ApiResponse(responseCode = "404", description = "Árbitro no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<ArbitroDto> obtenerArbitro(
            @Parameter(description = "ID del árbitro", required = true)
            @PathVariable Long id) {
        
        Arbitro arbitro = arbitroService.obtenerPorId(id)
                .orElse(null);
        
        if (arbitro == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertirADto(arbitro));
    }

    /**
     * Actualiza la disponibilidad de un árbitro
     */
    @PatchMapping("/{id}/disponibilidad")
    @Operation(summary = "Actualizar disponibilidad",
               description = "Cambia el estado de disponibilidad del árbitro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
        @ApiResponse(responseCode = "404", description = "Árbitro no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<ArbitroDto> actualizarDisponibilidad(
            @Parameter(description = "ID del árbitro", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nueva disponibilidad", required = true)
            @RequestParam Boolean disponible) {
        
        Arbitro arbitro = arbitroService.obtenerPorId(id)
                .orElse(null);
        
        if (arbitro == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (disponible) {
            arbitro.marcarDisponible();
        } else {
            arbitro.marcarNoDisponible();
        }
        
        arbitro = arbitroService.actualizar(id, arbitro);
        
        return ResponseEntity.ok(convertirADto(arbitro));
    }

    /**
     * Crea un nuevo árbitro
     */
    @PostMapping
    @Operation(summary = "Crear nuevo árbitro",
               description = "Registra un nuevo árbitro en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Árbitro creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArbitroDto> crearArbitro(@RequestBody Arbitro arbitro) {
        Arbitro nuevoArbitro = arbitroService.crear(arbitro);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADto(nuevoArbitro));
    }

    /**
     * Actualiza un árbitro existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar árbitro",
               description = "Actualiza los datos de un árbitro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Árbitro actualizado"),
        @ApiResponse(responseCode = "404", description = "Árbitro no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArbitroDto> actualizarArbitro(
            @Parameter(description = "ID del árbitro", required = true)
            @PathVariable Long id,
            @RequestBody Arbitro arbitroActualizado) {
        
        Arbitro arbitro = arbitroService.obtenerPorId(id)
                .orElse(null);
        
        if (arbitro == null) {
            return ResponseEntity.notFound().build();
        }
        
        arbitro = arbitroService.actualizar(id, arbitroActualizado);
        
        return ResponseEntity.ok(convertirADto(arbitro));
    }

    /**
     * Elimina (desactiva) un árbitro
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar árbitro",
               description = "Desactiva un árbitro del sistema (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Árbitro eliminado"),
        @ApiResponse(responseCode = "404", description = "Árbitro no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarArbitro(
            @Parameter(description = "ID del árbitro", required = true)
            @PathVariable Long id) {
        
        Arbitro arbitro = arbitroService.obtenerPorId(id)
                .orElse(null);
        
        if (arbitro == null) {
            return ResponseEntity.notFound().build();
        }
        
        arbitro.setActivo(false);
        arbitroService.actualizar(id, arbitro);
        
        return ResponseEntity.noContent().build();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Convierte una entidad Arbitro a DTO
     */
    private ArbitroDto convertirADto(Arbitro arbitro) {
        ArbitroDto dto = new ArbitroDto();
        dto.setId(arbitro.getId());
        dto.setEmail(arbitro.getEmail());
        dto.setNombre(arbitro.getNombre());
        dto.setApellido(arbitro.getApellido());
        dto.setTelefono(arbitro.getTelefono());
        dto.setDireccion(arbitro.getDireccion());
        dto.setNumeroLicencia(arbitro.getNumeroLicencia());
        dto.setEspecialidad(arbitro.getEspecialidad());
        dto.setEscalafon(arbitro.getEscalafon());
        dto.setTarifaBase(arbitro.getTarifaBase());
        dto.setDisponible(arbitro.getDisponible());
        dto.setActivo(arbitro.getActivo());
        dto.setFotoUrl(arbitro.getFotoUrl());
        
        // Estadísticas calculadas
        long totalAsignaciones = arbitro.getAsignaciones() != null ? arbitro.getAsignaciones().size() : 0;
        long pendientes = arbitro.getAsignaciones() != null ? 
                arbitro.getAsignaciones().stream()
                        .filter(a -> a.getEstado() == EstadoAsignacion.PENDIENTE)
                        .count() : 0;
        long aceptadas = arbitro.getAsignaciones() != null ?
                arbitro.getAsignaciones().stream()
                        .filter(a -> a.getEstado() == EstadoAsignacion.ACEPTADA)
                        .count() : 0;
        
        dto.setTotalAsignaciones((int) totalAsignaciones);
        dto.setAsignacionesPendientes((int) pendientes);
        dto.setAsignacionesAceptadas((int) aceptadas);
        
        return dto;
    }
}
