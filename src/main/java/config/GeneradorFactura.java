package config;

import model.Venta;
import model.Producto;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generador de Facturas - HU-22
 * Genera comprobantes de venta en formato TXT
 */
public class GeneradorFactura {
    
    private static final String LINEA_SEPARADORA = "═══════════════════════════════════════════════════════════";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Genera el contenido de la factura en formato texto
     * @param venta Venta a facturar
     * @return String con el contenido de la factura
     */
    public static String generarContenidoFactura(Venta venta) {
        StringBuilder factura = new StringBuilder();
        
        // Encabezado
        factura.append("\n");
        factura.append(LINEA_SEPARADORA).append("\n");
        factura.append(centrar("MINIMARKET - FACTURA COMPROBANTE", 59)).append("\n");
        factura.append(centrar("RUC: 1791234567001", 59)).append("\n");
        factura.append(LINEA_SEPARADORA).append("\n\n");
        
        // Datos del comprobante
        factura.append("Número Comprobante: ").append(venta.getId()).append("\n");
        factura.append("Fecha y Hora: ").append(sdf.format(venta.getFecha())).append("\n");
        factura.append("Vendedor: ").append(venta.getUsuario()).append("\n");
        
        // Datos del cliente
        if (venta.getCedulaCliente() != null && !venta.getCedulaCliente().isEmpty()) {
            factura.append("C.I. Cliente: ").append(formatearCedula(venta.getCedulaCliente())).append("\n");
        }
        
        factura.append("\n").append(LINEA_SEPARADORA).append("\n");
        factura.append(String.format("%-35s %10s %10s", "DESCRIPCIÓN", "CANTIDAD", "VALOR")).append("\n");
        factura.append(LINEA_SEPARADORA).append("\n");
        
        // Detalles de productos
        double totalVentaN = 0;
        for (Producto producto : venta.getProductos()) {
            double subtotal = producto.getPrecioVenta() * producto.getStock();
            totalVentaN += subtotal;
            
            String nombre = truncarTexto(producto.getNombre(), 35);
            factura.append(String.format("%-35s %10d S/. %8.2f\n", 
                nombre, 
                producto.getStock(), 
                producto.getPrecioVenta()));
            
            if (subtotal > 0) {
                factura.append(String.format("%44s %8.2f\n", "Subtotal:", subtotal));
            }
        }
        
        factura.append(LINEA_SEPARADORA).append("\n");
        
        // Totales
        factura.append(String.format("%45s S/. %8.2f\n", "TOTAL:", venta.getTotal()));
        factura.append("Método de Pago: ").append(venta.getMetodoPago()).append("\n");
        
        // Pie de página
        factura.append("\n").append(LINEA_SEPARADORA).append("\n");
        factura.append(centrar("¡GRACIAS POR SU COMPRA!", 59)).append("\n");
        factura.append(centrar("Visite nuevamente nuestro minimarket", 59)).append("\n");
        factura.append(LINEA_SEPARADORA).append("\n\n");
        
        return factura.toString();
    }
    
    /**
     * Guarda la factura en un archivo TXT
     * @param venta Venta a facturar
     * @param rutaCarpeta Ruta de la carpeta donde guardar la factura
     * @return Ruta completa del archivo guardado
     * @throws IOException Si hay error al guardar el archivo
     */
    public static String guardarFactura(Venta venta, String rutaCarpeta) throws IOException {
        // Crear carpeta si no existe
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        
        // Nombre del archivo basado en el ID de venta
        String nombreArchivo = venta.getId().replace(":", "-") + "_FACTURA.txt";
        File archivo = new File(carpeta, nombreArchivo);
        
        // Escribir contenido en el archivo
        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(generarContenidoFactura(venta));
            writer.flush();
        }
        
        return archivo.getAbsolutePath();
    }
    
    /**
     * Guarda la factura en la carpeta de documentos predeterminada
     * @param venta Venta a facturar
     * @return Ruta completa del archivo guardado
     * @throws IOException Si hay error al guardar
     */
    public static String guardarFacturaEnPorDefecto(Venta venta) throws IOException {
        String rutaDocumentos = System.getProperty("user.home") + File.separator + "Documents" + 
                               File.separator + "Facturas_MiniMarket";
        return guardarFactura(venta, rutaDocumentos);
    }
    
    /**
     * Obtiene la factura como string para vista previa
     * @param venta Venta a facturar
     * @return String con el contenido de la factura
     */
    public static String obtenerVistaPreviaFactura(Venta venta) {
        return generarContenidoFactura(venta);
    }
    
    /**
     * Centra un texto en una línea de ancho especificado
     */
    private static String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        
        int espaciosIzquierda = (ancho - texto.length()) / 2;
        int espaciosDerecha = ancho - texto.length() - espaciosIzquierda;
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espaciosIzquierda; i++) {
            sb.append(" ");
        }
        sb.append(texto);
        for (int i = 0; i < espaciosDerecha; i++) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
    
    /**
     * Trunca texto a un ancho máximo
     */
    private static String truncarTexto(String texto, int ancho) {
        if (texto.length() <= ancho) {
            return texto;
        }
        return texto.substring(0, ancho - 3) + "...";
    }
    
    /**
     * Formatea una cédula para presentación
     */
    private static String formatearCedula(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return cedula;
        }
        
        return cedula.substring(0, 3) + "-" + 
               cedula.substring(3, 6) + "-" + 
               cedula.substring(6, 9) + "-" + 
               cedula.charAt(9);
    }
    
    /**
     * Valida que la ruta sea válida y escribible
     * @param ruta Ruta a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esRutaValida(String ruta) {
        try {
            File directorio = new File(ruta);
            if (!directorio.exists()) {
                return directorio.mkdirs();
            }
            return directorio.isDirectory() && directorio.canWrite();
        } catch (Exception e) {
            return false;
        }
    }
}
