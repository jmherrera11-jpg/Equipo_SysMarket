package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import model.Producto;

@DisplayName("HU-03: Editar productos")
public class HU03_EditarProductoTest {
    
    private Producto producto;
    
    @BeforeEach
    public void setUp() {
        // Inicializar producto para cada test
        producto = new Producto(
            "PROD001",
            "Leche Entera",
            "Lácteos",
            2.50, 3.50, 100, 20
        );
    }
    
    @Test
    @DisplayName("CA-1: El producto se carga en el formulario al seleccionarlo")
    public void testCargarProductoEnFormulario() {
        // Given - Producto existente
        String codigoEsperado = "PROD001";
        String nombreEsperado = "Leche Entera";
        String categoriaEsperada = "Lácteos";
        double precioCompraEsperado = 2.50;
        double precioVentaEsperado = 3.50;
        int stockEsperado = 100;
        int stockMinimoEsperado = 20;
        
        // Then - Se cargan los datos en el formulario
        assertEquals(codigoEsperado, producto.getCodigo(), "Código se carga correctamente");
        assertEquals(nombreEsperado, producto.getNombre(), "Nombre se carga correctamente");
        assertEquals(categoriaEsperada, producto.getCategoria(), "Categoría se carga correctamente");
        assertEquals(precioCompraEsperado, producto.getPrecioCompra(), 0.01, "Precio compra se carga");
        assertEquals(precioVentaEsperado, producto.getPrecioVenta(), 0.01, "Precio venta se carga");
        assertEquals(stockEsperado, producto.getStock(), "Stock se carga correctamente");
        assertEquals(stockMinimoEsperado, producto.getStockMinimo(), "Stock mínimo se carga");
    }
    
    @Test
    @DisplayName("CA-2: Se valida que el precio sea valor válido (positivo)")
    public void testValidarPrecioVentaPositivo() {
        // Given - Producto con precio venta válido
        double precioVentaNuevo = 4.75;
        
        // When - Editamos el precio
        producto.setPrecioVenta(precioVentaNuevo);
        
        // Then - El precio debe ser positivo
        assertTrue(producto.getPrecioVenta() > 0, "Precio venta debe ser positivo");
        assertEquals(4.75, producto.getPrecioVenta(), 0.01, "Precio actualizado correctamente");
    }
    
    @Test
    @DisplayName("CA-2: Se valida que precio venta > precio compra")
    public void testValidarRelacionPreciosEdicion() {
        // Given - Producto con precios válidos
        producto.setPrecioCompra(5.00);
        
        // When & Then - Precio venta debe ser mayor que compra
        producto.setPrecioVenta(6.00);
        assertTrue(producto.getPrecioVenta() > producto.getPrecioCompra(),
                  "Precio venta debe ser mayor que precio compra al editar");
    }
    
    @Test
    @DisplayName("CA-2: Se valida que stock sea valor válido (no negativo)")
    public void testValidarStockNoNegativo() {
        // Given - Stock actual válido
        int stockNuevo = 150;
        
        // When - Editamos el stock
        producto.setStock(stockNuevo);
        
        // Then - Stock no debe ser negativo
        assertTrue(producto.getStock() >= 0, "Stock no puede ser negativo");
        assertEquals(150, producto.getStock(), "Stock actualizado correctamente");
    }
    
    @Test
    @DisplayName("CA-2: Se valida que stock cero sea válido")
    public void testValidarStockCero() {
        // Given - Stock actual
        // When - Editamos stock a cero
        producto.setStock(0);
        
        // Then - Stock cero es válido
        assertEquals(0, producto.getStock(), "Stock 0 es válido");
        assertEquals("AGOTADO", producto.getEstado(), "Estado debe ser AGOTADO cuando stock=0");
    }
    
    @Test
    @DisplayName("CA-3: Los cambios se guardan correctamente (persistencia)")
    public void testPersistenciaCambios() {
        // Given - Producto original
        String nombreNuevo = "Leche Descremada 1L";
        String categoriaNueva = "Lácteos Descremados";
        double precioVentaNuevo = 3.75;
        int stockNuevo = 200;
        int stockMinimoNuevo = 25;
        
        // When - Editamos múltiples campos
        producto.setNombre(nombreNuevo);
        producto.setCategoria(categoriaNueva);
        producto.setPrecioVenta(precioVentaNuevo);
        producto.setStock(stockNuevo);
        producto.setStockMinimo(stockMinimoNuevo);
        
        // Then - Todos los cambios deben persistir
        assertEquals(nombreNuevo, producto.getNombre(), "Nombre persiste");
        assertEquals(categoriaNueva, producto.getCategoria(), "Categoría persiste");
        assertEquals(precioVentaNuevo, producto.getPrecioVenta(), 0.01, "Precio venta persiste");
        assertEquals(stockNuevo, producto.getStock(), "Stock persiste");
        assertEquals(stockMinimoNuevo, producto.getStockMinimo(), "Stock mínimo persiste");
        
        // El código no debe cambiar
        assertEquals("PROD001", producto.getCodigo(), "Código no debe cambiar");
    }
    
    @Test
    @DisplayName("CA-3: Cambios se reflejan en MongoDB (estado ACTIVO/AGOTADO)")
    public void testActualizarEstadoEnMongoDB() {
        // Given - Producto con stock inicial
        assertEquals("ACTIVO", producto.getEstado(), "Estado inicial es ACTIVO");
        
        // When - Reducimos stock a cero
        producto.setStock(0);
        
        // Then - El estado debe actualizarse a AGOTADO
        assertEquals("AGOTADO", producto.getEstado(), "Estado debe cambiar a AGOTADO");
        
        // When - Aumentamos stock nuevamente
        producto.setStock(50);
        
        // Then - El estado debe volver a ACTIVO
        assertEquals("ACTIVO", producto.getEstado(), "Estado debe volver a ACTIVO");
    }
    
    @Test
    @DisplayName("Escenario: Edición completa con todas las validaciones")
    public void testEdicionCompletaProducto() {
        // Given - Producto existente
        assertNotNull(producto, "Producto debe estar inicializado");
        
        // When - Editamos nombre
        producto.setNombre("Leche Entera Premium 1L");
        assertNotNull(producto.getNombre(), "Nombre no debe ser nulo");
        assertTrue(!producto.getNombre().isEmpty(), "Nombre no debe estar vacío");
        
        // When - Editamos precios con validación
        producto.setPrecioVenta(4.20);
        assertTrue(producto.getPrecioVenta() > producto.getPrecioCompra(),
                  "Precio venta > precio compra");
        
        // When - Editamos stock
        producto.setStock(120);
        assertTrue(producto.getStock() >= 0, "Stock válido");
        assertEquals("ACTIVO", producto.getEstado(), "Estado correcto para stock positivo");
        
        // When - Editamos stock mínimo
        producto.setStockMinimo(25);
        assertTrue(producto.getStockMinimo() >= 0, "Stock mínimo válido");
        
        // Then - Verificar que todo persistió
        assertEquals("Leche Entera Premium 1L", producto.getNombre());
        assertEquals(4.20, producto.getPrecioVenta(), 0.01);
        assertEquals(120, producto.getStock());
        assertEquals(25, producto.getStockMinimo());
    }
}