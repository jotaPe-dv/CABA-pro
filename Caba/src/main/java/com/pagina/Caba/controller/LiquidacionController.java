package com.pagina.Caba.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.service.LiquidacionService;
import com.pagina.Caba.model.EstadoLiquidacion;

/**
 * Controlador REST para la gestión de liquidaciones.
 * Proporciona endpoints para operaciones CRUD de liquidaciones.
 */
@RestController
@RequestMapping("/api/liquidaciones")
@CrossOrigin(origins = "*")
public class LiquidacionController {

    @Autowired
    private LiquidacionService liquidacionService;

    /**
     * Obtiene todas las liquidaciones.
     * 
     * @return Lista de todas las liquidaciones
     */
    @GetMapping
    public ResponseEntity<List<LiquidacionDto>> getAllLiquidaciones() {
        try {
            List<LiquidacionDto> liquidaciones = liquidacionService.findAll();
            return ResponseEntity.ok(liquidaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una liquidación por ID.
     * 
     * @param id ID de la liquidación
     * @return Liquidación encontrada o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionDto> getLiquidacionById(@PathVariable Long id) {
        try {
            Optional<LiquidacionDto> liquidacion = liquidacionService.findById(id);
            return liquidacion.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva liquidación.
     * 
     * @param liquidacionDto DTO de la liquidación a crear
     * @return Liquidación creada
     */
    @PostMapping
    public ResponseEntity<LiquidacionDto> createLiquidacion(@Valid @RequestBody LiquidacionDto liquidacionDto) {
        try {
            LiquidacionDto nuevaLiquidacion = liquidacionService.save(liquidacionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLiquidacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una liquidación por ID.
     * 
     * @param id ID de la liquidación a eliminar
     * @return 204 si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiquidacion(@PathVariable Long id) {
        try {
            liquidacionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Genera una nueva liquidación para un árbitro.
     * 
     * @param arbitroId   ID del árbitro
     * @param fechaInicio Fecha de inicio del período (opcional)
     * @param fechaFin    Fecha de fin del período (opcional)
     * @return Liquidación generada
     */
    @PostMapping("/generar")
    public ResponseEntity<LiquidacionDto> generarLiquidacion(
            @RequestParam Long arbitroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            // Si no se proporcionan fechas, usar el mes actual
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now().withDayOfMonth(1);
            }
            if (fechaFin == null) {
                fechaFin = LocalDate.now();
            }
            
            LiquidacionDto liquidacion = liquidacionService.generarLiquidacion(arbitroId, fechaInicio, fechaFin);
            return ResponseEntity.status(HttpStatus.CREATED).body(liquidacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Aprueba una liquidación cambiando su estado a PAGADA.
     * 
     * @param id ID de la liquidación a aprobar
     * @return Liquidación aprobada
     */
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<LiquidacionDto> aprobarLiquidacion(@PathVariable Long id) {
        try {
            LiquidacionDto liquidacionAprobada = liquidacionService.aprobarLiquidacion(id);
            return ResponseEntity.ok(liquidacionAprobada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene las liquidaciones de un árbitro específico.
     * 
     * @param arbitroId ID del árbitro
     * @return Lista de liquidaciones del árbitro
     */
    @GetMapping("/arbitro/{arbitroId}")
    public ResponseEntity<List<LiquidacionDto>> getLiquidacionesByArbitro(@PathVariable Long arbitroId) {
        try {
            List<LiquidacionDto> liquidaciones = liquidacionService.getLiquidacionesByArbitro(arbitroId);
            return ResponseEntity.ok(liquidaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene liquidaciones por estado.
     * 
     * @param estado Estado de las liquidaciones
     * @return Lista de liquidaciones con el estado especificado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<LiquidacionDto>> getLiquidacionesByEstado(@PathVariable EstadoLiquidacion estado) {
        try {
            List<LiquidacionDto> liquidaciones = liquidacionService.getLiquidacionesByEstado(estado);
            return ResponseEntity.ok(liquidaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene liquidaciones en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin    Fecha de fin del rango
     * @return Lista de liquidaciones en el rango
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<LiquidacionDto>> getLiquidacionesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<LiquidacionDto> liquidaciones = liquidacionService.getLiquidacionesByDateRange(fechaInicio, fechaFin);
            return ResponseEntity.ok(liquidaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene liquidaciones pendientes.
     * 
     * @return Lista de liquidaciones pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<LiquidacionDto>> getLiquidacionesPendientes() {
        try {
            List<LiquidacionDto> liquidacionesPendientes = 
                liquidacionService.getLiquidacionesByEstado(EstadoLiquidacion.PENDIENTE);
            return ResponseEntity.ok(liquidacionesPendientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene liquidaciones pagadas.
     * 
     * @return Lista de liquidaciones pagadas
     */
    @GetMapping("/pagadas")
    public ResponseEntity<List<LiquidacionDto>> getLiquidacionesPagadas() {
        try {
            List<LiquidacionDto> liquidacionesPagadas = 
                liquidacionService.getLiquidacionesByEstado(EstadoLiquidacion.PAGADA);
            return ResponseEntity.ok(liquidacionesPagadas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
