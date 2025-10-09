package com.pagina.Caba.service;

import com.pagina.Caba.model.Tarifa;
import com.pagina.Caba.repository.TarifaRepository;
import com.pagina.Caba.dto.TarifaDto;
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
    
    // Métodos para el controlador REST (DTO)
    public List<TarifaDto> findAll() {
        return tarifaRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<TarifaDto> findById(Long id) {
        return tarifaRepository.findById(id).map(this::toDto);
    }

    public TarifaDto save(TarifaDto dto) {
        Tarifa tarifa = toEntity(dto);
        tarifa.setActiva(true);
        tarifa.setFechaCreacion(java.time.LocalDateTime.now());
        tarifa.setFechaVigenciaInicio(java.time.LocalDateTime.now());
        return toDto(tarifaRepository.save(tarifa));
    }

    public TarifaDto update(Long id, TarifaDto dto) {
        Tarifa tarifa = tarifaRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
        tarifa.setMonto(dto.getMonto());
        tarifa.setEscalafon(dto.getEscalafon());
        // Puedes agregar más campos aquí según tu modelo
        return toDto(tarifaRepository.save(tarifa));
    }

    public void deleteById(Long id) {
        tarifaRepository.deleteById(id);
    }

    public List<TarifaDto> getTarifasByTorneo(Long torneoId) {
        // Si tienes relación con torneo, implementa aquí el filtrado
        return tarifaRepository.findAll().stream()
            .filter(t -> t.getDescripcion() != null && t.getDescripcion().contains("Torneo:"+torneoId))
            .map(this::toDto).toList();
    }

    public List<TarifaDto> getTarifasByEscalafon(String escalafon) {
        return tarifaRepository.findAll().stream()
            .filter(t -> t.getEscalafon().equalsIgnoreCase(escalafon))
            .map(this::toDto).toList();
    }

    public Optional<TarifaDto> findByTorneoAndEscalafon(Long torneoId, String escalafon) {
        return tarifaRepository.findAll().stream()
            .filter(t -> t.getDescripcion() != null && t.getDescripcion().contains("Torneo:"+torneoId))
            .filter(t -> t.getEscalafon().equalsIgnoreCase(escalafon))
            .findFirst().map(this::toDto);
    }

    // Conversión entidad <-> DTO
    private TarifaDto toDto(Tarifa tarifa) {
        TarifaDto dto = new TarifaDto();
        dto.setId(tarifa.getId());
        dto.setMonto(tarifa.getMonto());
        dto.setEscalafon(tarifa.getEscalafon());
        // Si tienes relación con torneo, setea aquí el id/nombre
        return dto;
    }

    private Tarifa toEntity(TarifaDto dto) {
        Tarifa tarifa = new Tarifa();
        tarifa.setId(dto.getId());
        tarifa.setMonto(dto.getMonto());
        tarifa.setEscalafon(dto.getEscalafon());
        // Si tienes relación con torneo, setea aquí el id/nombre
        return tarifa;
    }
    
    /**
     * Busca la tarifa vigente según tipo de partido y escalafón del árbitro
     */
    public Optional<Tarifa> buscarTarifaParaAsignacion(String tipoPartido, String escalafon, java.time.LocalDateTime fecha) {
        return tarifaRepository.findAll().stream()
            .filter(t -> t.getActiva() != null && t.getActiva())
            .filter(t -> t.getTipoPartido().equalsIgnoreCase(tipoPartido))
            .filter(t -> t.getEscalafon().equalsIgnoreCase(escalafon))
            .filter(t -> !fecha.isBefore(t.getFechaVigenciaInicio()) && (t.getFechaVigenciaFin() == null || !fecha.isAfter(t.getFechaVigenciaFin())))
            .findFirst();
    }
}
