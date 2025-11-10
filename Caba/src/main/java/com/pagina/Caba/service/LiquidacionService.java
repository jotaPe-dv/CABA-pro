
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




@Service
@Transactional
public class LiquidacionService {
    public void deleteById(Long id) {
        liquidacionRepository.deleteById(id);
    }
    //
    // Genera una liquidación para un árbitro en un rango de fechas (implementación básica de ejemplo)
    /**
     * Genera una liquidación para un árbitro en un rango de fechas, calculando el monto
     * según la especialidad y escalafón del árbitro y la tarifa base de la asignación.
     */
    public LiquidacionDto generarLiquidacion(Long arbitroId, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        // Buscar asignaciones COMPLETADAS del árbitro en el rango de fechas
        List<Asignacion> asignaciones = asignacionRepository.findAll().stream()
            .filter(a -> a.getArbitro() != null && a.getArbitro().getId().equals(arbitroId))
            .filter(a -> a.getEstado() == EstadoAsignacion.COMPLETADA)
            .filter(a -> a.getPartido() != null &&
                a.getPartido().getFechaPartido() != null &&
                !a.getPartido().getFechaPartido().toLocalDate().isBefore(fechaInicio) &&
                !a.getPartido().getFechaPartido().toLocalDate().isAfter(fechaFin))
            .toList();

        if (asignaciones.isEmpty()) {
            throw new IllegalArgumentException("No hay asignaciones completadas para el árbitro en el rango de fechas");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Asignacion asignacion : asignaciones) {
            BigDecimal monto = calcularMontoLiquidacion(asignacion);
            total = total.add(monto);
        }

        // Crear una liquidación (puede asociarse a la última asignación del rango)
        Asignacion ultima = asignaciones.get(asignaciones.size() - 1);
        Liquidacion liquidacion = new Liquidacion(ultima, total);
        liquidacion.setEstado(EstadoLiquidacion.PENDIENTE);
        liquidacionRepository.save(liquidacion);
        return toDto(liquidacion);
    }

