package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import model.Venta;
import model.Producto;
import config.GeneradorFactura;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DisplayName("HU-22: Facturación - Emitir comprobante de venta")
public class HU22_FacturacionTest {
    
    private Venta venta;
    private List<Producto> productos;
    
    @BeforeEach
    public void setUp() {
        // Crear productos de prueba
        productos = new ArrayList<>();
        
        Producto p1 = new Producto("PROD001", "Leche Entera 1L", "Lácteos", 2.50, 3.50, 2, 10);
        Producto p2 = new Producto("PROD002", "Pan de Molde", "Panadería", 1.00, 1.80, 3, 5);
        
        productos.add(p1);
        productos.add(p2);
        
        // Crear venta de prueba
        venta = new Venta("cajero001", "1715045179", productos, 10.40, "EFECTIVO");
    }
    
    @Test
    @DisplayName("CA-1: El botón de emitir factura está disponible después de la venta")
    public void testBototonEmitirFactortaDisponible() {
        // Given - Venta realizada
        assertNotNull(venta, "Debe existir una venta");
        assertFalse(venta.getProductos().isEmpty(), "Venta debe tener productos");
        
        // Then - La factura puede ser generada
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        assertNotNull(contenido, "Debe generarse contenido de factura");
        assertTrue(contenido.length() > 0, "Factura debe tener contenido");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene número de comprobante")
    public void testFacturaContieneNumeroComprobante() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains(venta.getId()), 
                  "Factura debe contener el número de comprobante");
        assertTrue(contenido.contains("Número Comprobante:"),
                  "Factura debe mostrar el número de comprobante");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene fecha y hora de la venta")
    public void testFacturaContieneFecharHora() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("Fecha y Hora:"),
                  "Factura debe contener fecha y hora");
        assertTrue(contenido.contains(venta.getId()),
                  "Factura debe tener la información de la venta");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene datos del vendedor")
    public void testFacturaContieneVendedor() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("Vendedor:"),
                  "Factura debe mostrar vendedor");
        assertTrue(contenido.contains(venta.getUsuario()),
                  "Factura debe contener nombre del vendedor");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene cédula del cliente")
    public void testFacturaContieneClienteCedula() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("C.I. Cliente:"),
                  "Factura debe mostrar C.I. del cliente");
        assertTrue(contenido.contains("1715045179"),
                  "Factura debe contener cédula del cliente");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene detalle de productos")
    public void testFacturaContieneDetalleProductos() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("DESCRIPCIÓN"),
                  "Factura debe tener sección de descripción");
        assertTrue(contenido.contains("Leche Entera 1L"),
                  "Factura debe contener producto 1");
        assertTrue(contenido.contains("Pan de Molde"),
                  "Factura debe contener producto 2");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene cantidades y precios")
    public void testFacturaContieneQuantidadYPrecios() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("CANTIDAD"),
                  "Factura debe mostrar columna cantidad");
        assertTrue(contenido.contains("VALOR"),
                  "Factura debe mostrar columna valor");
        
        // Verificar que se incluyan las cantidades
        assertTrue(contenido.contains("3") || contenido.contains("2"),
                  "Factura debe contener cantidades de productos");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene total de la venta")
    public void testFacturaContieneTotal() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("TOTAL:"),
                  "Factura debe mostrar total");
        assertTrue(contenido.contains(String.format("%.2f", venta.getTotal())) ||
                  contenido.contains("10.40"),
                  "Factura debe contener el total de la venta");
    }
    
    @Test
    @DisplayName("CA-2: La factura contiene método de pago")
    public void testFacturaContieneMetodoPago() {
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        
        assertTrue(contenido.contains("Método de Pago:"),
                  "Factura debe mostrar método de pago");
        assertTrue(contenido.contains(venta.getMetodoPago()),
                  "Factura debe contener el método de pago");
    }
    
    @Test
    @DisplayName("CA-3: Se puede guardar la factura en un archivo")
    public void testGuardarFacturaEnArchivo(@TempDir File tempDir) {
        try {
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, tempDir.getAbsolutePath());
            
            assertNotNull(rutaArchivo, "Debe retornar ruta del archivo");
            
            File archivoGuardado = new File(rutaArchivo);
            assertTrue(archivoGuardado.exists(), "El archivo debe existir después de guardar");
            assertTrue(archivoGuardado.length() > 0, "El archivo debe tener contenido");
            
        } catch (Exception e) {
            fail("No debe lanzar excepción al guardar factura: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("CA-3: El archivo de factura tiene formato txt")
    public void testArchivoFacturaTieneFomatoTxt(@TempDir File tempDir) {
        try {
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, tempDir.getAbsolutePath());
            
            assertTrue(rutaArchivo.endsWith(".txt"),
                      "Archivo de factura debe tener extensión .txt");
            
        } catch (Exception e) {
            fail("Error al guardar factura: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("CA-3: El contenido del archivo guardado es correcto")
    public void testContenidoArchivoGuardadoEsCorrecto(@TempDir File tempDir) {
        try {
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, tempDir.getAbsolutePath());
            File archivo = new File(rutaArchivo);
            
            // Leer contenido del archivo
            java.nio.file.Path path = archivo.toPath();
            String contenido = new String(java.nio.file.Files.readAllBytes(path));
            
            // Verificar que contiene los elementos principales
            assertTrue(contenido.contains("MINIMARKET"),
                      "Archivo debe contener nombre del minimarket");
            assertTrue(contenido.contains("FACTURA COMPROBANTE"),
                      "Archivo debe contener título de factura");
            assertTrue(contenido.contains(venta.getId()),
                      "Archivo debe contener número de venta");
            
        } catch (Exception e) {
            fail("Error al verificar contenido: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("CA-3: Se puede crear carpeta automáticamente si no existe")
    public void testCrearCarpetaAutomaticamente(@TempDir File tempDir) {
        try {
            String rutaPrueba = tempDir.getAbsolutePath() + File.separator + 
                               "nueva_carpeta" + File.separator + "subfolder";
            
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, rutaPrueba);
            
            File archivo = new File(rutaArchivo);
            assertTrue(archivo.exists(), "Debe crear directorios automáticamente");
            
        } catch (Exception e) {
            fail("No debe fallar al crear carpetas: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("CA-3: Nombre del archivo incluye ID de venta")
    public void testNombreArchivoIncluveIdVenta(@TempDir File tempDir) {
        try {
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, tempDir.getAbsolutePath());
            
            File archivo = new File(rutaArchivo);
            String nombreArchivo = archivo.getName();
            
            assertTrue(nombreArchivo.contains(venta.getId()),
                      "Nombre del archivo debe incluir ID de venta");
            assertTrue(nombreArchivo.contains("FACTURA"),
                      "Nombre debe incluir palabra FACTURA");
            
        } catch (Exception e) {
            fail("Error al validar nombre: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("HU-22: Flujo completo de emisión de factura")
    public void testFlujoCompletoEmisionFactura(@TempDir File tempDir) {
        // PASO 1: Generar contenido de factura
        String contenido = GeneradorFactura.generarContenidoFactura(venta);
        assertNotNull(contenido);
        
        // PASO 2: Validar que contiene información principal
        assertTrue(contenido.contains("FACTURA COMPROBANTE"));
        assertTrue(contenido.contains(venta.getId()));
        assertTrue(contenido.contains(venta.getUsuario()));
        
        // PASO 3: Guardar factura en archivo
        try {
            String rutaArchivo = GeneradorFactura.guardarFactura(venta, tempDir.getAbsolutePath());
            File archivo = new File(rutaArchivo);
            
            // PASO 4: Validar que se guardó correctamente
            assertTrue(archivo.exists());
            assertTrue(archivo.length() > 0);
            
        } catch (Exception e) {
            fail("Error en flujo de emisión: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("HU-22: Validar ruta para guardar factura")
    public void testValidarRutaParaGuardar(@TempDir File tempDir) {
        assertTrue(GeneradorFactura.esRutaValida(tempDir.getAbsolutePath()),
                  "Ruta válida debe validarse correctamente");
        
        assertFalse(GeneradorFactura.esRutaValida("/ruta/inexistente/imposible/crear"),
                   "Ruta inválida debe ser rechazada");
    }
}
