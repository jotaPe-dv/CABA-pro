package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsignacionService {
    
    @Autowired
    private AsignacionRepository asignacionRepository;
    
    @Autowired
    private ArbitroRepository arbitroRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    


    @Autowired
    private TarifaService tarifaService;
    
    // CRUD Básico
    public List<Asignacion> obtenerTodas() {
        List<Asignacion> asignaciones = asignacionRepository.findAll();
        System.out.println("[DEBUG] AsignacionService.obtenerTodas() - Retornando " + asignaciones.size() + " asignaciones");
        return asignaciones;
    }
    
    public Optional<Asignacion> obtenerPorId(Long id) {
        return asignacionRepository.findById(id);
    }
    
    public Asignacion guardar(Asignacion asignacion) {
        System.out.println("[DEBUG] AsignacionService.guardar() - Guardando asignación: " + 
                          "Partido ID=" + asignacion.getPartido().getId() + 
                          ", Arbitro=" + asignacion.getArbitro().getNombre() + 
                          ", Rol=" + asignacion.getRolEspecifico());
        Asignacion resultado = asignacionRepository.save(asignacion);
        System.out.println("[DEBUG] AsignacionService.guardar() - Asignación guardada con ID: " + resultado.getId());
        return resultado;
    }
    
    // Lógica de Negocio Principal
    public Asignacion crearAsignacion(Long partidoId, Long arbitroId, String tipoPartido) {
        // Obtener entidades
        Optional<Partido> partidoOpt = partidoRepository.findById(partidoId);
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (partidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Partido no encontrado");
        }
        if (arbitroOpt.isEmpty()) {
            throw new IllegalArgumentException("Árbitro no encontrado");
        }
        Partido partido = partidoOpt.get();
        Arbitro arbitro = arbitroOpt.get();
        // Validaciones
        validarAsignacion(partido, arbitro);
        // Buscar tarifa automáticamente según tipo de partido y escalafón del árbitro
        String escalafon = arbitro.getEscalafon();
        String tipo = tipoPartido != null ? tipoPartido : partido.getTipoPartido();
        java.time.LocalDateTime fecha = partido.getFechaPartido();
        Optional<Tarifa> tarifaOpt = tarifaService.buscarTarifaParaAsignacion(tipo, escalafon, fecha);
        if (tarifaOpt.isEmpty()) {
            throw new IllegalArgumentException("No hay tarifa vigente para el tipo de partido y escalafón del árbitro");
        }
        Tarifa tarifa = tarifaOpt.get();
        // Crear asignación
        Asignacion asignacion = new Asignacion(partido, arbitro, tarifa);
        return asignacionRepository.save(asignacion);
    }
    
    /**
     * Método principal para que un árbitro ACEPTE una asignación
     */
    public void aceptarAsignacion(Long asignacionId, String comentarios) {
        Optional<Asignacion> asignacionOpt = asignacionRepository.findById(asignacionId);
        
        if (asignacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Asignación no encontrada");
        }
        
        Asignacion asignacion = asignacionOpt.get();
        
        // Verificar que se puede aceptar
        if (asignacion.getEstado() != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aceptar asignaciones pendientes");
        }
        
        // Verificar que el partido no haya pasado
        if (asignacion.getPartido().getFechaPartido().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede aceptar una asignación de un partido que ya pasó");
        }
        
        // Aceptar la asignación
        asignacion.aceptar();
        if (comentarios != null && !comentarios.trim().isEmpty()) {
            asignacion.setComentarios(comentarios);
        }
        
        asignacionRepository.save(asignacion);
    }
    
    /**
     * Método principal para que un árbitro RECHACE una asignación
     */
    public void rechazarAsignacion(Long asignacionId, String motivoRechazo) {
        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }
        
        Optional<Asignacion> asignacionOpt = asignacionRepository.findById(asignacionId);
        
        if (asignacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Asignación no encontrada");
        }
        
        Asignacion asignacion = asignacionOpt.get();
        
        // Verificar que se puede rechazar
        if (asignacion.getEstado() != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar asignaciones pendientes");
        }
        
        // Rechazar la asignación
        asignacion.rechazar(motivoRechazo);
        asignacionRepository.save(asignacion);
    }
    
    /**
     * Completar una asignación después del partido
     */
    public void completarAsignacion(Long asignacionId) {
        Optional<Asignacion> asignacionOpt = asignacionRepository.findById(asignacionId);
        
        if (asignacionOpt.isEmpty()) {
            throw new IllegalArgumentException("Asignación no encontrada");
        }
        
        Asignacion asignacion = asignacionOpt.get();
        
        // Verificar que se puede completar
        if (asignacion.getEstado() != EstadoAsignacion.ACEPTADA) {
            throw new IllegalStateException("Solo se pueden completar asignaciones aceptadas");
        }
        
        // Completar
        asignacion.completar();
        asignacionRepository.save(asignacion);
    }
    
    // Consultas específicas
    public List<Asignacion> obtenerAsignacionesPorArbitro(Long arbitroId) {
        Optional<Arbitro> arbitro = arbitroRepository.findById(arbitroId);
        return arbitro.map(asignacionRepository::findByArbitro).orElse(List.of());
    }
    
    public List<Asignacion> obtenerAsignacionesPorPartido(Long partidoId) {
        Optional<Partido> partido = partidoRepository.findById(partidoId);
        return partido.map(asignacionRepository::findByPartido).orElse(List.of());
    }
    
    public List<Asignacion> obtenerAsignacionesPorEstado(EstadoAsignacion estado) {
        return asignacionRepository.findByEstado(estado);
    }
    
    public List<Asignacion> obtenerAsignacionesPendientesArbitro(Long arbitroId) {
        Optional<Arbitro> arbitro = arbitroRepository.findById(arbitroId);
        return arbitro.map(a -> asignacionRepository.findByArbitroAndEstado(a, EstadoAsignacion.PENDIENTE))
                     .orElse(List.of());
    }
    
    public List<Asignacion> obtenerAsignacionesVencidas() {
        return asignacionRepository.findAsignacionesVencidas(
            EstadoAsignacion.PENDIENTE, LocalDateTime.now());
    }
    
    // Estadísticas
    public long contarAsignacionesPorEstado(EstadoAsignacion estado) {
        return asignacionRepository.countByEstado(estado);
    }
    
    public long contarAsignacionesActivasArbitro(Long arbitroId) {
        Optional<Arbitro> arbitro = arbitroRepository.findById(arbitroId);
        if (arbitro.isPresent()) {
            List<EstadoAsignacion> estadosActivos = List.of(
                EstadoAsignacion.PENDIENTE, 
                EstadoAsignacion.ACEPTADA
            );
            return asignacionRepository.countAsignacionesActivasByArbitro(arbitro.get(), estadosActivos);
        }
        return 0;
    }
    
    /**
     * Obtiene las asignaciones rechazadas de un partido específico
     */
    public List<Asignacion> obtenerAsignacionesRechazadas(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
            .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));
        return asignacionRepository.findAsignacionesRechazadasByPartido(partido);
    }
    
    /**
     * Reasigna un árbitro que rechazó una asignación
     */
    @Transactional
    public Asignacion reasignarArbitro(Long asignacionId, Long nuevoArbitroId) {
        // Obtener la asignación rechazada
        Asignacion asignacionRechazada = asignacionRepository.findById(asignacionId)
            .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));
        
        if (asignacionRechazada.getEstado() != EstadoAsignacion.RECHAZADA) {
            throw new IllegalStateException("Solo se pueden reasignar asignaciones rechazadas");
        }
        
        // Obtener el nuevo árbitro
        Arbitro nuevoArbitro = arbitroRepository.findById(nuevoArbitroId)
            .orElseThrow(() -> new IllegalArgumentException("Árbitro no encontrado"));
        
        // Validar el nuevo árbitro
        validarAsignacion(asignacionRechazada.getPartido(), nuevoArbitro);
        
        // Crear nueva asignación con el nuevo árbitro
        Asignacion nuevaAsignacion = new Asignacion();
        nuevaAsignacion.setPartido(asignacionRechazada.getPartido());
        nuevaAsignacion.setArbitro(nuevoArbitro);
        nuevaAsignacion.setRolEspecifico(asignacionRechazada.getRolEspecifico());
        nuevaAsignacion.setTarifa(asignacionRechazada.getTarifa());
        nuevaAsignacion.setMontoCalculado(asignacionRechazada.getMontoCalculado());
        nuevaAsignacion.setEstado(EstadoAsignacion.PENDIENTE);
        nuevaAsignacion.setFechaAsignacion(LocalDateTime.now());
        
        return asignacionRepository.save(nuevaAsignacion);
    }
    
    // Validaciones privadas
    private void validarAsignacion(Partido partido, Arbitro arbitro) {
        // Verificar que el árbitro esté disponible
        if (!arbitro.getDisponible() || !arbitro.getActivo()) {
            throw new IllegalStateException("El árbitro no está disponible");
        }
        
        // Verificar que el partido no haya pasado
        if (partido.getFechaPartido().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("No se puede asignar un árbitro a un partido que ya pasó");
        }
        
        // Verificar que no exista ya una asignación aceptada para este partido
        Optional<Asignacion> asignacionExistente = asignacionRepository.findByArbitroAndPartido(arbitro, partido);
        if (asignacionExistente.isPresent() && 
            asignacionExistente.get().getEstado() != EstadoAsignacion.RECHAZADA) {
            throw new IllegalStateException("Ya existe una asignación para este árbitro en este partido");
        }
    }
}