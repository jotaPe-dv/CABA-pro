package com.pagina.Caba.service;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TorneoService {
    
    @Autowired
    private TorneoRepository torneoRepository;
    
    // CREATE
    public Torneo save(Torneo torneo) {
        try {
            return torneoRepository.save(torneo);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el torneo: " + e.getMessage());
        }
    }
    
    // READ
    public List<Torneo> findAll() {
        try {
            return torneoRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los torneos: " + e.getMessage());
        }
    }
    
    public Optional<Torneo> findById(Long id) {
        try {
            return torneoRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el torneo: " + e.getMessage());
        }
    }
    
    public Optional<Torneo> findByNombre(String nombre) {
        try {
            return torneoRepository.findByNombre(nombre);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar torneo por nombre: " + e.getMessage());
        }
    }
    
    // UPDATE
    public Torneo update(Long id, Torneo torneoDetails) {
        try {
            Optional<Torneo> torneoOptional = torneoRepository.findById(id);
            if (torneoOptional.isPresent()) {
                Torneo torneo = torneoOptional.get();
                torneo.setNombre(torneoDetails.getNombre());
                torneo.setFechaInicio(torneoDetails.getFechaInicio());
                torneo.setFechaFin(torneoDetails.getFechaFin());
                return torneoRepository.save(torneo);
            } else {
                throw new RuntimeException("Torneo no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el torneo: " + e.getMessage());
        }
    }
    
    // DELETE
    public void deleteById(Long id) {
        try {
            if (torneoRepository.existsById(id)) {
                torneoRepository.deleteById(id);
            } else {
                throw new RuntimeException("Torneo no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el torneo: " + e.getMessage());
        }
    }
    
    public boolean existsById(Long id) {
        try {
            return torneoRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de torneo: " + e.getMessage());
        }
    }
    
    // Métodos adicionales
    public List<Torneo> findTorneosActivos() {
        try {
            LocalDate fechaActual = LocalDate.now();
            return torneoRepository.findByFechaFinAfter(fechaActual);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar torneos activos: " + e.getMessage());
        }
    }
    
    public List<Torneo> findByDateRange(LocalDate inicio, LocalDate fin) {
        try {
            return torneoRepository.findByFechaInicioBetween(inicio, fin);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar torneos por rango de fechas: " + e.getMessage());
        }
    }
}
