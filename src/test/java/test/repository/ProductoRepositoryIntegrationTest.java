package test.repository;

import repository.ProductoRepository;
import model.Producto;
import database.MongoDBConnection;
import org.junit.jupiter.api.*;
import org.bson.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración para ProductoRepository")
public class ProductoRepositoryIntegrationTest {
    
    private static ProductoRepository productoRepository;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Iniciando tests de integración para ProductoRepository...");
        MongoDBConnection.connect();
        
        var database = MongoDBConnection.getDatabase();
        productoRepository = new ProductoRepository(database);
        
        // Limpiar productos de prueba
        limpiarProductosPrueba();
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Finalizando tests de integración para ProductoRepository...");
        limpiarProductosPrueba();
        MongoDBConnection.close();
    }
    
    private static void limpiarProductosPrueba() {
        try {
            var database = MongoDBConnection.getDatabase();
            var collection = database.getCollection("productos");
            collection.deleteMany(new Document("codigo", new Document("$regex", "^TEST_")));
        } catch (Exception e) {
            System.out.println("Error al limpiar productos: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Agregar nuevo producto")
    void testAgregarProducto() {
        try {
            // Arrange
            Producto producto = new Producto(
                "TEST_001",
                "Leche Test",
                "Lácteos",
                2.50,
                3.50,
                100,
                10
            );
            
            // Act
            productoRepository.agregarProducto(producto);
            
            // Assert
            Producto productoGuardado = productoRepository.buscarProductoPorCodigo("TEST_001");
            assertNotNull(productoGuardado);
            assertEquals("TEST_001", productoGuardado.getCodigo());
            assertEquals("Leche Test", productoGuardado.getNombre());
            assertEquals("Lácteos", productoGuardado.getCategoria());
            assertEquals(2.50, productoGuardado.getPrecioCompra(), 0.001);
            assertEquals(3.50, productoGuardado.getPrecioVenta(), 0.001);
            assertEquals(100, productoGuardado.getStock());
            assertEquals(10, productoGuardado.getStockMinimo());
            assertEquals("ACTIVO", productoGuardado.getEstado());
            
        } catch (Exception e) {
            fail("Error al agregar producto: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Buscar producto por código")
    void testBuscarProductoPorCodigo() {
        // Arrange
        String codigo = "TEST_001";
        
        // Act
        Producto encontrado = productoRepository.buscarProductoPorCodigo(codigo);
        
        // Assert
        assertNotNull(encontrado);
        assertEquals(codigo, encontrado.getCodigo());
        assertEquals("Leche Test", encontrado.getNombre());
    }
    
    @Test
    @Order(3)
    @DisplayName("Buscar producto no existente")
    void testBuscarProductoNoExistente() {
        // Act
        Producto encontrado = productoRepository.buscarProductoPorCodigo("CODIGO_INEXISTENTE_123");
        
        // Assert
        assertNull(encontrado);
    }
    
    @Test
    @Order(4)
    @DisplayName("Verificar existencia de producto")
    void testExisteProducto() {
        // Producto existente
        assertTrue(productoRepository.existeProducto("TEST_001"));
        
        // Producto no existente
        assertFalse(productoRepository.existeProducto("PRODUCTO_INEXISTENTE_999"));
    }
    
    @Test
    @Order(5)
    @DisplayName("Obtener todos los productos")
    void testObtenerTodosProductos() {
        // Arrange - Agregar más productos
        Producto p2 = new Producto("TEST_002", "Pan Test", "Panadería", 1.00, 1.50, 50, 5);
        Producto p3 = new Producto("TEST_003", "Queso Test", "Lácteos", 5.00, 7.00, 25, 3);
        
        productoRepository.agregarProducto(p2);
        productoRepository.agregarProducto(p3);
        
        // Act
        List<Producto> productos = productoRepository.obtenerTodosProductos();
        
        // Assert
        assertNotNull(productos);
        assertTrue(productos.size() >= 3); // Debería haber al menos los 3 productos test
        
        // Verificar que están todos
        long productosTest = productos.stream()
            .filter(p -> p.getCodigo().startsWith("TEST_"))
            .count();
        assertTrue(productosTest >= 3);
    }
    
    @Test
    @Order(6)
    @DisplayName("Buscar productos por categoría")
    void testBuscarProductosPorCategoria() {
        // Act
        List<Producto> lacteos = productoRepository.buscarProductosPorCategoria("Lácteos");
        
        // Assert
        assertNotNull(lacteos);
        assertTrue(lacteos.size() >= 2); // TEST_001 y TEST_003 son lácteos
        
        // Verificar que todos son lácteos
        boolean todosLacteos = lacteos.stream()
            .allMatch(p -> "Lácteos".equals(p.getCategoria()));
        assertTrue(todosLacteos);
    }
    
    @Test
    @Order(7)
    @DisplayName("Actualizar producto existente")
    void testActualizarProducto() {
        try {
            // Arrange - Crear producto para actualizar
            Producto productoOriginal = new Producto(
                "TEST_UPDATE",
                "Producto Original",
                "Original",
                1.00,
                2.00,
                50,
                5
            );
            productoRepository.agregarProducto(productoOriginal);
            
            // Producto actualizado
            Producto productoActualizado = new Producto(
                "TEST_UPDATE",
                "Producto Actualizado",
                "Actualizada",
                1.50,
                3.00,
                75,
                8
            );
            
            // Act
            productoRepository.actualizarProducto("TEST_UPDATE", productoActualizado);
            
            // Assert
            Producto resultado = productoRepository.buscarProductoPorCodigo("TEST_UPDATE");
            assertNotNull(resultado);
            assertEquals("Producto Actualizado", resultado.getNombre());
            assertEquals("Actualizada", resultado.getCategoria());
            assertEquals(1.50, resultado.getPrecioCompra(), 0.001);
            assertEquals(3.00, resultado.getPrecioVenta(), 0.001);
            assertEquals(75, resultado.getStock());
            assertEquals(8, resultado.getStockMinimo());
            
        } catch (Exception e) {
            fail("Error al actualizar producto: " + e.getMessage());
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("Eliminar producto existente")
    void testEliminarProducto() {
        try {
            // Arrange - Crear producto para eliminar
            Producto productoEliminar = new Producto(
                "TEST_DELETE",
                "Producto a Eliminar",
                "Test",
                1.00,
                2.00,
                10,
                2
            );
            productoRepository.agregarProducto(productoEliminar);
            
            // Verificar que existe
            assertNotNull(productoRepository.buscarProductoPorCodigo("TEST_DELETE"));
            
            // Act
            productoRepository.eliminarProducto("TEST_DELETE");
            
            // Assert
            assertNull(productoRepository.buscarProductoPorCodigo("TEST_DELETE"));
            assertFalse(productoRepository.existeProducto("TEST_DELETE"));
            
        } catch (Exception e) {
            fail("Error al eliminar producto: " + e.getMessage());
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("Actualizar stock de producto")
    void testActualizarStock() {
        try {
            // Arrange - Crear producto
            Producto producto = new Producto(
                "TEST_STOCK",
                "Producto Stock",
                "Test",
                1.00,
                2.00,
                100,
                10
            );
            productoRepository.agregarProducto(producto);
            
            // Act - Reducir stock en 25 unidades
            productoRepository.actualizarStock("TEST_STOCK", 25);
            
            // Assert
            Producto resultado = productoRepository.buscarProductoPorCodigo("TEST_STOCK");
            assertNotNull(resultado);
            assertEquals(75, resultado.getStock()); // 100 - 25 = 75
            
        } catch (Exception e) {
            fail("Error al actualizar stock: " + e.getMessage());
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("CRUD completo de un producto")
    void testCRUDCompleto() {
        try {
            String codigo = "TEST_CRUD";
            
            // 1. CREATE
            Producto crear = new Producto(
                codigo,
                "Producto CRUD",
                "Test",
                1.00,
                2.00,
                50,
                5
            );
            productoRepository.agregarProducto(crear);
            assertNotNull(productoRepository.buscarProductoPorCodigo(codigo));
            
            // 2. READ
            Producto leido = productoRepository.buscarProductoPorCodigo(codigo);
            assertEquals("Producto CRUD", leido.getNombre());
            
            // 3. UPDATE
            Producto actualizar = new Producto(
                codigo,
                "Producto CRUD Actualizado",
                "Test Actualizado",
                1.50,
                3.00,
                75,
                8
            );
            productoRepository.actualizarProducto(codigo, actualizar);
            
            Producto actualizado = productoRepository.buscarProductoPorCodigo(codigo);
            assertEquals("Producto CRUD Actualizado", actualizado.getNombre());
            assertEquals(1.50, actualizado.getPrecioCompra(), 0.001);
            
            // 4. DELETE
            productoRepository.eliminarProducto(codigo);
            assertNull(productoRepository.buscarProductoPorCodigo(codigo));
            
            System.out.println("✓ CRUD completo ejecutado exitosamente");
            
        } catch (Exception e) {
            fail("Error en CRUD completo: " + e.getMessage());
        }
    }
}