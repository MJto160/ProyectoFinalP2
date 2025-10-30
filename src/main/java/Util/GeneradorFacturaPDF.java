package Util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import Modelos.Partido;
import Servicios.ServicioPartido;
import java.awt.Desktop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorFacturaPDF {
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    
public static boolean generarFacturaVenta(JTable tablaVentas, double totalVenta, String partidoInfo) {
    try {
        // Crear documento
        Document document = new Document();
        
        // Crear directorio si no existe
        File directorio = new File("facturas");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        // Generar nombre único para el archivo
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "facturas/Factura_" + timestamp + ".pdf";
        
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        
        // Agregar contenido al PDF
        agregarCabecera(document);
        agregarInformacionVenta(document, partidoInfo);
        agregarTablaItems(document, tablaVentas);
        agregarTotal(document, totalVenta);
        agregarPie(document);
        
        document.close();
        
        // ✅ ABRIR AUTOMÁTICAMENTE EL PDF GENERADO
        try {
            File pdfFile = new File(fileName);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "El archivo se generó pero no se pudo abrir automáticamente.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, 
                "Error al intentar abrir la factura: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(null, 
            "✅ Factura generada exitosamente!\n" +
            "Archivo: " + fileName + "\n" +
            "Total: Q" + String.format("%.2f", totalVenta),
            "Factura Generada", 
            JOptionPane.INFORMATION_MESSAGE);
        
        return true;
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null,
            "❌ Error al generar factura: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return false;
    }
}

    
    private static void agregarCabecera(Document document) throws DocumentException {
        // Título principal
        Paragraph titulo = new Paragraph("VENTA DE BOLETOS - FACTURA", TITLE_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);
        
        // Línea separadora
        Paragraph separador = new Paragraph("________________________________________________");
        separador.setAlignment(Element.ALIGN_CENTER);
        separador.setSpacingAfter(15);
        document.add(separador);
    }
    
    private static void agregarInformacionVenta(Document document, String partidoInfo) throws DocumentException {
        // Información de la venta
        Paragraph infoVenta = new Paragraph("INFORMACIÓN DE LA VENTA", SUBTITLE_FONT);
        infoVenta.setSpacingAfter(10);
        document.add(infoVenta);
        
        // Fecha de generación
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        
        // Crear tabla para información
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(15);
        
        // Agregar celdas
        agregarCelda(infoTable, "Fecha de Generación:", BOLD_FONT);
        agregarCelda(infoTable, fechaActual, NORMAL_FONT);
        
        agregarCelda(infoTable, "No. Factura:", BOLD_FONT);
        agregarCelda(infoTable, "FAC-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), NORMAL_FONT);
        
        document.add(infoTable);
    }
    
    private static void agregarTablaItems(Document document, JTable tablaVentas) throws DocumentException {
        Paragraph itemsTitle = new Paragraph("DETALLE DE BOLETOS VENDIDOS", SUBTITLE_FONT);
        itemsTitle.setSpacingAfter(10);
        document.add(itemsTitle);
        
        // Crear tabla para items
        PdfPTable table = new PdfPTable(5); // 5 columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);
        
        // Encabezados de la tabla
        String[] headers = {"PARTIDO", "LOCALIDAD", "PRECIO UNITARIO", "SUBTOTAL", "PARTIDO"};
        
        // Agregar encabezados
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, BOLD_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Agregar datos de la tabla de ventas
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                String cellValue = (value != null) ? value.toString() : "";
                PdfPCell cell = new PdfPCell(new Phrase(cellValue, NORMAL_FONT));
                cell.setPadding(5);
                
                // Alinear columnas numéricas a la derecha
                if (j == 1 || j == 2 || j == 3) { // Cantidad, Precio, Subtotal
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                
                table.addCell(cell);
            }
        }
        
        document.add(table);
    }
    
    private static void agregarTotal(Document document, double totalVenta) throws DocumentException {
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setSpacingBefore(10);
        
        agregarCelda(totalTable, "TOTAL A PAGAR:", BOLD_FONT);
        agregarCelda(totalTable, "Q " + String.format("%.2f", totalVenta), BOLD_FONT);
        
        document.add(totalTable);
    }
    
    private static void agregarPie(Document document) throws DocumentException {
        Paragraph pie = new Paragraph("\n\n¡Gracias por su compra!\nVuelva pronto.", NORMAL_FONT);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(20);
        document.add(pie);
        
        // Agregar espacio para imagen (opcional)
        Paragraph espacioImagen = new Paragraph("\n\n\n");
        document.add(espacioImagen);
    }
    
    private static void agregarCelda(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    // Método para agregar imagen (opcional)
    public static void agregarImagen(Document document, String imagePath) {
        try {
            Image imagen = Image.getInstance(imagePath);
            imagen.scaleToFit(100, 100);
            imagen.setAlignment(Element.ALIGN_CENTER);
            document.add(imagen);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
    }
}