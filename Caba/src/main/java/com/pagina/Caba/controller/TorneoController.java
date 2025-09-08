package com.pagina.Caba.controller;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/torneos")
@CrossOrigin(origins = "*")
public class TorneoController {
    
    @Autowired
    private TorneoService torneoService;
    
    // CREATE
    @PostMapping
    public ResponseEntity<Torneo> createTorneo(@RequestBody Torneo torneo) {
        try {
            Torneo nuevoTorneo = torneoService.save(torneo);
            return new ResponseEntity<>(nuevoTorneo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // READ
    @GetMapping
    public ResponseEntity<List<Torneo>> getAllTorneos() {
        try {
            List<Torneo> torneos = torneoService.findAll();
            if (torneos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(torneos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Torneo> getTorneoById(@PathVariable Long id) {
        try {
            Optional<Torneo> torneoData = torneoService.findById(id);
            if (torneoData.isPresent()) {
                return new ResponseEntity<>(torneoData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Torneo> getTorneoByNombre(@PathVariable String nombre) {
        try {
            Optional<Torneo> torneoData = torneoService.findByNombre(nombre);
            if (torneoData.isPresent()) {
                return new ResponseEntity<>(torneoData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Torneo>> getTorneosActivos() {
        try {
            List<Torneo> torneos = torneoService.findTorneosActivos();
            if (torneos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(torneos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/fechas")
    public ResponseEntity<List<Torneo>> getTorneosByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<Torneo> torneos = torneoService.findByDateRange(inicio, fin);
            if (torneos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(torneos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Torneo> updateTorneo(@PathVariable Long id, @RequestBody Torneo torneoDetails) {
        try {
            Torneo torneoActualizado = torneoService.update(id, torneoDetails);
            return new ResponseEntity<>(torneoActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTorneo(@PathVariable Long id) {
        try {
            torneoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // CHECK EXISTENCE
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsTorneo(@PathVariable Long id) {
        try {
            boolean exists = torneoService.existsById(id);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
