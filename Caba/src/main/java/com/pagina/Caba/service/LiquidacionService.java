package com.pagina.Caba.service;

import com.pagina.Caba.model.EstadoLiquidacion;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.repository.LiquidacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LiquidacionService {
    
    @Autowired
    private LiquidacionRepository liquidacionRepository;
    
    // CREATE
    public Liquidacion save(Liquidacion liquidacion) {
        try {
            return liquidacionRepository.save(liquidacion);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la liquidación: " + e.getMessage());
        }
    }
    
    // READ
    public List<Liquidacion> findAll() {
        try {
            return liquidacionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las liquidaciones: " + e.getMessage());
        }
    }
    
    public Optional<Liquidacion> findById(Long id) {
        try {
            return liquidacionRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la liquidación: " + e.getMessage());
        }
    }
    
    public List<Liquidacion> findByArbitroId(Long arbitroId) {
        try {
            return liquidacionRepository.findByArbitroId(arbitroId);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar liquidaciones por árbitro: " + e.getMessage());
        }
    }
    
    public List<Liquidacion> findByEstado(EstadoLiquidacion estado) {
        try {
            return liquidacionRepository.findByEstado(estado);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar liquidaciones por estado: " + e.getMessage());
        }
    }
    
    // UPDATE
    public Liquidacion update(Long id, Liquidacion liquidacionDetails) {
        try {
            Optional<Liquidacion> liquidacionOptional = liquidacionRepository.findById(id);
            if (liquidacionOptional.isPresent()) {
                Liquidacion liquidacion = liquidacionOptional.get();
                liquidacion.setFecha(liquidacionDetails.getFecha());
                liquidacion.setMontoTotal(liquidacionDetails.getMontoTotal());
                liquidacion.setEstado(liquidacionDetails.getEstado());
                liquidacion.setArbitro(liquidacionDetails.getArbitro());
                return liquidacionRepository.save(liquidacion);
            } else {
                throw new RuntimeException("Liquidación no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la liquidación: " + e.getMessage());
        }
    }
    
    // DELETE
    public void deleteById(Long id) {
        try {
            if (liquidacionRepository.existsById(id)) {
                liquidacionRepository.deleteById(id);
            } else {
                throw new RuntimeException("Liquidación no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la liquidación: " + e.getMessage());
        }
    }
    
    public boolean existsById(Long id) {
        try {
            return liquidacionRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de liquidación: " + e.getMessage());
        }
    }
    
    // Métodos adicionales
    public Liquidacion generarLiquidacion(Long arbitroId, LocalDate fecha, BigDecimal monto) {
        try {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setFecha(fecha);
            liquidacion.setMontoTotal(monto);
            liquidacion.setEstado(EstadoLiquidacion.PENDIENTE);
            // Aquí se podría cargar el árbitro desde el servicio correspondiente
            return liquidacionRepository.save(liquidacion);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar la liquidación: " + e.getMessage());
        }
    }
    
    public Liquidacion aprobarLiquidacion(Long id) {
        try {
            Optional<Liquidacion> liquidacionOptional = liquidacionRepository.findById(id);
            if (liquidacionOptional.isPresent()) {
                Liquidacion liquidacion = liquidacionOptional.get();
                liquidacion.setEstado(EstadoLiquidacion.PAGADA);
                return liquidacionRepository.save(liquidacion);
            } else {
                throw new RuntimeException("Liquidación no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al aprobar la liquidación: " + e.getMessage());
        }
    }
    
    public List<Liquidacion> findByDateRange(LocalDate inicio, LocalDate fin) {
        try {
            return liquidacionRepository.findByFechaBetween(inicio, fin);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar liquidaciones por rango de fechas: " + e.getMessage());
        }
    }
}
