package com.pagina.Caba.service;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {
    @Autowired
    private AsignacionRepository asignacionRepository;

    public List<Asignacion> findByPartidoId(Long partidoId) {
        return asignacionRepository.findByPartidoId(partidoId);
    }

    public List<Asignacion> findByArbitroIdAndEstado(Long arbitroId, EstadoAsignacion estado) {
        return asignacionRepository.findByArbitroIdAndEstado(arbitroId, estado);
    }

    public List<Asignacion> findByArbitroId(Long arbitroId) {
        return asignacionRepository.findByArbitroId(arbitroId);
    }

    public Optional<Asignacion> findById(Long id) {
        return asignacionRepository.findById(id);
    }

    @Autowired
    private com.pagina.Caba.service.LiquidacionService liquidacionService;

    @Transactional
    public void aceptarAsignacion(Long asignacionId, Long arbitroId) {
        Asignacion asignacion = findById(asignacionId)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        // Verificar que la asignación pertenece al árbitro
        if (!asignacion.getArbitro().getId().equals(arbitroId)) {
            throw new RuntimeException("No autorizado");
        }

        asignacion.aceptar();
        asignacionRepository.save(asignacion);

        // Crear liquidación automáticamente
        java.math.BigDecimal monto = java.math.BigDecimal.valueOf(asignacion.getPagoCalculado() != null ? asignacion.getPagoCalculado() : 0.0f);
        com.pagina.Caba.model.Liquidacion liquidacion = new com.pagina.Caba.model.Liquidacion(
            java.time.LocalDate.now(),
            monto,
            com.pagina.Caba.model.EstadoLiquidacion.PENDIENTE,
            asignacion.getArbitro()
        );
        liquidacionService.save(liquidacion);
    }

    @Transactional
    public void rechazarAsignacion(Long asignacionId, Long arbitroId) {
        Asignacion asignacion = findById(asignacionId)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        
        // Verificar que la asignación pertenece al árbitro
        if (!asignacion.getArbitro().getId().equals(arbitroId)) {
            throw new RuntimeException("No autorizado");
        }
        
        asignacion.rechazar();
        asignacionRepository.save(asignacion);
    }

    public List<Asignacion> findAll() {
        return asignacionRepository.findAll();
    }

    public Asignacion save(Asignacion asignacion) {
        return asignacionRepository.save(asignacion);
    }
}
