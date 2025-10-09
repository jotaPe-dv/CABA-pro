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
    
    // CRUD Básico
    public List<Arbitro> obtenerTodos() {
        return arbitroRepository.findAll();
    }
    
    public Optional<Arbitro> obtenerPorId(Long id) {
        return arbitroRepository.findById(id);
    }
    
    public Arbitro guardar(Arbitro arbitro) {
        validarArbitro(arbitro);
        return arbitroRepository.save(arbitro);
    }
    
    public void eliminar(Long id) {
        arbitroRepository.deleteById(id);
    }
    
    // Lógica de Negocio
    public List<Arbitro> obtenerArbitrosDisponibles() {
        return arbitroRepository.findByDisponibleTrueAndActivoTrue();
    }
    
    public Optional<Arbitro> buscarPorNumeroLicencia(String numeroLicencia) {
        return arbitroRepository.findByNumeroLicencia(numeroLicencia);
    }
    
    public List<Arbitro> buscarPorEspecialidad(Long especialidadId) {
        Optional<Especialidad> especialidad = especialidadRepository.findById(especialidadId);
        if (especialidad.isPresent()) {
            return arbitroRepository.findByEspecialidad(especialidad.get());
        }
        return List.of();
    }
    
    public void activarArbitro(Long arbitroId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            arbitro.activar();
            arbitro.marcarDisponible();
            arbitroRepository.save(arbitro);
        }
    }
    
    public void desactivarArbitro(Long arbitroId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            arbitro.desactivar();
            arbitro.marcarNoDisponible();
            arbitroRepository.save(arbitro);
        }
    }
    
    public void cambiarDisponibilidad(Long arbitroId, boolean disponible) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            if (disponible) {
                arbitro.marcarDisponible();
            } else {
                arbitro.marcarNoDisponible();
            }
            arbitroRepository.save(arbitro);
        }
    }
    
    public void asignarEspecialidad(Long arbitroId, Long especialidadId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        Optional<Especialidad> especialidadOpt = especialidadRepository.findById(especialidadId);
        
        if (arbitroOpt.isPresent() && especialidadOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            Especialidad especialidad = especialidadOpt.get();
            
            arbitro.getEspecialidades().add(especialidad);
            arbitroRepository.save(arbitro);
        }
    }
    
    public void removerEspecialidad(Long arbitroId, Long especialidadId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        Optional<Especialidad> especialidadOpt = especialidadRepository.findById(especialidadId);
        
        if (arbitroOpt.isPresent() && especialidadOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            Especialidad especialidad = especialidadOpt.get();
            
            arbitro.getEspecialidades().remove(especialidad);
            arbitroRepository.save(arbitro);
        }
    }
    
    // Estadísticas
    public long contarArbitrosActivos() {
        return arbitroRepository.countArbitrosActivos();
    }
    
    public long contarArbitrosDisponibles() {
        return arbitroRepository.countArbitrosDisponibles();
    }
    
    // Validaciones
    private void validarArbitro(Arbitro arbitro) {
        if (arbitro.getNumeroLicencia() != null && 
            arbitroRepository.existsByNumeroLicencia(arbitro.getNumeroLicencia())) {
            if (arbitro.getId() == null) { // Nuevo arbitro
                throw new IllegalArgumentException("Ya existe un árbitro con ese número de licencia");
            }
            // Para edición, verificar que no sea otro arbitro
            Optional<Arbitro> existente = arbitroRepository.findByNumeroLicencia(arbitro.getNumeroLicencia());
            if (existente.isPresent() && !existente.get().getId().equals(arbitro.getId())) {
                throw new IllegalArgumentException("Ya existe un árbitro con ese número de licencia");
            }
        }
    }
    
    // Búsquedas
    public List<Arbitro> buscarPorTexto(String texto) {
        return arbitroRepository.findByNombreOrApellidoContaining(texto);
    }
}