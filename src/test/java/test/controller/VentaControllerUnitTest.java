package test.controller;

import controller.VentaController;
import model.Producto;
import model.Venta;
import repository.VentaRepository;
import repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para VentaController")
public class VentaControllerUnitTest {
    
    @Mock
    private VentaRepository ventaRepository;
    
    @Mock
    private ProductoRepository productoRepository;
    
    private VentaController ventaController;
    
    @BeforeEach
    void setUp() {
        ventaController = new VentaController(ventaRepository, productoRepository);
    }
    
    @Test
    @DisplayName("Registrar venta válida")
    void testRegistrarVentaValida() {
        // Arrange
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 1, 10);
        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        
        Producto productoBD = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 100, 10);
        
        when(productoRepository.buscarProductoPorCodigo("PROD001")).thenReturn(productoBD);
        doNothing().when(productoRepository).actualizarProducto(eq("PROD001"), any(Producto.class));
        doNothing().when(ventaRepository).registrarVenta(any(Venta.class));
        
        // Act
        Venta venta = ventaController.registrarVenta("cajero01", productos, 3.5, "EFECTIVO");
        
        // Assert
        assertNotNull(venta, "La venta no debe ser null");
        assertEquals("cajero01", venta.getUsuario());
        assertNotNull(venta.getProductos(), "Los productos no deben ser null");
        assertTrue(venta.getProductos().size() > 0, "Debe tener al menos un producto");
        assertEquals(3.5, venta.getTotal(), 0.001);
        assertEquals("EFECTIVO", venta.getMetodoPago());
        
        // Verificar que se llamó a los repositorios
        verify(productoRepository, atLeastOnce()).buscarProductoPorCodigo("PROD001");
        verify(productoRepository, atLeastOnce()).actualizarProducto(eq("PROD001"), any(Producto.class));
        verify(ventaRepository, times(1)).registrarVenta(any(Venta.class));
    }
    
    @Test
    @DisplayName("Registrar venta con producto no encontrado")
    void testRegistrarVentaProductoNoEncontrado() {
        // Arrange
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 1, 5);
        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        
        when(productoRepository.buscarProductoPorCodigo("PROD001")).thenReturn(null);
        
        // Act & Assert
        Exception exception = assertThrows(
            Exception.class,
            () -> ventaController.registrarVenta("cajero01", productos, 3.5, "EFECTIVO")
        );
        
        assertNotNull(exception, "Debe lanzar una excepción");
        String mensaje = exception.getMessage();
        assertTrue(mensaje != null && 
                   (mensaje.contains("Producto no encontrado") || 
                    mensaje.contains("producto") || 
                    mensaje.contains("existe")),
                   "El mensaje debe indicar que el producto no fue encontrado");
        
        verify(ventaRepository, never()).registrarVenta(any());
    }
    
    @Test
    @DisplayName("Obtener todas las ventas")
    void testObtenerTodasVentas() {
        // Arrange
        Venta venta1 = new Venta("cajero01", new ArrayList<>(), 10.0, "EFECTIVO");
        Venta venta2 = new Venta("cajero02", new ArrayList<>(), 20.0, "TARJETA");
        List<Venta> ventasMock = Arrays.asList(venta1, venta2);
        
        when(ventaRepository.obtenerTodasVentas()).thenReturn(ventasMock);
        
        // Act
        List<Venta> resultado = ventaController.obtenerTodasVentas();
        
        // Assert
        assertNotNull(resultado, "El resultado no debe ser null");
        assertEquals(2, resultado.size());
        assertEquals("cajero01", resultado.get(0).getUsuario());
        assertEquals("cajero02", resultado.get(1).getUsuario());
        verify(ventaRepository, times(1)).obtenerTodasVentas();
    }
    
    @Test
    @DisplayName("Calcular total de ventas")
    void testCalcularTotalVentas() {
        // Arrange
        Venta venta1 = new Venta("cajero01", new ArrayList<>(), 15.0, "EFECTIVO");
        Venta venta2 = new Venta("cajero02", new ArrayList<>(), 25.0, "TARJETA");
        Venta venta3 = new Venta("cajero03", new ArrayList<>(), 10.0, "TRANSFERENCIA");
        List<Venta> ventas = Arrays.asList(venta1, venta2, venta3);
        
        // Act
        double total = ventaController.calcularTotalVentas(ventas);
        
        // Assert
        assertEquals(50.0, total, 0.001);
    }
}