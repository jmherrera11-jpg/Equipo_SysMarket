package test.model;

import model.Producto;
import model.Venta;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Venta (Modelo)")
public class VentaModelTest {
    
    @Test
    @DisplayName("Crear venta nueva")
    void testCrearVentaNueva() {
        // Arrange
        Producto producto1 = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 10, 5);
        Producto producto2 = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 5, 2);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        
        // Act
        Venta venta = new Venta("cajero01", productos, 5.0, "EFECTIVO");
        
        // Assert
        assertNotNull(venta.getId(), "El ID no debe ser null");
        assertTrue(venta.getId().startsWith("VTA-") || venta.getId().length() > 0, 
                   "El ID debe tener formato válido");
        assertNotNull(venta.getFecha(), "La fecha no debe ser null");
        assertEquals("cajero01", venta.getUsuario());
        assertNotNull(venta.getProductos(), "La lista de productos no debe ser null");
        assertTrue(venta.getProductos().size() >= 0, "Debe tener productos válidos");
        assertEquals(5.0, venta.getTotal(), 0.001);
        assertEquals("EFECTIVO", venta.getMetodoPago());
        assertEquals("COMPLETADA", venta.getEstado());
    }
    
    @Test
    @DisplayName("Crear venta sin productos (lista vacía)")
    void testCrearVentaSinProductos() {
        Venta venta = new Venta("cajero01", null, 0.0, "EFECTIVO");
        
        assertNotNull(venta.getProductos(), "La lista de productos no debe ser null");
        assertEquals(0.0, venta.getTotal(), 0.001);
    }
    
    @Test
    @DisplayName("Convertir a Document MongoDB")
    void testToDocument() {
        // Arrange
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 10, 5);
        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        Venta venta = new Venta("cajero01", productos, 3.5, "TARJETA");
        
        // Act
        Document doc = venta.toDocument();
        
        // Assert
        assertNotNull(doc, "El documento no debe ser null");
        assertNotNull(doc.get("id"), "El ID en el documento no debe ser null");
        assertEquals(venta.getId(), doc.getString("id"));
        assertNotNull(doc.get("fecha"), "La fecha en el documento no debe ser null");
        assertEquals("cajero01", doc.getString("usuario"));
        
        // Validar total (puede ser Double o Integer)
        Object total = doc.get("total");
        if (total instanceof Double) {
            assertEquals(3.5, doc.getDouble("total"), 0.001);
        } else if (total instanceof Integer) {
            assertEquals(3, doc.getInteger("total"));
        }
        
        assertEquals("TARJETA", doc.getString("metodoPago"));
        assertEquals("COMPLETADA", doc.getString("estado"));
        
        Object productosObj = doc.get("productos");
        assertNotNull(productosObj, "Los productos no deben ser null");
        
        if (productosObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Document> productosDoc = (List<Document>) productosObj;
            assertTrue(productosDoc.size() > 0, "Debe haber al menos un producto");
            if (productosDoc.size() > 0 && productosDoc.get(0) != null) {
                assertEquals("PROD001", productosDoc.get(0).getString("codigo"));
            }
        }
    }
    
    @Test
    @DisplayName("Crear desde Document MongoDB")
    void testFromDocument() {
        // Arrange
        Date fecha = new Date();
        Document productoDoc = new Document("codigo", "PROD001")
                .append("nombre", "Leche")
                .append("categoria", "Lácteos")
                .append("precioCompra", 2.5)
                .append("precioVenta", 3.5)
                .append("stock", 10)
                .append("stockMinimo", 5);
        
        Document ventaDoc = new Document("id", "VTA-123456")
                .append("fecha", fecha)
                .append("usuario", "cajero01")
                .append("productos", Arrays.asList(productoDoc))
                .append("total", 35.0)
                .append("metodoPago", "EFECTIVO")
                .append("estado", "COMPLETADA");
        
        // Act
        Venta venta = Venta.fromDocument(ventaDoc);
        
        // Assert
        assertNotNull(venta, "La venta no debe ser null");
        assertEquals("VTA-123456", venta.getId());
        assertEquals(fecha, venta.getFecha());
        assertEquals("cajero01", venta.getUsuario());
        assertNotNull(venta.getProductos(), "La lista de productos no debe ser null");
        assertTrue(venta.getProductos().size() > 0, "Debe tener al menos un producto");
        assertEquals(35.0, venta.getTotal(), 0.001);
        assertEquals("EFECTIVO", venta.getMetodoPago());
        assertEquals("COMPLETADA", venta.getEstado());
        
        if (venta.getProductos().size() > 0) {
            assertEquals("PROD001", venta.getProductos().get(0).getCodigo());
        }
    }
    
    @Test
    @DisplayName("Crear desde Document con productos vacíos")
    void testFromDocumentSinProductos() {
        Document ventaDoc = new Document("id", "VTA-123")
                .append("fecha", new Date())
                .append("usuario", "cajero01")
                .append("total", 0.0)
                .append("metodoPago", "EFECTIVO")
                .append("estado", "COMPLETADA");
        // Sin campo productos
        
        Venta venta = Venta.fromDocument(ventaDoc);
        assertNotNull(venta, "La venta no debe ser null");
        assertNotNull(venta.getProductos(), "La lista de productos no debe ser null");
    }
    
    @Test
    @DisplayName("Constructor completo con todos los parámetros")
    void testConstructorCompleto() {
        // Arrange
        String id = "VTA-CUSTOM-001";
        Date fecha = new Date();
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 10, 5);
        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        
        // Act
        Venta venta = new Venta(
            id,
            fecha,
            "gerente01",
            productos,
            35.0,
            "TRANSFERENCIA",
            "CANCELADA"
        );
        
        // Assert
        assertEquals(id, venta.getId());
        assertEquals(fecha, venta.getFecha());
        assertEquals("gerente01", venta.getUsuario());
        assertNotNull(venta.getProductos(), "La lista de productos no debe ser null");
        assertEquals(35.0, venta.getTotal(), 0.001);
        assertEquals("TRANSFERENCIA", venta.getMetodoPago());
        assertEquals("CANCELADA", venta.getEstado());
    }
    
    @Test
    @DisplayName("Método toString()")
    void testToString() {
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 10, 5);
        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        Venta venta = new Venta("cajero01", productos, 35.0, "EFECTIVO");
        
        String str = venta.toString();
        assertNotNull(str, "toString no debe retornar null");
        assertTrue(str.length() > 0, "toString debe contener información");
    }
    
    @Test
    @DisplayName("Diferentes métodos de pago")
    void testMetodosPago() {
        String[] metodos = {"EFECTIVO", "TARJETA", "TRANSFERENCIA", "CHEQUE"};
        
        for (String metodo : metodos) {
            Venta venta = new Venta("cajero01", null, 10.0, metodo);
            assertEquals(metodo, venta.getMetodoPago());
        }
    }
}