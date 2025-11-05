package com.pagina.Caba.service.reporte;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interfaz para generación de reportes
 * 
 * Implementa el principio de Inversión de Dependencias (DIP):
 * - Los módulos de alto nivel (controllers/services) no dependen de módulos de bajo nivel (PDF/Excel)
 * - Ambos dependen de abstracciones (esta interfaz)
 * - Las abstracciones no dependen de detalles, los detalles dependen de abstracciones
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface ReporteGenerator {
    
    /**
     * Genera un reporte de liquidaciones
     * 
     * @param liquidaciones Lista de liquidaciones a incluir en el reporte
     * @param metadata Información adicional (título, fecha, filtros aplicados, etc.)
     * @return ByteArrayOutputStream con el reporte generado
     * @throws Exception Si ocurre un error durante la generación
     */
    ByteArrayOutputStream generarReporteLiquidaciones(List<?> liquidaciones, Map<String, Object> metadata) throws Exception;
    
    /**
     * Genera un reporte de árbitros
     * 
     * @param arbitros Lista de árbitros a incluir en el reporte
     * @param metadata Información adicional (título, fecha, filtros aplicados, etc.)
     * @return ByteArrayOutputStream con el reporte generado
     * @throws Exception Si ocurre un error durante la generación
     */
    ByteArrayOutputStream generarReporteArbitros(List<?> arbitros, Map<String, Object> metadata) throws Exception;
    
    /**
     * Genera un reporte de partidos
     * 
     * @param partidos Lista de partidos a incluir en el reporte
     * @param metadata Información adicional (título, fecha, filtros aplicados, etc.)
     * @return ByteArrayOutputStream con el reporte generado
     * @throws Exception Si ocurre un error durante la generación
     */
    ByteArrayOutputStream generarReportePartidos(List<?> partidos, Map<String, Object> metadata) throws Exception;
    
    /**
     * Genera un reporte de asignaciones
     * 
     * @param asignaciones Lista de asignaciones a incluir en el reporte
     * @param metadata Información adicional (título, fecha, filtros aplicados, etc.)
     * @return ByteArrayOutputStream con el reporte generado
     * @throws Exception Si ocurre un error durante la generación
     */
    ByteArrayOutputStream generarReporteAsignaciones(List<?> asignaciones, Map<String, Object> metadata) throws Exception;
    
    /**
     * Obtiene el tipo MIME del formato de reporte
     * 
     * @return String con el tipo MIME (ej: "application/pdf", "application/vnd.ms-excel")
     */
    String getContentType();
    
    /**
     * Obtiene la extensión de archivo para este formato
     * 
     * @return String con la extensión (ej: "pdf", "xlsx")
     */
    String getFileExtension();
    
    /**
     * Obtiene el nombre descriptivo del formato
     * 
     * @return String con el nombre del formato (ej: "PDF", "Excel")
     */
    String getFormatName();
}