    /**
     * Calcula el monto de liquidación para una asignación según reglas de especialidad y escalafón.
     * Puedes ajustar los multiplicadores según las reglas de negocio.
     */
    private BigDecimal calcularMontoLiquidacion(Asignacion asignacion) {
        BigDecimal base = asignacion.getTarifa() != null ? asignacion.getTarifa().getMonto() : BigDecimal.ZERO;
        if (asignacion.getArbitro() == null) return base;
        String especialidad = asignacion.getArbitro().getEspecialidad();
        String escalafon = asignacion.getArbitro().getEscalafon();

        // Multiplicadores de ejemplo (ajustar según reglas reales)
        BigDecimal multEspecialidad = BigDecimal.ONE;
        if ("Principal".equalsIgnoreCase(especialidad)) multEspecialidad = new BigDecimal("1.2");
        else if ("Auxiliar".equalsIgnoreCase(especialidad)) multEspecialidad = new BigDecimal("1.0");
        else if ("Mesa".equalsIgnoreCase(especialidad)) multEspecialidad = new BigDecimal("0.9");

        BigDecimal multEscalafon = BigDecimal.ONE;
        if ("A".equalsIgnoreCase(escalafon)) multEscalafon = new BigDecimal("1.15");
        else if ("B".equalsIgnoreCase(escalafon)) multEscalafon = new BigDecimal("1.05");
        else if ("C".equalsIgnoreCase(escalafon)) multEscalafon = new BigDecimal("1.00");

        return base.multiply(multEspecialidad).multiply(multEscalafon);
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
    
    /**
     * Genera liquidaciones automáticas para todas las asignaciones de un partido.
     * Este método se llama cuando un partido se marca como completado.
     * Genera liquidaciones para todas las asignaciones, independientemente de su estado.
     * Usa REQUIRES_NEW para ejecutarse en una transacción independiente.
     * 
     * @param partidoId ID del partido completado
     * @return Lista de liquidaciones generadas (una por cada árbitro asignado)
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public List<Liquidacion> generarLiquidacionesParaPartido(Long partidoId) {
        // Buscar todas las asignaciones del partido (sin filtrar por estado)
        List<Asignacion> asignaciones = asignacionRepository.findAll().stream()
            .filter(a -> a.getPartido() != null && a.getPartido().getId().equals(partidoId))
            .toList();
        
        if (asignaciones.isEmpty()) {
            throw new IllegalArgumentException("No hay asignaciones para el partido #" + partidoId);
        }
        
        List<Liquidacion> liquidacionesGeneradas = new java.util.ArrayList<>();
        
        for (Asignacion asignacion : asignaciones) {
            // Verificar si ya existe una liquidación para esta asignación
            if (asignacion.getLiquidacion() != null) {
                // Ya existe liquidación, agregarla a la lista
                liquidacionesGeneradas.add(asignacion.getLiquidacion());
                continue;
            }
            
            // Calcular el monto según la tarifa y las reglas de negocio
            BigDecimal monto = calcularMontoLiquidacion(asignacion);
            
            // Crear nueva liquidación
            Liquidacion liquidacion = new Liquidacion(asignacion, monto);
            liquidacion.setEstado(EstadoLiquidacion.PENDIENTE);
            liquidacion.setObservaciones("Liquidación generada automáticamente al completar el partido");
            
            // Guardar liquidación
            Liquidacion saved = liquidacionRepository.save(liquidacion);
            liquidacionesGeneradas.add(saved);
            
            // Marcar asignación como COMPLETADA (solo si estaba ACEPTADA, sino dejarla como está)
            if (asignacion.getEstado() == EstadoAsignacion.ACEPTADA) {
                asignacion.completar();
                asignacionRepository.save(asignacion);
            } else {
                // Si no estaba aceptada, solo actualizar el estado a COMPLETADA directamente
                asignacion.setEstado(EstadoAsignacion.COMPLETADA);
                asignacionRepository.save(asignacion);
            }
        }
        
        return liquidacionesGeneradas;
    }
    
    /**
     * Envía las liquidaciones a los árbitros (marca como enviadas)
     * 
     * @param liquidacionIds IDs de las liquidaciones a enviar
     */
    public void enviarLiquidaciones(List<Long> liquidacionIds) {
        for (Long id : liquidacionIds) {
            Optional<Liquidacion> liquidacionOpt = liquidacionRepository.findById(id);
            if (liquidacionOpt.isPresent()) {
                Liquidacion liquidacion = liquidacionOpt.get();
                // Aquí podrías agregar lógica para enviar email, notificación, etc.
                // Por ahora solo guardamos con observación
                if (liquidacion.getObservaciones() == null || liquidacion.getObservaciones().isEmpty()) {
                    liquidacion.setObservaciones("Liquidación enviada al árbitro");
                } else {
                    liquidacion.setObservaciones(liquidacion.getObservaciones() + ". Enviada al árbitro.");
                }
                liquidacionRepository.save(liquidacion);
            }
        }
    }
    
    /**
     * Obtener liquidaciones por árbitro y estado (para API REST)
     */
    public List<LiquidacionDto> obtenerPorArbitroYEstado(Long arbitroId, String estado) {
        List<Liquidacion> liquidaciones = liquidacionRepository.findAll().stream()
            .filter(l -> l.getAsignacion() != null && 
                        l.getAsignacion().getArbitro() != null && 
                        l.getAsignacion().getArbitro().getId().equals(arbitroId))
            .toList();
        
        EstadoLiquidacion estadoEnum;
        try {
            estadoEnum = EstadoLiquidacion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + estado);
        }
        
        return liquidaciones.stream()
            .filter(l -> l.getEstado() == estadoEnum)
            .map(this::toDto)
            .toList();
    }
    
    /**
     * Obtener liquidaciones por árbitro (para API REST)
     */
    public List<LiquidacionDto> obtenerPorArbitro(Long arbitroId) {
        List<Liquidacion> liquidaciones = liquidacionRepository.findAll().stream()
            .filter(l -> l.getAsignacion() != null && 
                        l.getAsignacion().getArbitro() != null && 
                        l.getAsignacion().getArbitro().getId().equals(arbitroId))
            .toList();
        
        return liquidaciones.stream()
            .map(this::toDto)
            .toList();
    }
}
