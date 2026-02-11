package test.controller;

import controller.ProductoController;
import model.Producto;
import repository.ProductoRepository;
import repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para ProductoController")
public class ProductoControllerUnitTest {
    
    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    private ProductoController productoController;
    
    @BeforeEach
    void setUp() {
        productoController = new ProductoController(productoRepository, categoriaRepository);
    }
    
    @Test
    @DisplayName("Validar producto correcto")
    void testValidarProductoCorrecto() {
        // Arrange
        Producto producto = new Producto(
            "PROD001",
            "Leche",
            "Lácteos",
            2.50,
            3.50,
            100,
            10
        );
        
        when(productoRepository.existeProducto("PROD001")).thenReturn(false);
        
        // Act & Assert
        assertDoesNotThrow(() -> productoController.agregarProducto(producto));
        
        // Verificar que se llamó al repositorio
        verify(productoRepository, times(1)).agregarProducto(producto);
    }
    
    @Test
    @DisplayName("Validar producto sin código")
    void testValidarProductoSinCodigo() {
        Producto producto = new Producto("", "Leche", "Lácteos", 2.50, 3.50, 100, 10);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto)
        );
        assertEquals("El código del producto es obligatorio", exception.getMessage());
        
        // Verificar que NO se llamó al repositorio
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Validar producto sin nombre")
    void testValidarProductoSinNombre() {
        Producto producto = new Producto("PROD001", "", "Lácteos", 2.50, 3.50, 100, 10);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto)
        );
        assertEquals("El nombre del producto es obligatorio", exception.getMessage());
        
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Validar precio compra cero o negativo")
    void testValidarPrecioCompraInvalido() {
        // Precio compra cero
        Producto producto1 = new Producto("PROD001", "Leche", "Lácteos", 0, 3.50, 100, 10);
        
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto1)
        );
        assertEquals("El precio de compra debe ser mayor a 0", exception1.getMessage());
        
        // Precio compra negativo
        Producto producto2 = new Producto("PROD002", "Leche", "Lácteos", -1.0, 3.50, 100, 10);
        
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto2)
        );
        assertEquals("El precio de compra debe ser mayor a 0", exception2.getMessage());
        
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Validar precio venta menor o igual a precio compra")
    void testValidarPrecioVentaInvalido() {
        // Precio venta igual a compra
        Producto producto1 = new Producto("PROD001", "Leche", "Lácteos", 3.50, 3.50, 100, 10);
        
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto1)
        );
        assertEquals("El precio de venta debe ser mayor al precio de compra", exception1.getMessage());
        
        // Precio venta menor que compra
        Producto producto2 = new Producto("PROD002", "Leche", "Lácteos", 3.50, 3.00, 100, 10);
        
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto2)
        );
        assertEquals("El precio de venta debe ser mayor al precio de compra", exception2.getMessage());
        
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Validar stock negativo")
    void testValidarStockNegativo() {
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.50, 3.50, -10, 10);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto)
        );
        assertEquals("El stock no puede ser negativo", exception.getMessage());
        
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Validar stock mínimo negativo")
    void testValidarStockMinimoNegativo() {
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.50, 3.50, 100, -10);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productoController.agregarProducto(producto)
        );
        assertEquals("El stock mínimo no puede ser negativo", exception.getMessage());
        
        verify(productoRepository, never()).agregarProducto(any());
    }
    
    @Test
    @DisplayName("Verificar existencia de producto")
    void testExisteProducto() {
        // Arrange
        when(productoRepository.existeProducto("PROD001")).thenReturn(true);
        
        // Act
        boolean resultado = productoController.existeProducto("PROD001");
        
        // Assert
        assertTrue(resultado);
        verify(productoRepository, times(1)).existeProducto("PROD001");
    }
    
    @Test
    @DisplayName("Obtener todos los productos")
    void testObtenerTodosProductos() {
        // Arrange
        Producto p1 = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 100, 10);
        Producto p2 = new Producto("PROD002", "Pan", "Panadería", 1.0, 1.5, 50, 5);
        List<Producto> productosMock = Arrays.asList(p1, p2);
        
        when(productoRepository.obtenerTodosProductos()).thenReturn(productosMock);
        
        // Act
        List<Producto> resultado = productoController.obtenerTodosProductos();
        
        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Leche", resultado.get(0).getNombre());
        assertEquals("Pan", resultado.get(1).getNombre());
        verify(productoRepository, times(1)).obtenerTodosProductos();
    }
    
    @Test
    @DisplayName("Buscar producto por código")
    void testBuscarProductoPorCodigo() {
        // Arrange
        Producto producto = new Producto("PROD001", "Leche", "Lácteos", 2.5, 3.5, 100, 10);
        
        when(productoRepository.buscarProductoPorCodigo("PROD001")).thenReturn(producto);
        
        // Act
        Producto resultado = productoController.buscarProductoPorCodigo("PROD001");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("PROD001", resultado.getCodigo());
        assertEquals("Leche", resultado.getNombre());
        verify(productoRepository, times(1)).buscarProductoPorCodigo("PROD001");
    }
    
    @Test
    @DisplayName("Obtener categorías")
    void testObtenerCategorias() {
        // Arrange
        List<String> categoriasMock = Arrays.asList("Lácteos", "Bebidas", "Abarrotes");
        
        when(categoriaRepository.obtenerTodasCategorias()).thenReturn(categoriasMock);
        
        // Act
        List<String> resultado = productoController.obtenerCategorias();
        
        // Assert
        assertEquals(3, resultado.size());
        assertTrue(resultado.contains("Lácteos"));
        assertTrue(resultado.contains("Bebidas"));
        assertTrue(resultado.contains("Abarrotes"));
        verify(categoriaRepository, times(1)).obtenerTodasCategorias();
    }
}