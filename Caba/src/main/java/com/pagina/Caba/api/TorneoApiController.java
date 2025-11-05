package com.pagina.Caba.api;

import com.pagina.Caba.dto.TorneoDto;
import com.pagina.Caba.service.TorneoService;
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

/**
 * API REST para gestión de Torneos
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/torneos")
@Tag(name = "Torneos", description = "Operaciones de gestión de torneos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class TorneoApiController {

    @Autowired
    private TorneoService torneoService;

    /**
     * Lista todos los torneos con filtros opcionales
     */
    @GetMapping
    @Operation(summary = "Listar todos los torneos",
               description = "Obtiene la lista completa de torneos con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<List<TorneoDto>> listarTorneos(
            @Parameter(description = "Filtrar solo activos")
            @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Buscar por nombre")
            @RequestParam(required = false) String nombre) {
        
        List<TorneoDto> torneos;
        
        if (nombre != null && !nombre.isEmpty()) {
            torneos = torneoService.findByNombreContaining(nombre);
        } else if (Boolean.TRUE.equals(activo)) {
            torneos = torneoService.getTorneosActivos();
        } else {
            torneos = torneoService.findAll();
        }
        
        return ResponseEntity.ok(torneos);
    }

    /**
     * Obtiene un torneo por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener torneo por ID",
               description = "Obtiene los datos completos de un torneo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<TorneoDto> obtenerTorneo(
            @Parameter(description = "ID del torneo", required = true)
            @PathVariable Long id) {
        
        return torneoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo torneo
     */
    @PostMapping
    @Operation(summary = "Crear nuevo torneo",
               description = "Registra un nuevo torneo en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Torneo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TorneoDto> crearTorneo(@RequestBody TorneoDto torneoDto) {
        TorneoDto nuevoTorneo = torneoService.save(torneoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTorneo);
    }

    /**
     * Actualiza un torneo existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar torneo",
               description = "Actualiza los datos de un torneo existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Torneo actualizado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TorneoDto> actualizarTorneo(
            @Parameter(description = "ID del torneo", required = true)
            @PathVariable Long id,
            @RequestBody TorneoDto torneoDto) {
        
        if (torneoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TorneoDto actualizado = torneoService.update(id, torneoDto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un torneo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar torneo",
               description = "Elimina un torneo del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Torneo eliminado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar (tiene partidos asociados)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarTorneo(
            @Parameter(description = "ID del torneo", required = true)
            @PathVariable Long id) {
        
        if (torneoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            torneoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
