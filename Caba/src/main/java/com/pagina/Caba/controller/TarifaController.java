package com.pagina.Caba.controller;

import com.pagina.Caba.dto.TarifaDto;
import com.pagina.Caba.service.TarifaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión de tarifas.
 * Proporciona endpoints para operaciones CRUD de tarifas.
 */
@RestController
@RequestMapping("/api/tarifas")
@CrossOrigin(origins = "*")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    /**
     * Obtiene todas las tarifas.
     * 
     * @return Lista de todas las tarifas
     */
    @GetMapping
    public ResponseEntity<List<TarifaDto>> getAllTarifas() {
        try {
            List<TarifaDto> tarifas = tarifaService.findAll();
            return ResponseEntity.ok(tarifas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una tarifa por ID.
     * 
     * @param id ID de la tarifa
     * @return Tarifa encontrada o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarifaDto> getTarifaById(@PathVariable Long id) {
        try {
            Optional<TarifaDto> tarifa = tarifaService.findById(id);
            return tarifa.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva tarifa.
     * 
     * @param tarifaDto DTO de la tarifa a crear
     * @return Tarifa creada
     */
    @PostMapping
    public ResponseEntity<TarifaDto> createTarifa(@Valid @RequestBody TarifaDto tarifaDto) {
        try {
            TarifaDto nuevaTarifa = tarifaService.save(tarifaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarifa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualiza una tarifa existente.
     * 
     * @param id        ID de la tarifa a actualizar
     * @param tarifaDto DTO con los nuevos datos
     * @return Tarifa actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarifaDto> updateTarifa(@PathVariable Long id, 
                                                  @Valid @RequestBody TarifaDto tarifaDto) {
        try {
            TarifaDto tarifaActualizada = tarifaService.update(id, tarifaDto);
            return ResponseEntity.ok(tarifaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una tarifa por ID.
     * 
     * @param id ID de la tarifa a eliminar
     * @return 204 si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Long id) {
        try {
            tarifaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todas las tarifas de un torneo específico.
     * 
     * @param torneoId ID del torneo
     * @return Lista de tarifas del torneo
     */
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<TarifaDto>> getTarifasByTorneo(@PathVariable Long torneoId) {
        try {
            List<TarifaDto> tarifas = tarifaService.getTarifasByTorneo(torneoId);
            return ResponseEntity.ok(tarifas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todas las tarifas de un escalafón específico.
     * 
     * @param escalafon Escalafón de las tarifas
     * @return Lista de tarifas del escalafón
     */
    @GetMapping("/escalafon/{escalafon}")
    public ResponseEntity<List<TarifaDto>> getTarifasByEscalafon(@PathVariable String escalafon) {
        try {
            List<TarifaDto> tarifas = tarifaService.getTarifasByEscalafon(escalafon);
            return ResponseEntity.ok(tarifas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca una tarifa específica por torneo y escalafón.
     * 
     * @param torneoId  ID del torneo
     * @param escalafon Escalafón de la tarifa
     * @return Tarifa encontrada o 404 si no existe
     */
    @GetMapping("/torneo/{torneoId}/escalafon/{escalafon}")
    public ResponseEntity<TarifaDto> getTarifaByTorneoAndEscalafon(@PathVariable Long torneoId, 
                                                                  @PathVariable String escalafon) {
        try {
            Optional<TarifaDto> tarifa = tarifaService.findByTorneoAndEscalafon(torneoId, escalafon);
            return tarifa.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
