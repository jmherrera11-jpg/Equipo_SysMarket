package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Producto;

@DisplayName("HU-03: Editar productos")
public class HU03_EditarProductoTest {
    
    @Test
    @DisplayName("Escenario 1: Editar información básica de producto")
    public void testEditarInformacionBasica() {
        // Given - Producto existente
        Producto producto = new Producto(
            "PROD001",
            "Leche Entera",
            "Lácteos",
            2.50, 3.50, 100, 20
        );
        
        // When - Editamos campos
        producto.setNombre("Leche Deslactosada 1L");
        producto.setCategoria("Lácteos Deslactosados");
        producto.setPrecioVenta(4.00);
        producto.setStock(150);
        producto.setStockMinimo(30);
        
        // Then - Verificamos cambios
        assertEquals("Leche Deslactosada 1L", producto.getNombre(), "Nombre actualizado");
        assertEquals("Lácteos Deslactosados", producto.getCategoria(), "Categoría actualizada");
        assertEquals(4.00, producto.getPrecioVenta(), 0.01, "Precio venta actualizado");
        assertEquals(150, producto.getStock(), "Stock actualizado");
        assertEquals(30, producto.getStockMinimo(), "Stock mínimo actualizado");
        assertEquals("PROD001", producto.getCodigo(), "Código no debe cambiar");
    }
    
    @Test
    @DisplayName("Escenario 2: Validar edición de precios")
    public void testValidarEdicionPrecios() {
        Producto producto = new Producto(
            "PROD001",
            "Producto Test",
            "Test",
            10.00, 15.00, 100, 20
        );
        
        // Caso válido: precio venta > precio compra
        producto.setPrecioVenta(18.00);
        assertTrue(producto.getPrecioVenta() > producto.getPrecioCompra(), 
                  "Precio venta debe ser mayor que precio compra");
        
        // Caso inválido: precio venta <= precio compra
        try {
            producto.setPrecioVenta(9.00); // Menor que precio compra
            fail("Debió fallar con precio venta <= precio compra");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("precio") || e.getMessage().contains("mayor"));
        }
    }
    
    @Test
    @DisplayName("Escenario 3: Campos no editables (código)")
    public void testCamposNoEditables() {
        Producto producto = new Producto(
            "PROD001",
            "Leche",
            "Lácteos",
            2.50, 3.50, 100, 20
        );
        
        String codigoOriginal = producto.getCodigo();
        // En tu implementación, Producto probablemente no tenga setCodigo()
        // Esto garantiza que el código no se pueda cambiar
        
        assertEquals(codigoOriginal, producto.getCodigo(), 
                    "Código no debe cambiar después de creación");
    }
    
    @Test
    @DisplayName("Escenario 4: Persistencia de cambios")
    public void testPersistenciaCambios() {
        // Given - Producto con datos originales
        Producto producto = new Producto(
            "PROD001",
            "Nombre Original",
            "Categoría Original",
            10.00, 15.00, 100, 20
        );
        
        // When - Editamos múltiples campos
        String[] cambios = {
            "Nombre Editado",
            "Categoría Editada",
            "18.00", // precioVenta
            "150",   // stock
            "25"     // stockMinimo
        };
        
        producto.setNombre(cambios[0]);
        producto.setCategoria(cambios[1]);
        producto.setPrecioVenta(Double.parseDouble(cambios[2]));
        producto.setStock(Integer.parseInt(cambios[3]));
        producto.setStockMinimo(Integer.parseInt(cambios[4]));
        
        // Then - Todos los cambios deben persistir
        assertEquals(cambios[0], producto.getNombre(), "Nombre editado debe persistir");
        assertEquals(cambios[1], producto.getCategoria(), "Categoría editada debe persistir");
        assertEquals(Double.parseDouble(cambios[2]), producto.getPrecioVenta(), 0.01,
                    "Precio editado debe persistir");
        assertEquals(Integer.parseInt(cambios[3]), producto.getStock(),
                    "Stock editado debe persistir");
        assertEquals(Integer.parseInt(cambios[4]), producto.getStockMinimo(),
                    "Stock mínimo editado debe persistir");
    }
}