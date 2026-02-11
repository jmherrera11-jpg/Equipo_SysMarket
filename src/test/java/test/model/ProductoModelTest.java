package test.model;

import model.Producto;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Producto (Modelo)")
public class ProductoModelTest {
    
    @Test
    @DisplayName("Crear producto con constructor completo")
    void testCrearProductoCompleto() {
        // Arrange & Act
        Producto producto = new Producto(
            "PROD001",
            "Leche Entera",
            "Lácteos",
            2.50,
            3.50,
            100,
            10
        );
        
        // Assert
        assertEquals("PROD001", producto.getCodigo());
        assertEquals("Leche Entera", producto.getNombre());
        assertEquals("Lácteos", producto.getCategoria());
        assertEquals(2.50, producto.getPrecioCompra(), 0.001);
        assertEquals(3.50, producto.getPrecioVenta(), 0.001);
        assertEquals(100, producto.getStock());
        assertEquals(10, producto.getStockMinimo());
        assertEquals("ACTIVO", producto.getEstado());
        assertNotNull(producto.getFechaCreacion());
    }
    
    @Test
    @DisplayName("Cambio de estado según stock")
    void testCambioEstadoStock() {
        Producto producto = new Producto("PROD001", "Test", "Categoria", 1.0, 2.0, 10, 5);
        
        // Stock positivo - ACTIVO
        producto.setStock(5);
        assertEquals("ACTIVO", producto.getEstado());
        
        // Stock cero - AGOTADO
        producto.setStock(0);
        assertEquals("AGOTADO", producto.getEstado());
        
        // Stock negativo - mantiene estado (aunque en práctica no debería haber stock negativo)
        producto.setStock(-5);
        assertEquals("AGOTADO", producto.getEstado()); // Sigue AGOTADO
    }
    
    @Test
    @DisplayName("Convertir a Document MongoDB")
    void testToDocument() {
        // Arrange
        Producto producto = new Producto(
            "TEST001",
            "Producto Test",
            "Test Category",
            1.00,
            2.00,
            50,
            5
        );
        
        // Act
        Document doc = producto.toDocument();
        
        // Assert
        assertNotNull(doc, "El documento no debe ser null");
        assertEquals("TEST001", doc.getString("codigo"));
        assertEquals("Producto Test", doc.getString("nombre"));
        assertEquals("Test Category", doc.getString("categoria"));
        assertEquals(1.00, doc.getDouble("precioCompra"), 0.001);
        assertEquals(2.00, doc.getDouble("precioVenta"), 0.001);
        assertEquals(50, doc.getInteger("stock"));
        assertEquals(5, doc.getInteger("stockMinimo"));
        assertEquals("ACTIVO", doc.getString("estado"));
        
        // Verificar formato de fecha
        String fechaStr = doc.getString("fechaCreacion");
        assertNotNull(fechaStr, "La fecha de creación no debe ser null");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        assertDoesNotThrow(() -> LocalDateTime.parse(fechaStr, formatter));
    }
    
    @Test
    @DisplayName("Crear desde Document MongoDB")
    void testFromDocument() {
        // Arrange
        String fechaStr = "2024-01-15 10:30:00";
        Document doc = new Document("codigo", "PROD002")
                .append("nombre", "Queso")
                .append("categoria", "Lácteos")
                .append("precioCompra", 5.00)
                .append("precioVenta", 7.50)
                .append("stock", 25)
                .append("stockMinimo", 5)
                .append("estado", "ACTIVO")
                .append("fechaCreacion", fechaStr);
        
        // Act
        Producto producto = Producto.fromDocument(doc);
        
        // Assert
        assertNotNull(producto, "El producto no debe ser null");
        assertEquals("PROD002", producto.getCodigo());
        assertEquals("Queso", producto.getNombre());
        assertEquals("Lácteos", producto.getCategoria());
        assertEquals(5.00, producto.getPrecioCompra(), 0.001);
        assertEquals(7.50, producto.getPrecioVenta(), 0.001);
        assertEquals(25, producto.getStock());
        assertEquals(5, producto.getStockMinimo());
        assertEquals("ACTIVO", producto.getEstado());
        
        // Verificar fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime fechaEsperada = LocalDateTime.parse(fechaStr, formatter);
        assertEquals(fechaEsperada, producto.getFechaCreacion());
    }
    
    @Test
    @DisplayName("Crear desde Document con tipos de datos diferentes")
    void testFromDocumentTiposDiferentes() {
        // Test con stock como Double
        Document doc1 = new Document("codigo", "PROD003")
                .append("nombre", "Producto")
                .append("categoria", "Cat")
                .append("precioCompra", 1.0)
                .append("precioVenta", 2.0)
                .append("stock", 100.0) // Double
                .append("stockMinimo", 10.0) // Double
                .append("estado", "ACTIVO");
        
        Producto p1 = Producto.fromDocument(doc1);
        assertNotNull(p1, "El producto p1 no debe ser null");
        assertEquals(100, p1.getStock());
        assertEquals(10, p1.getStockMinimo());
        
        // Test con stock como Integer
        Document doc2 = new Document("codigo", "PROD004")
                .append("nombre", "Producto")
                .append("categoria", "Cat")
                .append("precioCompra", 1.0)
                .append("precioVenta", 2.0)
                .append("stock", 50) // Integer
                .append("stockMinimo", 5) // Integer
                .append("estado", "ACTIVO");
        
        Producto p2 = Producto.fromDocument(doc2);
        assertNotNull(p2, "El producto p2 no debe ser null");
        assertEquals(50, p2.getStock());
        assertEquals(5, p2.getStockMinimo());
    }
    
    @Test
    @DisplayName("Getters y Setters funcionan correctamente")
    void testGettersSetters() {
        Producto producto = new Producto();
        
        producto.setCodigo("TEST001");
        producto.setNombre("Test Product");
        producto.setCategoria("Test Category");
        producto.setPrecioCompra(1.50);
        producto.setPrecioVenta(2.50);
        producto.setStock(100);
        producto.setStockMinimo(10);
        producto.setEstado("ACTIVO");
        
        LocalDateTime fecha = LocalDateTime.now();
        producto.setFechaCreacion(fecha);
        
        assertEquals("TEST001", producto.getCodigo());
        assertEquals("Test Product", producto.getNombre());
        assertEquals("Test Category", producto.getCategoria());
        assertEquals(1.50, producto.getPrecioCompra(), 0.001);
        assertEquals(2.50, producto.getPrecioVenta(), 0.001);
        assertEquals(100, producto.getStock());
        assertEquals(10, producto.getStockMinimo());
        assertEquals("ACTIVO", producto.getEstado());
        assertEquals(fecha, producto.getFechaCreacion());
    }
    
    @Test
    @DisplayName("Método toString()")
    void testToString() {
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 100, 10);
        String str = producto.toString();
        
        assertNotNull(str, "toString no debe retornar null");
        assertFalse(str.isEmpty(), "toString no debe retornar string vacío");
        // Verificar que al menos contiene información relevante del producto
        assertTrue(str.length() > 10, "toString debe contener información del producto");
    }
}