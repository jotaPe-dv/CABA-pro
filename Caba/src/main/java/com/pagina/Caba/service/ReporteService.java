package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import com.pagina.Caba.service.reporte.ReporteGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Servicio orquestador para generación de reportes
 * 
 * Este servicio actúa como fachada (Facade Pattern) para la generación de reportes,
 * implementando el principio de Inversión de Dependencias (DIP):
 * - Depende de la abstracción ReporteGenerator (interfaz)
 * - No conoce los detalles de implementación de PDF o Excel
 * - Selecciona dinámicamente el generador según el formato solicitado
 * 
 * Beneficios:
 * - Desacopla el controller de la lógica de negocio de reportes
 * - Facilita testing (se pueden mockear los generadores)
 * - Simplifica el controller (responsabilidad única)
 * - Permite agregar nuevos formatos sin modificar el controller
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service
public class ReporteService {
    
    @Autowired
    @Qualifier("pdfReporteGenerator")
    private ReporteGenerator pdfGenerator;
    
    @Autowired
    @Qualifier("excelReporteGenerator")
    private ReporteGenerator excelGenerator;
    
    @Autowired
    private LiquidacionRepository liquidacionRepository;
    
    @Autowired
    private ArbitroRepository arbitroRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private AsignacionRepository asignacionRepository;
    
    @Autowired
    private TorneoRepository torneoRepository;
    
    /**
     * Genera reporte de liquidaciones con filtros opcionales
     */
    public ReporteResultado generarReporteLiquidaciones(String formato, String estado) throws Exception {
        // Obtener datos
        List<Liquidacion> liquidaciones;
        if (estado != null && !estado.isEmpty()) {
            liquidaciones = liquidacionRepository.findByEstado(EstadoLiquidacion.valueOf(estado.toUpperCase()));
        } else {
            liquidaciones = liquidacionRepository.findAll();
        }
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("total", liquidaciones.size());
        if (estado != null) {
            metadata.put("filtros", "Estado: " + estado);
        }
        
        // Seleccionar generador y generar reporte
        ReporteGenerator generator = seleccionarGenerador(formato);
        ByteArrayOutputStream baos = generator.generarReporteLiquidaciones(liquidaciones, metadata);
        
        return new ReporteResultado(
            baos.toByteArray(),
            generator.getContentType(),
            "liquidaciones_" + System.currentTimeMillis() + "." + generator.getFileExtension()
        );
    }
    
    /**
     * Genera reporte de árbitros con filtros opcionales
     */
    public ReporteResultado generarReporteArbitros(String formato, Boolean disponible, String especialidad) throws Exception {
        // Obtener datos
        List<Arbitro> arbitros = arbitroRepository.findAll();
        
        // Aplicar filtros
        if (disponible != null) {
            arbitros = arbitros.stream()
                .filter(a -> a.getDisponible().equals(disponible))
                .toList();
        }
        
        if (especialidad != null && !especialidad.isEmpty()) {
            arbitros = arbitros.stream()
                .filter(a -> especialidad.equalsIgnoreCase(a.getEspecialidad()))
                .toList();
        }
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("total", arbitros.size());
        List<String> filtros = new ArrayList<>();
        if (disponible != null) {
            filtros.add("Disponible: " + disponible);
        }
        if (especialidad != null) {
            filtros.add("Especialidad: " + especialidad);
        }
        if (!filtros.isEmpty()) {
            metadata.put("filtros", String.join(", ", filtros));
        }
        
        // Seleccionar generador y generar reporte
        ReporteGenerator generator = seleccionarGenerador(formato);
        ByteArrayOutputStream baos = generator.generarReporteArbitros(arbitros, metadata);
        
        return new ReporteResultado(
            baos.toByteArray(),
            generator.getContentType(),
            "arbitros_" + System.currentTimeMillis() + "." + generator.getFileExtension()
        );
    }
    
