
package com.pagina.Caba.service;

import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoLiquidacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.repository.LiquidacionRepository;
import com.pagina.Caba.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoLiquidacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.repository.LiquidacionRepository;
import com.pagina.Caba.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class LiquidacionService {
    public void deleteById(Long id) {
        liquidacionRepository.deleteById(id);
    }
    // Genera una liquidación para un árbitro en un rango de fechas (implementación básica de ejemplo)
    public LiquidacionDto generarLiquidacion(Long arbitroId, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        // Aquí deberías implementar la lógica real según tu modelo de negocio
        // Por ahora, solo lanza excepción para que compile
        throw new UnsupportedOperationException("No implementado");
    }

    // Aprueba una liquidación cambiando su estado a PAGADA (implementación básica de ejemplo)
    public LiquidacionDto aprobarLiquidacion(Long id) {
        // Aquí deberías implementar la lógica real según tu modelo de negocio
        throw new UnsupportedOperationException("No implementado");
    }

    // Obtiene liquidaciones de un árbitro específico (implementación básica de ejemplo)
    public List<LiquidacionDto> getLiquidacionesByArbitro(Long arbitroId) {
        // Aquí deberías implementar la lógica real según tu modelo de negocio
        throw new UnsupportedOperationException("No implementado");
    }

    // Obtiene liquidaciones por estado (implementación básica de ejemplo)
    public List<LiquidacionDto> getLiquidacionesByEstado(EstadoLiquidacion estado) {
        // Aquí deberías implementar la lógica real según tu modelo de negocio
        throw new UnsupportedOperationException("No implementado");
    }

    // Obtiene liquidaciones en un rango de fechas (implementación básica de ejemplo)
    public List<LiquidacionDto> getLiquidacionesByDateRange(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        // Aquí deberías implementar la lógica real según tu modelo de negocio
        throw new UnsupportedOperationException("No implementado");
    }
    @Autowired
    private LiquidacionRepository liquidacionRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    // CRUD Básico (entidad)
    public List<Liquidacion> obtenerTodas() {
        return liquidacionRepository.findAll();
    }

    public Optional<Liquidacion> obtenerPorId(Long id) {
        return liquidacionRepository.findById(id);
    }

    public Liquidacion guardar(Liquidacion liquidacion) {
        return liquidacionRepository.save(liquidacion);
    }

    // Métodos para trabajar con LiquidacionDto
    public List<LiquidacionDto> findAll() {
        return liquidacionRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<LiquidacionDto> findById(Long id) {
        return liquidacionRepository.findById(id).map(this::toDto);
    }

    public LiquidacionDto save(LiquidacionDto dto) {
        Liquidacion liquidacion = fromDto(dto);
        Liquidacion saved = liquidacionRepository.save(liquidacion);
        return toDto(saved);
    }













    // Conversión entidad <-> DTO
    private LiquidacionDto toDto(Liquidacion l) {
        LiquidacionDto dto = new LiquidacionDto();
        dto.setId(l.getId());
        dto.setAsignacionId(l.getAsignacion() != null ? l.getAsignacion().getId() : null);
        dto.setMonto(l.getMonto());
        dto.setEstado(l.getEstado() != null ? l.getEstado().name() : null);
        dto.setFechaCreacion(l.getFechaCreacion());
        dto.setFechaPago(l.getFechaPago());
        dto.setMetodoPago(l.getMetodoPago());
        dto.setReferenciaPago(l.getReferenciaPago());
        dto.setObservaciones(l.getObservaciones());
        return dto;
    }

    private Liquidacion fromDto(LiquidacionDto dto) {
        Liquidacion l = new Liquidacion();
        l.setId(dto.getId());
        l.setMonto(dto.getMonto());
        l.setEstado(dto.getEstado() != null ? EstadoLiquidacion.valueOf(dto.getEstado()) : null);
        l.setFechaCreacion(dto.getFechaCreacion());
        l.setFechaPago(dto.getFechaPago());
        l.setMetodoPago(dto.getMetodoPago());
        l.setReferenciaPago(dto.getReferenciaPago());
        l.setObservaciones(dto.getObservaciones());
        // Asignacion debe ser seteada desde el repositorio si corresponde
        if (dto.getAsignacionId() != null) {
            l.setAsignacion(asignacionRepository.findById(dto.getAsignacionId()).orElse(null));
        }
        return l;
    }

    // Lógica de Negocio


    public void marcarComoPagada(Long liquidacionId, String metodoPago, String referenciaPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }
        Optional<Liquidacion> liquidacionOpt = liquidacionRepository.findById(liquidacionId);
        if (liquidacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Liquidación no encontrada");
        }
        Liquidacion liquidacion = liquidacionOpt.get();
        // Marcar como pagada
        liquidacion.marcarComoPagada(metodoPago, referenciaPago);
        liquidacionRepository.save(liquidacion);
    }

    public void cancelarLiquidacion(Long liquidacionId, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de cancelación es obligatorio");
        }
        Optional<Liquidacion> liquidacionOpt = liquidacionRepository.findById(liquidacionId);
        if (liquidacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Liquidación no encontrada");
        }
        Liquidacion liquidacion = liquidacionOpt.get();
        // Cancelar
        liquidacion.cancelar(motivo);
        liquidacionRepository.save(liquidacion);
    }

    // Estadísticas y reportes
    public long contarLiquidacionesPorEstado(EstadoLiquidacion estado) {
        return liquidacionRepository.countByEstado(estado);
    }

    public BigDecimal calcularTotalPendientePago() {
        BigDecimal total = liquidacionRepository.sumMontoByEstado(EstadoLiquidacion.PENDIENTE);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calcularTotalPagado() {
        BigDecimal total = liquidacionRepository.sumMontoByEstado(EstadoLiquidacion.PAGADA);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Liquidacion> obtenerLiquidacionesPorArbitro(Long arbitroId) {
        return liquidacionRepository.findLiquidacionesByArbitroId(arbitroId);
    }
}