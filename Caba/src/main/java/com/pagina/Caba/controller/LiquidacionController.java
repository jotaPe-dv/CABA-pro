package com.pagina.Caba.controller;

import com.pagina.Caba.model.EstadoLiquidacion;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/liquidaciones")
@CrossOrigin(origins = "*")
public class LiquidacionController {
    
    @Autowired
    private LiquidacionService liquidacionService;
    
    // CREATE
    @PostMapping
    public ResponseEntity<Liquidacion> createLiquidacion(@RequestBody Liquidacion liquidacion) {
        try {
            Liquidacion nuevaLiquidacion = liquidacionService.save(liquidacion);
            return new ResponseEntity<>(nuevaLiquidacion, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // READ
    @GetMapping
    public ResponseEntity<List<Liquidacion>> getAllLiquidaciones() {
        try {
            List<Liquidacion> liquidaciones = liquidacionService.findAll();
            if (liquidaciones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(liquidaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Liquidacion> getLiquidacionById(@PathVariable Long id) {
        try {
            Optional<Liquidacion> liquidacionData = liquidacionService.findById(id);
            if (liquidacionData.isPresent()) {
                return new ResponseEntity<>(liquidacionData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/arbitro/{arbitroId}")
    public ResponseEntity<List<Liquidacion>> getLiquidacionesByArbitro(@PathVariable Long arbitroId) {
        try {
            List<Liquidacion> liquidaciones = liquidacionService.findByArbitroId(arbitroId);
            if (liquidaciones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(liquidaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Liquidacion>> getLiquidacionesByEstado(@PathVariable EstadoLiquidacion estado) {
        try {
            List<Liquidacion> liquidaciones = liquidacionService.findByEstado(estado);
            if (liquidaciones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(liquidaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/fechas")
    public ResponseEntity<List<Liquidacion>> getLiquidacionesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<Liquidacion> liquidaciones = liquidacionService.findByDateRange(inicio, fin);
            if (liquidaciones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(liquidaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Liquidacion> updateLiquidacion(@PathVariable Long id, @RequestBody Liquidacion liquidacionDetails) {
        try {
            Liquidacion liquidacionActualizada = liquidacionService.update(id, liquidacionDetails);
            return new ResponseEntity<>(liquidacionActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiquidacion(@PathVariable Long id) {
        try {
            liquidacionService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // MÉTODOS ESPECIALES
    @PostMapping("/generar")
    public ResponseEntity<Liquidacion> generarLiquidacion(
            @RequestParam Long arbitroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam BigDecimal monto) {
        try {
            Liquidacion liquidacion = liquidacionService.generarLiquidacion(arbitroId, fecha, monto);
            return new ResponseEntity<>(liquidacion, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Liquidacion> aprobarLiquidacion(@PathVariable Long id) {
        try {
            Liquidacion liquidacionAprobada = liquidacionService.aprobarLiquidacion(id);
            return new ResponseEntity<>(liquidacionAprobada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // CHECK EXISTENCE
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsLiquidacion(@PathVariable Long id) {
        try {
            boolean exists = liquidacionService.existsById(id);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
