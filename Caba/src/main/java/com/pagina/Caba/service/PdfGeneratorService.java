package com.pagina.Caba.service;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Partido;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar PDFs de liquidaciones/facturas para árbitros.
 * Genera documentos con formato profesional incluyendo código de barras.
 */
@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Colores corporativos
    private static final DeviceRgb COLOR_PRIMARY = new DeviceRgb(0, 123, 255);
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb COLOR_SUCCESS = new DeviceRgb(40, 167, 69);
    
    /**
     * Genera un PDF de liquidación/factura para un árbitro.
     * 
     * @param liquidacion La liquidación a imprimir
     * @return byte[] con el contenido del PDF
     * @throws Exception Si hay error generando el PDF
     */
    public byte[] generarPdfLiquidacion(Liquidacion liquidacion) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // Obtener datos relacionados
        Arbitro arbitro = liquidacion.getAsignacion().getArbitro();
        Partido partido = liquidacion.getAsignacion().getPartido();
        
        // ============================================
        // ENCABEZADO CON LOGO Y TÍTULO
        // ============================================
        agregarEncabezado(document, liquidacion);
        
        // ============================================
        // INFORMACIÓN DEL ÁRBITRO
        // ============================================
        agregarInformacionArbitro(document, arbitro);
        
        // ============================================
        // INFORMACIÓN DEL PARTIDO
        // ============================================
        agregarInformacionPartido(document, partido, liquidacion);
        
        // ============================================
        // DETALLE DE LIQUIDACIÓN
        // ============================================
        agregarDetalleLiquidacion(document, liquidacion);
        
        // ============================================
        // CÓDIGO DE BARRAS
        // ============================================
        agregarCodigoBarras(document, pdfDoc, liquidacion);
        
        // ============================================
        // INFORMACIÓN BANCARIA
        // ============================================
        agregarInformacionBancaria(document, liquidacion);
        
        // ============================================
        // PIE DE PÁGINA
        // ============================================
        agregarPieDePagina(document, liquidacion);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Agrega el encabezado del documento con logo y título
     */
    private void agregarEncabezado(Document document, Liquidacion liquidacion) {
        // Título principal
        Paragraph titulo = new Paragraph("LIQUIDACIÓN DE SERVICIOS ARBITRALES")
            .setFontSize(20)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(titulo);
        
        // Subtítulo con número de liquidación y fecha
        String numeroLiquidacion = String.format("Liquidación #%06d", liquidacion.getId());
        Paragraph subtitulo = new Paragraph(numeroLiquidacion)
            .setFontSize(14)
            .setFontColor(COLOR_SECONDARY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);
        document.add(subtitulo);
        
        String fechaEmision = "Fecha de emisión: " + liquidacion.getFechaCreacion().format(DATE_FORMATTER);
        Paragraph fecha = new Paragraph(fechaEmision)
            .setFontSize(10)
            .setFontColor(COLOR_SECONDARY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(fecha);
        
        // Línea separadora
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega la información del árbitro
     */
    private void agregarInformacionArbitro(Document document, Arbitro arbitro) {
        Paragraph seccionTitulo = new Paragraph("DATOS DEL ÁRBITRO")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginBottom(10);
        document.add(seccionTitulo);
        
        Table tableArbitro = new Table(2);
        tableArbitro.setWidth(UnitValue.createPercentValue(100));
        
        // Estilo para encabezados
        Cell headerCell1 = new Cell().add(new Paragraph("Campo").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        Cell headerCell2 = new Cell().add(new Paragraph("Información").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        tableArbitro.addHeaderCell(headerCell1);
        tableArbitro.addHeaderCell(headerCell2);
        
        // Datos del árbitro
        agregarFilaTabla(tableArbitro, "Nombre completo", arbitro.getNombre() + " " + arbitro.getApellido());
        agregarFilaTabla(tableArbitro, "Email", arbitro.getEmail());
        agregarFilaTabla(tableArbitro, "Teléfono", arbitro.getTelefono() != null ? arbitro.getTelefono() : "N/A");
        agregarFilaTabla(tableArbitro, "Especialidad", arbitro.getEspecialidad());
        agregarFilaTabla(tableArbitro, "Escalafón", arbitro.getEscalafon());
        
        document.add(tableArbitro);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega la información del partido
     */
    private void agregarInformacionPartido(Document document, Partido partido, Liquidacion liquidacion) {
        Paragraph seccionTitulo = new Paragraph("DATOS DEL PARTIDO")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginBottom(10);
        document.add(seccionTitulo);
        
        Table tablePartido = new Table(2);
        tablePartido.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        Cell headerCell1 = new Cell().add(new Paragraph("Campo").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        Cell headerCell2 = new Cell().add(new Paragraph("Información").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        tablePartido.addHeaderCell(headerCell1);
        tablePartido.addHeaderCell(headerCell2);
        
        // Datos del partido
        agregarFilaTabla(tablePartido, "Equipos", partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante());
        agregarFilaTabla(tablePartido, "Fecha del partido", partido.getFechaPartido().format(DATE_FORMATTER));
        agregarFilaTabla(tablePartido, "Ubicación", partido.getUbicacion());
        agregarFilaTabla(tablePartido, "Tipo de partido", partido.getTipoPartido());
        agregarFilaTabla(tablePartido, "Torneo", partido.getTorneo() != null ? partido.getTorneo().getNombre() : "N/A");
        agregarFilaTabla(tablePartido, "Rol asignado", liquidacion.getAsignacion().getRolEspecifico());
        
        if (partido.getMarcadorLocal() != null && partido.getMarcadorVisitante() != null) {
            String resultado = partido.getMarcadorLocal() + " - " + partido.getMarcadorVisitante();
            agregarFilaTabla(tablePartido, "Resultado", resultado);
        }
        
        document.add(tablePartido);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega el detalle de liquidación con monto y estado
     */
    private void agregarDetalleLiquidacion(Document document, Liquidacion liquidacion) {
        Paragraph seccionTitulo = new Paragraph("DETALLE DE LIQUIDACIÓN")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginBottom(10);
        document.add(seccionTitulo);
        
        Table tableLiquidacion = new Table(new float[]{3, 1});
        tableLiquidacion.setWidth(UnitValue.createPercentValue(100));
        
        // Concepto y monto
        Cell conceptoCell = new Cell().add(new Paragraph("Honorarios por arbitraje").setBold())
            .setPadding(10)
            .setBackgroundColor(new DeviceRgb(250, 250, 250));
        Cell montoCell = new Cell().add(new Paragraph("$" + liquidacion.getMonto().toString()).setBold())
            .setPadding(10)
            .setTextAlignment(TextAlignment.RIGHT)
            .setBackgroundColor(new DeviceRgb(250, 250, 250));
        tableLiquidacion.addCell(conceptoCell);
        tableLiquidacion.addCell(montoCell);
        
        // Total
        Cell totalLabelCell = new Cell().add(new Paragraph("TOTAL A PAGAR").setBold())
            .setPadding(12)
            .setBackgroundColor(COLOR_SUCCESS)
            .setFontColor(ColorConstants.WHITE)
            .setFontSize(14);
        Cell totalMontoCell = new Cell().add(new Paragraph("$" + liquidacion.getMonto().toString()).setBold())
            .setPadding(12)
            .setTextAlignment(TextAlignment.RIGHT)
            .setBackgroundColor(COLOR_SUCCESS)
            .setFontColor(ColorConstants.WHITE)
            .setFontSize(14);
        tableLiquidacion.addCell(totalLabelCell);
        tableLiquidacion.addCell(totalMontoCell);
        
        document.add(tableLiquidacion);
        
        // Estado de la liquidación
        String estadoTexto = "Estado: " + liquidacion.getEstado().toString();
        DeviceRgb estadoColor = liquidacion.estaPagada() ? COLOR_SUCCESS : COLOR_SECONDARY;
        Paragraph estado = new Paragraph(estadoTexto)
            .setFontSize(12)
            .setBold()
            .setFontColor(estadoColor)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(10);
        document.add(estado);
        
        if (liquidacion.estaPagada() && liquidacion.getFechaPago() != null) {
            Paragraph fechaPago = new Paragraph("Pagado el: " + liquidacion.getFechaPago().format(DATE_FORMATTER))
                .setFontSize(10)
                .setFontColor(COLOR_SECONDARY)
                .setTextAlignment(TextAlignment.RIGHT);
            document.add(fechaPago);
        }
        
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega el código de barras al documento
     */
    private void agregarCodigoBarras(Document document, PdfDocument pdfDoc, Liquidacion liquidacion) {
        Paragraph seccionTitulo = new Paragraph("CÓDIGO DE REFERENCIA")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginBottom(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(seccionTitulo);
        
        // Generar código de barras con el ID de liquidación
        String codigoReferencia = String.format("LIQ%010d", liquidacion.getId());
        
        Barcode128 barcode = new Barcode128(pdfDoc);
        barcode.setCode(codigoReferencia);
        barcode.setCodeType(Barcode128.CODE128);
        
        // Crear imagen del código de barras
        PdfFormXObject barcodeObject = barcode.createFormXObject(pdfDoc);
        Image barcodeImage = new Image(barcodeObject);
        barcodeImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        barcodeImage.setMarginBottom(5);
        
        document.add(barcodeImage);
        
        // Texto del código
        Paragraph codigoTexto = new Paragraph(codigoReferencia)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(15);
        document.add(codigoTexto);
        
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega información bancaria para el pago
     */
    private void agregarInformacionBancaria(Document document, Liquidacion liquidacion) {
        Paragraph seccionTitulo = new Paragraph("INFORMACIÓN PARA PAGO")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginBottom(10);
        document.add(seccionTitulo);
        
        Table tableBanco = new Table(2);
        tableBanco.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        Cell headerCell1 = new Cell().add(new Paragraph("Concepto").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        Cell headerCell2 = new Cell().add(new Paragraph("Detalle").setBold())
            .setBackgroundColor(new DeviceRgb(240, 240, 240))
            .setPadding(8);
        tableBanco.addHeaderCell(headerCell1);
        tableBanco.addHeaderCell(headerCell2);
        
        // Información bancaria
        agregarFilaTabla(tableBanco, "Banco", "Banco de la República");
        agregarFilaTabla(tableBanco, "Beneficiario", "CABA - Comité Árbitros de Baloncesto");
        agregarFilaTabla(tableBanco, "Tipo de cuenta", "Cuenta Corriente");
        agregarFilaTabla(tableBanco, "Número de cuenta", "1234-5678-9012-3456");
        agregarFilaTabla(tableBanco, "NIT", "900.123.456-7");
        
        String referencia = String.format("LIQ%010d", liquidacion.getId());
        agregarFilaTabla(tableBanco, "Referencia de pago", referencia);
        
        document.add(tableBanco);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega el pie de página con información legal
     */
    private void agregarPieDePagina(Document document, Liquidacion liquidacion) {
        // Observaciones si las hay
        if (liquidacion.getObservaciones() != null && !liquidacion.getObservaciones().isEmpty()) {
            Paragraph obsTitle = new Paragraph("OBSERVACIONES:")
                .setFontSize(10)
                .setBold()
                .setMarginTop(10);
            document.add(obsTitle);
            
            Paragraph obs = new Paragraph(liquidacion.getObservaciones())
                .setFontSize(9)
                .setFontColor(COLOR_SECONDARY);
            document.add(obs);
        }
        
        // Nota legal
        document.add(new Paragraph("\n"));
        Paragraph notaLegal = new Paragraph(
            "NOTA IMPORTANTE: Este documento es una liquidación oficial de servicios arbitrales. " +
            "Conserve este documento para sus registros contables. En caso de dudas o consultas, " +
            "comuníquese con el Departamento de Administración de CABA."
        )
            .setFontSize(8)
            .setFontColor(COLOR_SECONDARY)
            .setItalic()
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginTop(20)
            .setPaddingTop(10);
        document.add(notaLegal);
        
        // Información de contacto
        Paragraph contacto = new Paragraph(
            "CABA - Comité de Árbitros de Baloncesto | Tel: (601) 123-4567 | Email: admin@caba.com | www.caba.com"
        )
            .setFontSize(8)
            .setFontColor(COLOR_SECONDARY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10);
        document.add(contacto);
    }
    
    /**
     * Método auxiliar para agregar una fila a una tabla
     */
    private void agregarFilaTabla(Table table, String campo, String valor) {
        Cell cellCampo = new Cell().add(new Paragraph(campo))
            .setPadding(8)
            .setBackgroundColor(new DeviceRgb(250, 250, 250));
        Cell cellValor = new Cell().add(new Paragraph(valor))
            .setPadding(8);
        table.addCell(cellCampo);
        table.addCell(cellValor);
    }
}
