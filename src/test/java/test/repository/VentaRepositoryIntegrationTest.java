package test.repository;

import repository.VentaRepository;
import repository.ProductoRepository;
import model.Producto;
import model.Venta;
import database.MongoDBConnection;
import org.junit.jupiter.api.*;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración para VentaRepository")
public class VentaRepositoryIntegrationTest {
    
    private static VentaRepository ventaRepository;
    private static ProductoRepository productoRepository;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Iniciando tests de integración para VentaRepository...");
        MongoDBConnection.connect();
        
        var database = MongoDBConnection.getDatabase();
        ventaRepository = new VentaRepository(database);
        productoRepository = new ProductoRepository(database);
        
        // Limpiar datos de prueba
        limpiarDatosPrueba();
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Finalizando tests de integración para VentaRepository...");
        limpiarDatosPrueba();
        MongoDBConnection.close();
    }
    
    private static void limpiarDatosPrueba() {
        try {
            var database = MongoDBConnection.getDatabase();
            database.getCollection("ventas").deleteMany(new Document("usuario", new Document("$regex", "^test_")));
            database.getCollection("productos").deleteMany(new Document("codigo", new Document("$regex", "^TEST_VENTA_")));
        } catch (Exception e) {
            System.out.println("Error al limpiar datos: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Registrar venta con productos")
    void testRegistrarVentaConProductos() {
        try {
            // Arrange - Crear productos primero
            Producto producto1 = new Producto("TEST_VENTA_001", "Leche Venta", "Lácteos", 2.5, 3.5, 100, 10);
            Producto producto2 = new Producto("TEST_VENTA_002", "Pan Venta", "Panadería", 1.0, 1.5, 50, 5);
            
            productoRepository.agregarProducto(producto1);
            productoRepository.agregarProducto(producto2);
            
            List<Producto> productosVenta = Arrays.asList(producto1, producto2);
            Venta venta = new Venta("test_cajero01", productosVenta, 5.0, "EFECTIVO");
            
            // Act
            ventaRepository.registrarVenta(venta);
            
            // Assert
            List<Venta> ventas = ventaRepository.obtenerTodasVentas();
            boolean encontrada = ventas.stream()
                .anyMatch(v -> v.getUsuario().equals("test_cajero01") && 
                              Math.abs(v.getTotal() - 5.0) < 0.01);
            assertTrue(encontrada, "La venta debería haberse registrado");
            
        } catch (Exception e) {
            fail("Error al registrar venta: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Obtener todas las ventas")
    void testObtenerTodasVentas() {
        try {
            // Arrange - Registrar otra venta
            Producto producto = new Producto("TEST_VENTA_003", "Queso Venta", "Lácteos", 5.0, 7.0, 25, 3);
            productoRepository.agregarProducto(producto);
            
            Venta venta = new Venta("test_cajero02", Arrays.asList(producto), 7.0, "TARJETA");
            ventaRepository.registrarVenta(venta);
            
            // Act
            List<Venta> ventas = ventaRepository.obtenerTodasVentas();
            
            // Assert
            assertNotNull(ventas);
            assertTrue(ventas.size() >= 2); // Debería haber al menos las 2 ventas de prueba
            
            // Verificar que están las ventas de prueba
            boolean tieneCajero01 = ventas.stream()
                .anyMatch(v -> v.getUsuario().equals("test_cajero01"));
            boolean tieneCajero02 = ventas.stream()
                .anyMatch(v -> v.getUsuario().equals("test_cajero02"));
            
            assertTrue(tieneCajero01, "Debería tener venta de test_cajero01");
            assertTrue(tieneCajero02, "Debería tener venta de test_cajero02");
            
        } catch (Exception e) {
            fail("Error al obtener ventas: " + e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Obtener ventas por usuario")
    void testObtenerVentasPorUsuario() {
        try {
            // Arrange - Registrar venta específica para usuario
            Producto producto = new Producto("TEST_VENTA_004", "Refresco Venta", "Bebidas", 1.0, 2.0, 100, 20);
            productoRepository.agregarProducto(producto);
            
            Venta ventaEspecifica = new Venta("test_usuario_especifico", Arrays.asList(producto), 2.0, "EFECTIVO");
            ventaRepository.registrarVenta(ventaEspecifica);
            
            // Act
            List<Venta> ventasUsuario = ventaRepository.obtenerVentasPorUsuario("test_usuario_especifico");
            
            // Assert
            assertNotNull(ventasUsuario);
            assertFalse(ventasUsuario.isEmpty());
            
            // Todas las ventas deberían ser del usuario específico
            boolean todasDelUsuario = ventasUsuario.stream()
                .allMatch(v -> v.getUsuario().equals("test_usuario_especifico"));
            assertTrue(todasDelUsuario);
            
            // Verificar que contiene la venta recién registrada
            boolean contieneVenta = ventasUsuario.stream()
                .anyMatch(v -> Math.abs(v.getTotal() - 2.0) < 0.01);
            assertTrue(contieneVenta);
            
        } catch (Exception e) {
            fail("Error al obtener ventas por usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Obtener ventas por usuario sin ventas")
    void testObtenerVentasPorUsuarioSinVentas() {
        // Act
        List<Venta> ventas = ventaRepository.obtenerVentasPorUsuario("usuario_sin_ventas_12345");
        
        // Assert
        assertNotNull(ventas);
        assertTrue(ventas.isEmpty());
    }
    
    @Test
    @Order(5)
    @DisplayName("Registrar venta con múltiples productos")
    void testRegistrarVentaMultiplesProductos() {
        try {
            // Arrange - Crear múltiples productos
            Producto[] productos = {
                new Producto("TEST_VENTA_005", "Producto 1", "Lácteos", 1.0, 2.0, 10, 5),
                new Producto("TEST_VENTA_006", "Producto 2", "Bebidas", 1.5, 3.0, 20, 10),
                new Producto("TEST_VENTA_007", "Producto 3", "Abarrotes", 0.5, 1.0, 30, 15)
            };
            
            for (Producto producto : productos) {
                productoRepository.agregarProducto(producto);
            }
            
            Venta venta = new Venta("test_cajero03", Arrays.asList(productos), 6.0, "TRANSFERENCIA");
            
            // Act
            ventaRepository.registrarVenta(venta);
            
            // Assert - Verificar en la lista general
            List<Venta> todasVentas = ventaRepository.obtenerTodasVentas();
            boolean encontrada = todasVentas.stream()
                .anyMatch(v -> v.getUsuario().equals("test_cajero03") && 
                              v.getMetodoPago().equals("TRANSFERENCIA"));
            assertTrue(encontrada);
            
            // Verificar específicamente por usuario
            List<Venta> ventasUsuario = ventaRepository.obtenerVentasPorUsuario("test_cajero03");
            assertFalse(ventasUsuario.isEmpty());
            assertEquals("TRANSFERENCIA", ventasUsuario.get(0).getMetodoPago());
            
        } catch (Exception e) {
            fail("Error con venta múltiple: " + e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Verificar persistencia de datos de venta")
    void testPersistenciaDatosVenta() {
        try {
            // Arrange
            Producto producto = new Producto("TEST_VENTA_008", "Producto Persistencia", "Test", 2.0, 4.0, 50, 10);
            productoRepository.agregarProducto(producto);
            
            Venta ventaOriginal = new Venta("test_persistencia", Arrays.asList(producto), 4.0, "CHEQUE");
            
            // Act
            ventaRepository.registrarVenta(ventaOriginal);
            
            // Assert - Obtener ventas y verificar datos
            List<Venta> ventas = ventaRepository.obtenerVentasPorUsuario("test_persistencia");
            assertFalse(ventas.isEmpty());
            
            Venta ventaRecuperada = ventas.get(0);
            assertNotNull(ventaRecuperada);
            
            // Verificar datos principales
            assertEquals("test_persistencia", ventaRecuperada.getUsuario());
            assertEquals(4.0, ventaRecuperada.getTotal(), 0.001);
            assertEquals("CHEQUE", ventaRecuperada.getMetodoPago());
            assertEquals("COMPLETADA", ventaRecuperada.getEstado());
            
            // Verificar productos
            assertFalse(ventaRecuperada.getProductos().isEmpty());
            assertEquals("TEST_VENTA_008", ventaRecuperada.getProductos().get(0).getCodigo());
            assertEquals("Producto Persistencia", ventaRecuperada.getProductos().get(0).getNombre());
            
        } catch (Exception e) {
            fail("Error en prueba de persistencia: " + e.getMessage());
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Registrar venta vacía (sin productos)")
    void testRegistrarVentaVacia() {
        try {
            // Arrange
            Venta ventaVacia = new Venta("test_vacia", null, 0.0, "EFECTIVO");
            
            // Act
            ventaRepository.registrarVenta(ventaVacia);
            
            // Assert
            List<Venta> ventas = ventaRepository.obtenerVentasPorUsuario("test_vacia");
            assertFalse(ventas.isEmpty());
            
            Venta ventaRecuperada = ventas.get(0);
            assertNotNull(ventaRecuperada.getProductos());
            assertTrue(ventaRecuperada.getProductos().isEmpty());
            assertEquals(0.0, ventaRecuperada.getTotal(), 0.001);
            
        } catch (Exception e) {
            fail("Error con venta vacía: " + e.getMessage());
        }
    }
}