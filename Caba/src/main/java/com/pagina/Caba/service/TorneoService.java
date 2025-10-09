package com.pagina.Caba.service;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    // CRUD Básico
    public List<Torneo> obtenerTodos() {
        return torneoRepository.findAll();
    }

    public Optional<Torneo> obtenerPorId(Long id) {
        return torneoRepository.findById(id);
    }

    public Torneo guardar(Torneo torneo) {
        validarTorneo(torneo);
        return torneoRepository.save(torneo);
    }

    public void eliminar(Long id) {
        torneoRepository.deleteById(id);
    }

    // Lógica de Negocio
    public List<Torneo> obtenerTorneosActivos() {
        return torneoRepository.findByActivoTrue();
    }

    public List<Torneo> obtenerTorneosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return torneoRepository.findByFechaInicioBetween(fechaInicio, fechaFin);
    }

    public List<Torneo> buscarPorNombre(String nombre) {
        return torneoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Torneo> obtenerTorneosFuturos() {
        return torneoRepository.findTorneosFuturos(LocalDateTime.now());
    }

    public List<Torneo> obtenerTorneosFinalizados() {
        return torneoRepository.findTorneosFinalizados(LocalDateTime.now());
    }

    public List<Torneo> obtenerTorneosActivosEnFecha(LocalDateTime fecha) {
        return torneoRepository.findTorneosActivosEnFecha(fecha);
    }

    public void activarTorneo(Long torneoId) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();
            torneo.activar();
            torneoRepository.save(torneo);
        }
    }

    public void desactivarTorneo(Long torneoId) {
        Optional<Torneo> torneoOpt = torneoRepository.findById(torneoId);
        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();
            torneo.desactivar();
            torneoRepository.save(torneo);
        }
    }

    // Estadísticas
    public long contarTorneosActivos() {
        return torneoRepository.countTorneosActivos();
    }

    public long contarTorneosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return torneoRepository.findByFechaInicioBetween(fechaInicio, fechaFin).size();
    }

    // Validaciones
    private void validarTorneo(Torneo torneo) {
        if (torneo.getNombre() != null) {
            List<Torneo> existentes = torneoRepository.findByNombreContainingIgnoreCase(torneo.getNombre());
            boolean nombreExiste = existentes.stream()
                    .anyMatch(t -> t.getNombre().equalsIgnoreCase(torneo.getNombre()) && 
                                  (torneo.getId() == null || !t.getId().equals(torneo.getId())));
            if (nombreExiste) {
                throw new IllegalArgumentException("Ya existe un torneo con ese nombre");
            }
        }
        
        if (torneo.getFechaInicio() != null && torneo.getFechaFin() != null) {
            if (torneo.getFechaInicio().isAfter(torneo.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
        }
    }
}