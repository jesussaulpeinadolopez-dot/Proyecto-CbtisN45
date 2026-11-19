package vista;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import controlador.controlReporte;

public class ExportadorReporteUtil {

    private static final Color COLOR_GUINDA = new Color(123, 9, 9);

    public static void gestionarExportacion(JFrame ventanaPadre, String textoBusqueda) {
        DefaultTableModel modelTemporal = new DefaultTableModel();
        modelTemporal.addColumn("ID");
        modelTemporal.addColumn("Nombre");
        modelTemporal.addColumn("Apellido Paterno");
        modelTemporal.addColumn("Apellido Materno");
        modelTemporal.addColumn("Fecha");
        modelTemporal.addColumn("Tipo Reporte");
        modelTemporal.addColumn("Estatus");
        modelTemporal.addColumn("Observaciones");
        modelTemporal.addColumn("Acción");

        controlReporte.cargarTablaReportes(modelTemporal, textoBusqueda);

        if (modelTemporal.getRowCount() == 0) {
            Control.mostrarAlerta(ventanaPadre, "Sin datos", "No hay reportes disponibles para exportar con el filtro actual.");
            return;
        }

        String[] opciones = {"Exportar a PDF", "Exportar a Excel (.xlsx)", "Cancelar"};
        int seleccion = Control.mostrarOpciones(ventanaPadre, "Exportar Reportes", 
                "Seleccione el formato al que desea exportar los reportes registrados:", opciones);

        if (seleccion == 0) {
            prepararYExportar(ventanaPadre, textoBusqueda, "pdf", "Documento PDF (*.pdf)");
        } else if (seleccion == 1) {
            prepararYExportar(ventanaPadre, textoBusqueda, "xlsx", "Archivo Excel (*.xlsx)");
        }
    }

    private static void prepararYExportar(JFrame parent, String filtro, String extension, String descripcionFiltro) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");
        fileChooser.setFileFilter(new FileNameExtensionFilter(descripcionFiltro, extension));
        
        SimpleDateFormat sdfNom = new SimpleDateFormat("yyyyMMdd_HHmmss");
        fileChooser.setSelectedFile(new File("Reporte_Asistencias_" + sdfNom.format(new Date()) + "." + extension));

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivoSeleccionado = fileChooser.getSelectedFile();
        String ruta = archivoSeleccionado.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith("." + extension)) {
            archivoSeleccionado = new File(ruta + "." + extension);
        }

        if (archivoSeleccionado.exists()) {
            boolean reemplazar = Control.mostrarConfirmacion(parent, "Confirmar Sobrescritura", 
                    "El archivo <b>" + archivoSeleccionado.getName() + "</b> ya existe.<br>¿Desea reemplazarlo?");
            if (!reemplazar) {
                return;
            }
        }

        final File archivoFinal = archivoSeleccionado;

        SwingWorker<Boolean, String> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    DefaultTableModel modelData = new DefaultTableModel();
                    modelData.addColumn("ID");
                    modelData.addColumn("Nombre");
                    modelData.addColumn("Apellido Paterno");
                    modelData.addColumn("Apellido Materno");
                    modelData.addColumn("Fecha");
                    modelData.addColumn("Tipo Reporte");
                    modelData.addColumn("Estatus");
                    modelData.addColumn("Observaciones");
                    modelData.addColumn("Acción");

                    controlReporte.cargarTablaReportes(modelData, filtro);

                    if ("pdf".equalsIgnoreCase(extension)) {
                        exportarAPDF(archivoFinal, modelData);
                    } else {
                        exportarAExcel(archivoFinal, modelData);
                    }
                    return true;
                } catch (Throwable ex) {
                    ex.printStackTrace(); // Imprime el error exacto en la consola de Eclipse
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        Control.mostrarExito(parent, "Exportación Exitosa", 
                                "El archivo fue generado correctamente en:<br><b>" + archivoFinal.getAbsolutePath() + "</b>");
                    } else {
                        Control.mostrarError(parent, "Error al Exportar", 
                                "Faltan dependencias secundarias de Apache POI o el archivo está abierto por otro programa.<br>Revisa la consola de Eclipse para más detalles.");
                    }
                } catch (Exception e) {
                    Control.mostrarError(parent, "Error Crítico", "Ocurrió un error inesperado al procesar el archivo.");
                }
            }
        };

        worker.execute();
    }

    private static void exportarAPDF(File destino, DefaultTableModel model) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(destino));
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_GUINDA);
        Paragraph pTitulo = new Paragraph("CBTis N°45 - REPORTE DE INCIDENCIAS Y ASISTENCIAS", fontTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Paragraph pFecha = new Paragraph("Fecha de exportación: " + sdf.format(new Date()), fontSub);
        pFecha.setAlignment(Element.ALIGN_CENTER);
        pFecha.setSpacingAfter(15);
        document.add(pFecha);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 3.5f, 3.5f, 3.5f, 3.0f, 3.0f, 2.5f, 6.0f});
        table.setHeaderRows(1);

        String[] headers = {"ID", "Nombre", "Ap. Paterno", "Ap. Materno", "Fecha", "Tipo", "Estatus", "Observaciones"};
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(COLOR_GUINDA);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);
            table.addCell(cell);
        }

        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        int rows = model.getRowCount();

        for (int i = 0; i < rows; i++) {
            for (int col = 0; col < 8; col++) {
                Object val = model.getValueAt(i, col);
                PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", fontData));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                if (col == 0 || col == 4 || col == 6) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
    }

    private static void exportarAExcel(File destino, DefaultTableModel model) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reportes");

            // Estilos estandarizados con IndexedColors para evitar fallos de compatibilidad
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(dataStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("CBTis N°45 - REPORTE DE INCIDENCIAS");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Fecha de exportación: " + sdf.format(new Date()));

            // Encabezados
            String[] headers = {"ID", "Nombre", "Apellido Paterno", "Apellido Materno", "Fecha", "Tipo Reporte", "Estatus", "Observaciones"};
            Row headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(24);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Row row = sheet.createRow(i + 4);
                for (int col = 0; col < 8; col++) {
                    Cell cell = row.createCell(col);
                    Object val = model.getValueAt(i, col);
                    String texto = val != null ? val.toString() : "";

                    if (col == 0) {
                        try {
                            cell.setCellValue(Integer.parseInt(texto));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(texto);
                        }
                        cell.setCellStyle(centerStyle);
                    } else if (col == 4 || col == 6) {
                        cell.setCellValue(texto);
                        cell.setCellStyle(centerStyle);
                    } else {
                        cell.setCellValue(texto);
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Dimensionado y Filtros
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1024, 3000));
            }

            sheet.createFreezePane(0, 4);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(3, Math.max(3, rowCount + 3), 0, 7));

            try (FileOutputStream fos = new FileOutputStream(destino)) {
                workbook.write(fos);
            }
        }
    }
}