package com.pagina.Caba.service;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.dto.TorneoDto;
import com.pagina.Caba.dto.TarifaDto;
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

    // Métodos para el controlador REST (DTO)
    public List<TorneoDto> findAll() {
        return torneoRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<TorneoDto> findById(Long id) {
        return torneoRepository.findById(id).map(this::toDto);
    }

    public TorneoDto save(TorneoDto dto) {
        Torneo torneo = toEntity(dto);
        torneo.setActivo(true);
        torneo.setFechaCreacion(java.time.LocalDateTime.now());
        return toDto(torneoRepository.save(torneo));
    }

    public TorneoDto update(Long id, TorneoDto dto) {
        Torneo torneo = torneoRepository.findById(id).orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        torneo.setNombre(dto.getNombre());
        torneo.setFechaInicio(dto.getFechaInicio());
        torneo.setFechaFin(dto.getFechaFin());
        // Puedes agregar más campos aquí según tu modelo
        return toDto(torneoRepository.save(torneo));
    }

    public void deleteById(Long id) {
        Torneo torneo = torneoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado con id=" + id));
        // Verificar si tiene partidos asociados
        List<com.pagina.Caba.model.Partido> partidos = torneoRepository.findById(id)
            .map(t -> t.getPartidos() != null ? new java.util.ArrayList<>(t.getPartidos()) : java.util.Collections.<com.pagina.Caba.model.Partido>emptyList())
            .orElse(java.util.Collections.emptyList());
        if (!partidos.isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar el torneo porque tiene " + partidos.size() + " partidos asociados. " +
                "Elimine primero los partidos o reasígnelos a otro torneo.");
        }
        torneoRepository.deleteById(id);
    }

    public List<TorneoDto> getTorneosActivos() {
        return torneoRepository.findByActivoTrue().stream().map(this::toDto).toList();
    }

    public List<TorneoDto> getTorneosByDateRange(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        return torneoRepository.findAll().stream()
            .filter(t -> !t.getFechaInicio().isBefore(fechaInicio) && !t.getFechaFin().isAfter(fechaFin))
            .map(this::toDto).toList();
    }

    public TorneoDto createTorneoWithTarifas(TorneoDto dto) {
        // Implementa la lógica para crear torneo y tarifas asociadas si es necesario
        return save(dto);
    }

    public List<TorneoDto> findByNombreContaining(String nombre) {
        return torneoRepository.findByNombreContainingIgnoreCase(nombre).stream().map(this::toDto).toList();
    }

    // Conversión entidad <-> DTO
    private TorneoDto toDto(Torneo torneo) {
        TorneoDto dto = new TorneoDto();
        dto.setId(torneo.getId());
        dto.setNombre(torneo.getNombre());
        dto.setFechaInicio(torneo.getFechaInicio());
        dto.setFechaFin(torneo.getFechaFin());
        // Si tienes relación con tarifas, setea aquí la lista de tarifas
        return dto;
    }

    private Torneo toEntity(TorneoDto dto) {
        Torneo torneo = new Torneo();
        torneo.setId(dto.getId());
        torneo.setNombre(dto.getNombre());
        torneo.setFechaInicio(dto.getFechaInicio());
        torneo.setFechaFin(dto.getFechaFin());
        // Si tienes relación con tarifas, setea aquí la lista de tarifas
        return torneo;
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