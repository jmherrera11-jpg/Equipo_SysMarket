package test.controller;

import controller.GerenteController;
import model.Producto;
import model.Venta;
import repository.ProductoRepository;
import repository.VentaRepository;
import repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para GerenteController")
public class GerenteControllerUnitTest {
    
    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private VentaRepository ventaRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    private GerenteController gerenteController;
    
    @BeforeEach
    void setUp() {
        gerenteController = new GerenteController(productoRepository, ventaRepository, categoriaRepository);
    }
    
    @Test
    @DisplayName("Obtener productos con stock bajo")
    void testObtenerProductosStockBajo() {
        // Arrange
        Producto productoConStockBajo = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 5, 10);
        Producto productoConStockNormal = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 20, 5);
        List<Producto> productos = Arrays.asList(productoConStockBajo, productoConStockNormal);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productos);
        
        // Act
        List<Producto> resultado = gerenteController.obtenerProductosStockBajo();
        
        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Leche", resultado.get(0).getNombre());
        assertEquals(5, resultado.get(0).getStock());
        assertEquals(10, resultado.get(0).getStockMinimo());
        verify(productoRepository, times(1)).obtenerTodosProductos();
    }
    
    @Test
    @DisplayName("Generar reporte de stock crítico")
    void testGenerarReporteStock() {
        // Arrange
        Producto producto1 = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 3, 10);
        Producto producto2 = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 4, 5);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productos);
        
        // Act
        String reporte = gerenteController.generarReporteStock();
        
        // Assert
        assertNotNull(reporte);
        assertTrue(reporte.contains("REPORTE DE STOCK CRÍTICO"));
        assertTrue(reporte.contains("Leche"));
        assertTrue(reporte.contains("Pan"));
        verify(productoRepository, times(1)).obtenerTodosProductos();
    }
    
    @Test
    @DisplayName("Generar reporte de stock sin productos críticos")
    void testGenerarReporteStockSinCriticos() {
        // Arrange
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 15, 10);
        List<Producto> productos = Arrays.asList(producto);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productos);
        
        // Act
        String reporte = gerenteController.generarReporteStock();
        
        // Assert
        assertNotNull(reporte);
        assertTrue(reporte.contains("No hay productos con stock crítico"));
        verify(productoRepository, times(1)).obtenerTodosProductos();
    }
    
    @Test
    @DisplayName("Generar reporte de productos más vendidos")
    void testGenerarReporteProductosMasVendidos() {
        // Arrange
        Producto leche = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 10, 5);
        Producto pan = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 5, 2);
        
        Venta venta1 = new Venta("cajero01", Arrays.asList(leche, leche, pan), 8.5, "EFECTIVO");
        Venta venta2 = new Venta("cajero02", Arrays.asList(leche, pan), 5.0, "TARJETA");
        List<Venta> ventas = Arrays.asList(venta1, venta2);
        
        when(ventaRepository.obtenerVentasPorRangoFechas("2024-01-01", "2024-01-31")).thenReturn(ventas);
        
        // Act
        String reporte = gerenteController.generarReporteProductosMasVendidos("2024-01-01", "2024-01-31");
        
        // Assert
        assertNotNull(reporte);
        assertTrue(reporte.contains("REPORTE DE PRODUCTOS MÁS VENDIDOS"));
        verify(ventaRepository, times(1)).obtenerVentasPorRangoFechas("2024-01-01", "2024-01-31");
    }
    
    @Test
    @DisplayName("Generar reporte de rentabilidad")
    void testGenerarReporteRentabilidad() {
        // Arrange
        Producto productoAlta = new Producto("PROD001", "Producto Alto", "Categoría", 10.0, 15.0, 100, 10);
        Producto productoMedia = new Producto("PROD002", "Producto Medio", "Categoría", 10.0, 12.0, 100, 10);
        Producto productoBaja = new Producto("PROD003", "Producto Bajo", "Categoría", 10.0, 11.0, 100, 10);
        List<Producto> productos = Arrays.asList(productoAlta, productoMedia, productoBaja);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productos);
        
        // Act
        String reporte = gerenteController.generarReporteRentabilidad();
        
        // Assert
        assertNotNull(reporte);
        assertTrue(reporte.contains("REPORTE DE RENTABILIDAD POR PRODUCTO"));
        verify(productoRepository, times(1)).obtenerTodosProductos();
    }
    
    @Test
    @DisplayName("Obtener estadísticas del dashboard")
    void testObtenerEstadisticasDashboard() {
        // Arrange
        Producto leche = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 3, 10);
        Producto pan = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 20, 5);
        List<Producto> productos = Arrays.asList(leche, pan);
        
        Venta venta1 = new Venta("cajero01", Arrays.asList(leche, leche, pan), 8.5, "EFECTIVO");
        Venta venta2 = new Venta("cajero02", Arrays.asList(leche, pan), 5.0, "TARJETA");
        List<Venta> ventas = Arrays.asList(venta1, venta2);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productos);
        when(ventaRepository.obtenerTodasVentas()).thenReturn(ventas);
        
        // Act
        Map<String, Object> estadisticas = gerenteController.obtenerEstadisticasDashboard();
        
        // Assert
        assertNotNull(estadisticas);
        assertEquals(13.5, (Double) estadisticas.get("totalVentas"), 0.001);
        verify(productoRepository, times(1)).obtenerTodosProductos();
        verify(ventaRepository, times(1)).obtenerTodasVentas();
    }
    
    @Test
    @DisplayName("Obtener estadísticas del dashboard sin datos")
    void testObtenerEstadisticasDashboardSinDatos() {
        // Arrange
        when(productoRepository.obtenerTodosProductos()).thenReturn(Arrays.asList());
        when(ventaRepository.obtenerTodasVentas()).thenReturn(Arrays.asList());
        
        // Act
        Map<String, Object> estadisticas = gerenteController.obtenerEstadisticasDashboard();
        
        // Assert
        assertNotNull(estadisticas);
        assertEquals(0.0, (Double) estadisticas.get("totalVentas"), 0.001);
        assertTrue(estadisticas.get("productoMasVendido").equals("Sin datos") || 
                  estadisticas.get("productoMasVendido").equals("No disponible"));
        verify(productoRepository, times(1)).obtenerTodosProductos();
        verify(ventaRepository, times(1)).obtenerTodasVentas();
    }
}