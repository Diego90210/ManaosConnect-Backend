package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.EmpleadoReporteDTO;
import com.diego.gestorcasino.dto.ReporteResponseDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    public byte[] generarReportePdf(ReporteResponseDTO reporte) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Formato de moneda
            NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // TÍTULO DEL REPORTE
            Paragraph titulo = new Paragraph("REPORTE DE CONSUMOS")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // INFORMACIÓN DE LA EMPRESA
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("INFORMACIÓN DE LA EMPRESA").setBold().setFontSize(14));
            document.add(new Paragraph("NIT: " + reporte.getEmpresaNit()));
            document.add(new Paragraph("Empresa: " + reporte.getEmpresaNombre()));
            document.add(new Paragraph("Período: " + reporte.getFechaInicio().format(formatoFecha) + 
                                     " - " + reporte.getFechaFin().format(formatoFecha)));
            document.add(new Paragraph("Total General: " + formatoMoneda.format(reporte.getTotalConsumos())).setBold());

            // TABLA DE EMPLEADOS
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("DETALLE POR EMPLEADO").setBold().setFontSize(14));
            
            if (reporte.getEmpleados() != null && !reporte.getEmpleados().isEmpty()) {
                // Crear tabla con 4 columnas
                Table tabla = new Table(UnitValue.createPercentArray(new float[]{25, 35, 20, 20}));
                tabla.setWidth(UnitValue.createPercentValue(100));

                // Encabezados
                tabla.addHeaderCell(new Cell().add(new Paragraph("Cédula").setBold()));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Nombre").setBold()));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Consumos").setBold()));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

                // Datos de empleados
                for (EmpleadoReporteDTO empleado : reporte.getEmpleados()) {
                    tabla.addCell(new Cell().add(new Paragraph(empleado.getCedula())));
                    tabla.addCell(new Cell().add(new Paragraph(empleado.getNombre())));
                    tabla.addCell(new Cell().add(new Paragraph(String.valueOf(empleado.getCantidadConsumos()))));
                    tabla.addCell(new Cell().add(new Paragraph(formatoMoneda.format(empleado.getTotalConsumido()))));
                }

                document.add(tabla);
            } else {
                document.add(new Paragraph("No hay empleados con consumos en este período."));
            }

            // RESUMEN FINAL
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("RESUMEN").setBold().setFontSize(14));
            document.add(new Paragraph("Total de empleados: " + 
                                     (reporte.getEmpleados() != null ? reporte.getEmpleados().size() : 0)));
            document.add(new Paragraph("Total consumos: " + 
                                     (reporte.getEmpleados() != null ? 
                                      reporte.getEmpleados().stream().mapToInt(EmpleadoReporteDTO::getCantidadConsumos).sum() : 0)));
            document.add(new Paragraph("Monto total: " + formatoMoneda.format(reporte.getTotalConsumos())).setBold());

            // PIE DE PÁGINA
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Reporte generado el: " + 
                                     java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                                     .setFontSize(10)
                                     .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}