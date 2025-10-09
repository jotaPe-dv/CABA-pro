package com.pagina.Caba.service;

import jakarta.annotation.PostConstruct;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.repository.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArbitroService {

    @PostConstruct
    public void crearArbitrosPorDefecto() {
        if (arbitroRepository.count() == 0) {
            Arbitro principal = new Arbitro("Juan", "Principal", "principal@caba.com", "123456", "LIC001", "3000000001", "Principal", "A", "");
            principal.setDisponible(true);
            principal.setActivo(true);
            arbitroRepository.save(principal);

            Arbitro asistente = new Arbitro("Pedro", "Asistente", "asistente@caba.com", "123456", "LIC002", "3000000002", "Auxiliar", "B", "");
            asistente.setDisponible(true);
            asistente.setActivo(true);
            arbitroRepository.save(asistente);

            Arbitro mesa = new Arbitro("Maria", "Mesa", "mesa@caba.com", "123456", "LIC003", "3000000003", "Mesa", "C", "");
            mesa.setDisponible(true);
            mesa.setActivo(true);
            arbitroRepository.save(mesa);
        }
    }
    
    @Autowired
    private ArbitroRepository arbitroRepository;
    
    
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
        return List.of(); // Removed logic related to Especialidad
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
        // Removed method logic related to asignarEspecialidad
    }
    
    public void removerEspecialidad(Long arbitroId, Long especialidadId) {
        // Removed method logic related to removerEspecialidad
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
        return arbitroRepository.findAll(); // Simplificado temporalmente
    }
}