    /**
     * Genera reporte de partidos con filtros opcionales
     */
    public ReporteResultado generarReportePartidos(String formato, Long torneoId, Boolean completado) throws Exception {
        // Obtener datos
        List<Partido> partidos;
        if (torneoId != null) {
            Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
            if (torneo != null) {
                partidos = partidoRepository.findByTorneo(torneo);
            } else {
                partidos = new ArrayList<>();
            }
        } else {
            partidos = partidoRepository.findAll();
        }
        
        // Aplicar filtros
        if (completado != null) {
            partidos = partidos.stream()
                .filter(p -> p.getCompletado().equals(completado))
                .toList();
        }
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("total", partidos.size());
        List<String> filtros = new ArrayList<>();
        if (torneoId != null) {
            filtros.add("Torneo ID: " + torneoId);
        }
        if (completado != null) {
            filtros.add("Completado: " + completado);
        }
        if (!filtros.isEmpty()) {
            metadata.put("filtros", String.join(", ", filtros));
        }
        
        // Seleccionar generador y generar reporte
        ReporteGenerator generator = seleccionarGenerador(formato);
        ByteArrayOutputStream baos = generator.generarReportePartidos(partidos, metadata);
        
        return new ReporteResultado(
            baos.toByteArray(),
            generator.getContentType(),
            "partidos_" + System.currentTimeMillis() + "." + generator.getFileExtension()
        );
    }
    
    /**
     * Genera reporte de asignaciones con filtros opcionales
     */
    public ReporteResultado generarReporteAsignaciones(String formato, String estado, Long arbitroId) throws Exception {
        // Obtener datos
        List<Asignacion> asignaciones;
        if (arbitroId != null) {
            Arbitro arbitro = arbitroRepository.findById(arbitroId).orElse(null);
            if (arbitro != null) {
                asignaciones = asignacionRepository.findByArbitro(arbitro);
            } else {
                asignaciones = new ArrayList<>();
            }
        } else {
            asignaciones = asignacionRepository.findAll();
        }
        
        // Aplicar filtros
        if (estado != null && !estado.isEmpty()) {
            EstadoAsignacion estadoEnum = EstadoAsignacion.valueOf(estado.toUpperCase());
            asignaciones = asignaciones.stream()
                .filter(a -> a.getEstado().equals(estadoEnum))
                .toList();
        }
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("total", asignaciones.size());
        List<String> filtros = new ArrayList<>();
        if (arbitroId != null) {
            filtros.add("Árbitro ID: " + arbitroId);
        }
        if (estado != null) {
            filtros.add("Estado: " + estado);
        }
        if (!filtros.isEmpty()) {
            metadata.put("filtros", String.join(", ", filtros));
        }
        
        // Seleccionar generador y generar reporte
        ReporteGenerator generator = seleccionarGenerador(formato);
        ByteArrayOutputStream baos = generator.generarReporteAsignaciones(asignaciones, metadata);
        
        return new ReporteResultado(
            baos.toByteArray(),
            generator.getContentType(),
            "asignaciones_" + System.currentTimeMillis() + "." + generator.getFileExtension()
        );
    }
    
    /**
     * Obtiene información sobre los formatos disponibles
     */
    public Map<String, Map<String, String>> obtenerFormatosDisponibles() {
        Map<String, Map<String, String>> formatos = new HashMap<>();
        
        Map<String, String> pdf = new HashMap<>();
        pdf.put("nombre", pdfGenerator.getFormatName());
        pdf.put("extension", pdfGenerator.getFileExtension());
        pdf.put("contentType", pdfGenerator.getContentType());
        
        Map<String, String> excel = new HashMap<>();
        excel.put("nombre", excelGenerator.getFormatName());
        excel.put("extension", excelGenerator.getFileExtension());
        excel.put("contentType", excelGenerator.getContentType());
        
        formatos.put("pdf", pdf);
        formatos.put("excel", excel);
        
        return formatos;
    }
    
    /**
     * Selecciona el generador apropiado según el formato
     */
    private ReporteGenerator seleccionarGenerador(String formato) {
        if ("excel".equalsIgnoreCase(formato) || "xlsx".equalsIgnoreCase(formato)) {
            return excelGenerator;
        }
        return pdfGenerator; // Default: PDF
    }
    
    /**
     * Clase interna para encapsular el resultado de la generación de reportes
     */
    public static class ReporteResultado {
        private final byte[] contenido;
        private final String contentType;
        private final String nombreArchivo;
        
        public ReporteResultado(byte[] contenido, String contentType, String nombreArchivo) {
            this.contenido = contenido;
            this.contentType = contentType;
            this.nombreArchivo = nombreArchivo;
        }
        
        public byte[] getContenido() { return contenido; }
        public String getContentType() { return contentType; }
        public String getNombreArchivo() { return nombreArchivo; }
    }
}
