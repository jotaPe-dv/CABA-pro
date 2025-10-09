package com.pagina.Caba.controller;

import com.pagina.Caba.dto.TorneoDto;
import com.pagina.Caba.service.TorneoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión de torneos.
 * Proporciona endpoints para operaciones CRUD de torneos.
 */
@RestController
@RequestMapping("/api/torneos")
@CrossOrigin(origins = "*")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    /**
     * Obtiene todos los torneos.
     * 
     * @return Lista de todos los torneos
     */
    @GetMapping
    public ResponseEntity<List<TorneoDto>> getAllTorneos() {
        try {
            List<TorneoDto> torneos = torneoService.findAll();
            return ResponseEntity.ok(torneos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un torneo por ID.
     * 
     * @param id ID del torneo
     * @return Torneo encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<TorneoDto> getTorneoById(@PathVariable Long id) {
        try {
            Optional<TorneoDto> torneo = torneoService.findById(id);
            return torneo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo torneo.
     * 
     * @param torneoDto DTO del torneo a crear
     * @return Torneo creado
     */
    @PostMapping
    public ResponseEntity<TorneoDto> createTorneo(@Valid @RequestBody TorneoDto torneoDto) {
        try {
            TorneoDto nuevoTorneo = torneoService.save(torneoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTorneo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualiza un torneo existente.
     * 
     * @param id        ID del torneo a actualizar
     * @param torneoDto DTO con los nuevos datos
     * @return Torneo actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<TorneoDto> updateTorneo(@PathVariable Long id, 
                                                  @Valid @RequestBody TorneoDto torneoDto) {
        try {
            TorneoDto torneoActualizado = torneoService.update(id, torneoDto);
            return ResponseEntity.ok(torneoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina un torneo por ID.
     * 
     * @param id ID del torneo a eliminar
     * @return 204 si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTorneo(@PathVariable Long id) {
        try {
            torneoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todos los torneos activos.
     * 
     * @return Lista de torneos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<TorneoDto>> getTorneosActivos() {
        try {
            List<TorneoDto> torneosActivos = torneoService.getTorneosActivos();
            return ResponseEntity.ok(torneosActivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene torneos en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin    Fecha de fin del rango
     * @return Lista de torneos en el rango
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<TorneoDto>> getTorneosByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<TorneoDto> torneos = torneoService.getTorneosByDateRange(fechaInicio, fechaFin);
            return ResponseEntity.ok(torneos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un torneo con sus tarifas asociadas.
     * 
     * @param torneoDto DTO del torneo con tarifas
     * @return Torneo creado con tarifas
     */
    @PostMapping("/con-tarifas")
    public ResponseEntity<TorneoDto> createTorneoWithTarifas(@Valid @RequestBody TorneoDto torneoDto) {
        try {
            TorneoDto torneoConTarifas = torneoService.createTorneoWithTarifas(torneoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(torneoConTarifas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca torneos por nombre (búsqueda parcial).
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de torneos que contengan el texto
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<TorneoDto>> searchTorneosByNombre(@RequestParam String nombre) {
        try {
            List<TorneoDto> torneos = torneoService.findByNombreContaining(nombre);
            return ResponseEntity.ok(torneos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
