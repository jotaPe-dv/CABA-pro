package com.pagina.Caba.service;

import com.pagina.Caba.model.Tarifa;
import com.pagina.Caba.repository.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TarifaService {
    
    @Autowired
    private TarifaRepository tarifaRepository;
    
    // CREATE
    public Tarifa save(Tarifa tarifa) {
        try {
            return tarifaRepository.save(tarifa);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la tarifa: " + e.getMessage());
        }
    }
    
    // READ
    public List<Tarifa> findAll() {
        try {
            return tarifaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las tarifas: " + e.getMessage());
        }
    }
    
    public Optional<Tarifa> findById(Long id) {
        try {
            return tarifaRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la tarifa: " + e.getMessage());
        }
    }
    
    public List<Tarifa> findByTorneoId(Long torneoId) {
        try {
            return tarifaRepository.findByTorneoId(torneoId);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar tarifas por torneo: " + e.getMessage());
        }
    }
    
    public List<Tarifa> findByEscalafon(String escalafon) {
        try {
            return tarifaRepository.findByEscalafon(escalafon);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar tarifas por escalafón: " + e.getMessage());
        }
    }
    
    // UPDATE
    public Tarifa update(Long id, Tarifa tarifaDetails) {
        try {
            Optional<Tarifa> tarifaOptional = tarifaRepository.findById(id);
            if (tarifaOptional.isPresent()) {
                Tarifa tarifa = tarifaOptional.get();
                tarifa.setMonto(tarifaDetails.getMonto());
                tarifa.setEscalafon(tarifaDetails.getEscalafon());
                tarifa.setTorneo(tarifaDetails.getTorneo());
                return tarifaRepository.save(tarifa);
            } else {
                throw new RuntimeException("Tarifa no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la tarifa: " + e.getMessage());
        }
    }
    
    // DELETE
    public void deleteById(Long id) {
        try {
            if (tarifaRepository.existsById(id)) {
                tarifaRepository.deleteById(id);
            } else {
                throw new RuntimeException("Tarifa no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la tarifa: " + e.getMessage());
        }
    }
    
    public boolean existsById(Long id) {
        try {
            return tarifaRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de tarifa: " + e.getMessage());
        }
    }
}
