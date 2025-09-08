package com.pagina.Caba.controller;

import com.pagina.Caba.model.Tarifa;
import com.pagina.Caba.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tarifas")
@CrossOrigin(origins = "*")
public class TarifaController {
    
    @Autowired
    private TarifaService tarifaService;
    
    // CREATE
    @PostMapping
    public ResponseEntity<Tarifa> createTarifa(@RequestBody Tarifa tarifa) {
        try {
            Tarifa nuevaTarifa = tarifaService.save(tarifa);
            return new ResponseEntity<>(nuevaTarifa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // READ
    @GetMapping
    public ResponseEntity<List<Tarifa>> getAllTarifas() {
        try {
            List<Tarifa> tarifas = tarifaService.findAll();
            if (tarifas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tarifas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getTarifaById(@PathVariable Long id) {
        try {
            Optional<Tarifa> tarifaData = tarifaService.findById(id);
            if (tarifaData.isPresent()) {
                return new ResponseEntity<>(tarifaData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Tarifa>> getTarifasByTorneo(@PathVariable Long torneoId) {
        try {
            List<Tarifa> tarifas = tarifaService.findByTorneoId(torneoId);
            if (tarifas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tarifas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/escalafon/{escalafon}")
    public ResponseEntity<List<Tarifa>> getTarifasByEscalafon(@PathVariable String escalafon) {
        try {
            List<Tarifa> tarifas = tarifaService.findByEscalafon(escalafon);
            if (tarifas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tarifas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> updateTarifa(@PathVariable Long id, @RequestBody Tarifa tarifaDetails) {
        try {
            Tarifa tarifaActualizada = tarifaService.update(id, tarifaDetails);
            return new ResponseEntity<>(tarifaActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarifa(@PathVariable Long id) {
        try {
            tarifaService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // CHECK EXISTENCE
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsTarifa(@PathVariable Long id) {
        try {
            boolean exists = tarifaService.existsById(id);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
