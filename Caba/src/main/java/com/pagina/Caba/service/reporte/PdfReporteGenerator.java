package com.pagina.Caba.service.reporte;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pagina.Caba.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementación de generación de reportes en formato PDF
 * 
 * Utiliza iText library para crear documentos PDF profesionales
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service("pdfReporteGenerator")
public class PdfReporteGenerator implements ReporteGenerator {
    
    // Fuentes personalizadas
    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FONT_SUBTITLE = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.GRAY);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
    
    // Colores corporativos
    private static final BaseColor COLOR_HEADER = new BaseColor(41, 128, 185); // Azul
    private static final BaseColor COLOR_ROW_ALT = new BaseColor(236, 240, 241); // Gris claro
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Override
    public ByteArrayOutputStream generarReporteLiquidaciones(List<?> liquidaciones, Map<String, Object> metadata) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Encabezado
        addHeader(document, "Reporte de Liquidaciones", metadata);
        
        // Tabla
        PdfPTable table = new PdfPTable(7); // 7 columnas
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2.5f, 2f, 1.5f, 1.5f, 1.5f, 2f});
        table.setSpacingBefore(10f);
        
        // Headers
        addTableHeader(table, new String[]{
            "ID", "Árbitro", "Asignación", "Monto", "Estado", "Fecha", "Método Pago"
        });
        
        // Datos
        for (Object obj : liquidaciones) {
            if (obj instanceof Liquidacion) {
                Liquidacion liq = (Liquidacion) obj;
                addTableCell(table, String.valueOf(liq.getId()));
                addTableCell(table, liq.getAsignacion().getArbitro().getNombreCompleto());
                addTableCell(table, "Asig #" + liq.getAsignacion().getId());
                addTableCell(table, String.format("$%.2f", liq.getMonto()));
                addTableCell(table, liq.estaPendiente() ? "Pendiente" : "Pagada");
                addTableCell(table, dateFormat.format(java.sql.Timestamp.valueOf(liq.getFechaCreacion())));
                addTableCell(table, liq.getMetodoPago() != null ? liq.getMetodoPago() : "N/A");
            }
        }
        
        document.add(table);
        
        // Footer
        addFooter(document);
        
        document.close();
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReporteArbitros(List<?> arbitros, Map<String, Object> metadata) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Encabezado
        addHeader(document, "Reporte de Árbitros", metadata);
        
        // Tabla
        PdfPTable table = new PdfPTable(7); // 7 columnas
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 2f, 2f, 1.5f, 1.5f, 1.5f});
        table.setSpacingBefore(10f);
        
        // Headers
        addTableHeader(table, new String[]{
            "ID", "Nombre", "Email", "Especialidad", "Escalafón", "Tarifa", "Estado"
        });
        
        // Datos
        for (Object obj : arbitros) {
            if (obj instanceof Arbitro) {
                Arbitro arb = (Arbitro) obj;
                addTableCell(table, String.valueOf(arb.getId()));
                addTableCell(table, arb.getNombreCompleto());
                addTableCell(table, arb.getEmail());
                addTableCell(table, arb.getEspecialidad() != null ? arb.getEspecialidad() : "N/A");
                addTableCell(table, arb.getEscalafon() != null ? arb.getEscalafon() : "N/A");
                addTableCell(table, String.format("$%.2f", arb.getTarifaBase()));
                addTableCell(table, arb.getActivo() ? "Activo" : "Inactivo");
            }
        }
        
        document.add(table);
        
        // Footer
        addFooter(document);
        
        document.close();
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReportePartidos(List<?> partidos, Map<String, Object> metadata) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 50, 50); // Landscape
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Encabezado
        addHeader(document, "Reporte de Partidos", metadata);
        
        // Tabla
        PdfPTable table = new PdfPTable(8); // 8 columnas
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2f, 2f, 2f, 2f, 2f, 1.5f, 1.5f});
        table.setSpacingBefore(10f);
        
        // Headers
        addTableHeader(table, new String[]{
            "ID", "Torneo", "Local", "Visitante", "Fecha/Hora", "Ubicación", "Resultado", "Estado"
        });
        
        // Datos
        for (Object obj : partidos) {
            if (obj instanceof Partido) {
                Partido partido = (Partido) obj;
                addTableCell(table, String.valueOf(partido.getId()));
                addTableCell(table, partido.getTorneo() != null ? partido.getTorneo().getNombre() : "N/A");
                addTableCell(table, partido.getEquipoLocal());
                addTableCell(table, partido.getEquipoVisitante());
                addTableCell(table, partido.getFechaPartido() != null ? dateTimeFormat.format(java.sql.Timestamp.valueOf(partido.getFechaPartido())) : "N/A");
                addTableCell(table, partido.getUbicacion());
                
                String resultado = "N/A";
                if (partido.estaCompletado() && partido.getMarcadorLocal() != null && partido.getMarcadorVisitante() != null) {
                    resultado = partido.getMarcadorLocal() + " - " + partido.getMarcadorVisitante();
                }
                addTableCell(table, resultado);
                addTableCell(table, partido.estaCompletado() ? "Completado" : "Pendiente");
            }
        }
        
        document.add(table);
        
        // Footer
        addFooter(document);
        
        document.close();
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReporteAsignaciones(List<?> asignaciones, Map<String, Object> metadata) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 50, 50); // Landscape
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Encabezado
        addHeader(document, "Reporte de Asignaciones", metadata);
        
        // Tabla
        PdfPTable table = new PdfPTable(7); // 7 columnas
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2.5f, 3f, 2f, 1.5f, 1.5f, 2f});
        table.setSpacingBefore(10f);
        
        // Headers
        addTableHeader(table, new String[]{
            "ID", "Árbitro", "Partido", "Rol", "Tarifa", "Estado", "Fecha Asignación"
        });
        
        // Datos
        for (Object obj : asignaciones) {
            if (obj instanceof Asignacion) {
                Asignacion asig = (Asignacion) obj;
                addTableCell(table, String.valueOf(asig.getId()));
                addTableCell(table, asig.getArbitro().getNombreCompleto());
                
                String partidoInfo = "N/A";
                if (asig.getPartido() != null) {
                    partidoInfo = asig.getPartido().getEquipoLocal() + " vs " + asig.getPartido().getEquipoVisitante();
                }
                addTableCell(table, partidoInfo);
                
                addTableCell(table, asig.getRolEspecifico() != null ? asig.getRolEspecifico() : "N/A");
                addTableCell(table, String.format("$%.2f", asig.getMontoCalculado()));
                addTableCell(table, asig.getEstado() != null ? asig.getEstado().toString() : "N/A");
                addTableCell(table, dateFormat.format(java.sql.Timestamp.valueOf(asig.getFechaAsignacion())));
            }
        }
        
        document.add(table);
        
        // Footer
        addFooter(document);
        
        document.close();
        return baos;
    }
    
    // Métodos auxiliares
    
    private void addHeader(Document document, String title, Map<String, Object> metadata) throws DocumentException {
        // Logo y título
        Paragraph titlePara = new Paragraph(title, FONT_TITLE);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(5);
        document.add(titlePara);
        
        // Subtítulo con fecha
        Paragraph subtitlePara = new Paragraph("Generado el " + dateTimeFormat.format(new Date()), FONT_SUBTITLE);
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        subtitlePara.setSpacingAfter(10);
        document.add(subtitlePara);
        
        // Metadata adicional si existe
        if (metadata != null && !metadata.isEmpty()) {
            Paragraph metaPara = new Paragraph();
            metaPara.setFont(FONT_SMALL);
            metaPara.setSpacingAfter(10);
            
            if (metadata.containsKey("filtros")) {
                metaPara.add(new Chunk("Filtros aplicados: " + metadata.get("filtros")));
            }
            if (metadata.containsKey("total")) {
                metaPara.add(new Chunk("\nTotal de registros: " + metadata.get("total")));
            }
            
            document.add(metaPara);
        }
        
        // Línea separadora
        Paragraph linePara = new Paragraph("_________________________________________________________________");
        linePara.setFont(FONT_SMALL);
        linePara.setAlignment(Element.ALIGN_CENTER);
        document.add(linePara);
        document.add(Chunk.NEWLINE);
    }
    
    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FONT_HEADER));
            cell.setBackgroundColor(COLOR_HEADER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_NORMAL));
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        // Alternar color de filas
        if (table.getRows().size() % 2 == 0) {
            cell.setBackgroundColor(COLOR_ROW_ALT);
        }
        
        table.addCell(cell);
    }
    
    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        
        Paragraph footer = new Paragraph("CABA Pro - Sistema de Gestión de Árbitros", FONT_SMALL);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        Paragraph footerPage = new Paragraph("Página 1 de 1", FONT_SMALL);
        footerPage.setAlignment(Element.ALIGN_CENTER);
        document.add(footerPage);
    }
    
    @Override
    public String getContentType() {
        return "application/pdf";
    }
    
    @Override
    public String getFileExtension() {
        return "pdf";
    }
    
    @Override
    public String getFormatName() {
        return "PDF";
    }
}
