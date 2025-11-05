package com.pagina.Caba.service.reporte;

import com.pagina.Caba.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementación de generación de reportes en formato Excel (.xlsx)
 * 
 * Utiliza Apache POI library para crear hojas de cálculo Excel
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service("excelReporteGenerator")
public class ExcelReporteGenerator implements ReporteGenerator {
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Override
    public ByteArrayOutputStream generarReporteLiquidaciones(List<?> liquidaciones, Map<String, Object> metadata) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Liquidaciones");
        
        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Encabezado del reporte
        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE LIQUIDACIONES");
        titleCell.setCellStyle(createTitleStyle(workbook));
        
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("Generado el: " + dateTimeFormat.format(new Date()));
        rowNum++; // Fila vacía
        
        // Headers de columnas
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Árbitro", "Asignación", "Monto", "Estado", "Fecha", "Método Pago"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        for (Object obj : liquidaciones) {
            if (obj instanceof Liquidacion) {
                Liquidacion liq = (Liquidacion) obj;
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(liq.getId());
                row.createCell(1).setCellValue(liq.getAsignacion().getArbitro().getNombreCompleto());
                row.createCell(2).setCellValue("Asig #" + liq.getAsignacion().getId());
                
                Cell montoCell = row.createCell(3);
                montoCell.setCellValue(liq.getMonto().doubleValue());
                montoCell.setCellStyle(currencyStyle);
                
                row.createCell(4).setCellValue(liq.estaPendiente() ? "Pendiente" : "Pagada");
                row.createCell(5).setCellValue(dateFormat.format(java.sql.Timestamp.valueOf(liq.getFechaCreacion())));
                row.createCell(6).setCellValue(liq.getMetodoPago() != null ? liq.getMetodoPago() : "N/A");
                
                // Aplicar estilo a todas las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    if (i != 3) { // Skip currency cell
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
        }
        
        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Escribir a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReporteArbitros(List<?> arbitros, Map<String, Object> metadata) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Árbitros");
        
        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Encabezado del reporte
        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE ÁRBITROS");
        titleCell.setCellStyle(createTitleStyle(workbook));
        
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("Generado el: " + dateTimeFormat.format(new Date()));
        rowNum++; // Fila vacía
        
        // Headers de columnas
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Nombre", "Email", "Especialidad", "Escalafón", "Tarifa", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        for (Object obj : arbitros) {
            if (obj instanceof Arbitro) {
                Arbitro arb = (Arbitro) obj;
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(arb.getId());
                row.createCell(1).setCellValue(arb.getNombreCompleto());
                row.createCell(2).setCellValue(arb.getEmail());
                row.createCell(3).setCellValue(arb.getEspecialidad() != null ? arb.getEspecialidad() : "N/A");
                row.createCell(4).setCellValue(arb.getEscalafon() != null ? arb.getEscalafon() : "N/A");
                
                Cell tarifaCell = row.createCell(5);
                tarifaCell.setCellValue(arb.getTarifaBase().doubleValue());
                tarifaCell.setCellStyle(currencyStyle);
                
                row.createCell(6).setCellValue(arb.getActivo() ? "Activo" : "Inactivo");
                
                // Aplicar estilo a todas las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    if (i != 5) { // Skip currency cell
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
        }
        
        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Escribir a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReportePartidos(List<?> partidos, Map<String, Object> metadata) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Partidos");
        
        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Encabezado del reporte
        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE PARTIDOS");
        titleCell.setCellStyle(createTitleStyle(workbook));
        
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("Generado el: " + dateTimeFormat.format(new Date()));
        rowNum++; // Fila vacía
        
        // Headers de columnas
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Torneo", "Local", "Visitante", "Fecha/Hora", "Ubicación", "Resultado", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        for (Object obj : partidos) {
            if (obj instanceof Partido) {
                Partido partido = (Partido) obj;
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(partido.getId());
                row.createCell(1).setCellValue(partido.getTorneo() != null ? partido.getTorneo().getNombre() : "N/A");
                row.createCell(2).setCellValue(partido.getEquipoLocal());
                row.createCell(3).setCellValue(partido.getEquipoVisitante());
                row.createCell(4).setCellValue(partido.getFechaPartido() != null ? 
                    dateTimeFormat.format(java.sql.Timestamp.valueOf(partido.getFechaPartido())) : "N/A");
                row.createCell(5).setCellValue(partido.getUbicacion());
                
                String resultado = "N/A";
                if (partido.estaCompletado() && partido.getMarcadorLocal() != null && partido.getMarcadorVisitante() != null) {
                    resultado = partido.getMarcadorLocal() + " - " + partido.getMarcadorVisitante();
                }
                row.createCell(6).setCellValue(resultado);
                row.createCell(7).setCellValue(partido.estaCompletado() ? "Completado" : "Pendiente");
                
                // Aplicar estilo a todas las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
        }
        
        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Escribir a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream generarReporteAsignaciones(List<?> asignaciones, Map<String, Object> metadata) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asignaciones");
        
        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Encabezado del reporte
        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE ASIGNACIONES");
        titleCell.setCellStyle(createTitleStyle(workbook));
        
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("Generado el: " + dateTimeFormat.format(new Date()));
        rowNum++; // Fila vacía
        
        // Headers de columnas
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"ID", "Árbitro", "Partido", "Rol", "Tarifa", "Estado", "Fecha Asignación"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        for (Object obj : asignaciones) {
            if (obj instanceof Asignacion) {
                Asignacion asig = (Asignacion) obj;
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(asig.getId());
                row.createCell(1).setCellValue(asig.getArbitro().getNombreCompleto());
                
                String partidoInfo = "N/A";
                if (asig.getPartido() != null) {
                    partidoInfo = asig.getPartido().getEquipoLocal() + " vs " + asig.getPartido().getEquipoVisitante();
                }
                row.createCell(2).setCellValue(partidoInfo);
                
                row.createCell(3).setCellValue(asig.getRolEspecifico() != null ? asig.getRolEspecifico() : "N/A");
                
                Cell tarifaCell = row.createCell(4);
                tarifaCell.setCellValue(asig.getMontoCalculado().doubleValue());
                tarifaCell.setCellStyle(currencyStyle);
                
                row.createCell(5).setCellValue(asig.getEstado() != null ? asig.getEstado().toString() : "N/A");
                row.createCell(6).setCellValue(dateFormat.format(java.sql.Timestamp.valueOf(asig.getFechaAsignacion())));
                
                // Aplicar estilo a todas las celdas de datos
                for (int i = 0; i < headers.length; i++) {
                    if (i != 4) { // Skip currency cell
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
        }
        
        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Escribir a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos;
    }
    
    // Métodos auxiliares para estilos
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        return style;
    }
    
    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
    
    @Override
    public String getFileExtension() {
        return "xlsx";
    }
    
    @Override
    public String getFormatName() {
        return "Excel";
    }
}
