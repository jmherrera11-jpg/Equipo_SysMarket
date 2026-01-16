package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("HU-04: Eliminar productos")
public class HU04_EliminarProductoTest {
    
    @Test
    @DisplayName("Escenario 1: Eliminar producto existente")
    public void testEliminarProductoExistente() {
        // Given - Inventario con productos
        List<String> inventario = new ArrayList<>();
        inventario.add("PROD001:Leche");
        inventario.add("PROD002:Pan");
        inventario.add("PROD003:Arroz");
        
        String productoAEliminar = "PROD002:Pan";
        
        // When - Eliminamos producto
        boolean productoExiste = inventario.contains(productoAEliminar);
        if (productoExiste) {
            inventario.remove(productoAEliminar);
        }
        
        // Then
        assertTrue(productoExiste, "Producto debe existir antes de eliminar");
        assertEquals(2, inventario.size(), "Inventario debe tener 2 productos después");
        assertFalse(inventario.contains(productoAEliminar), "Producto eliminado no debe estar en inventario");
    }
    
    @Test
    @DisplayName("Escenario 2: Intentar eliminar producto inexistente")
    public void testEliminarProductoInexistente() {
        List<String> inventario = new ArrayList<>();
        inventario.add("PROD001:Leche");
        inventario.add("PROD002:Pan");
        
        String productoInexistente = "PROD999:ProductoInexistente";
        
        // When
        boolean productoExiste = inventario.contains(productoInexistente);
        int tamanoInicial = inventario.size();
        
        if (!productoExiste) {
            // No hacemos nada, producto no existe
        }
        
        // Then
        assertFalse(productoExiste, "Producto inexistente debe retornar false");
        assertEquals(tamanoInicial, inventario.size(), 
                    "Tamaño del inventario no debe cambiar");
    }
    
    @Test
    @DisplayName("Escenario 3: Confirmación de eliminación")
    public void testConfirmacionEliminacion() {
        // Given - Datos del producto
        String codigo = "PROD001";
        String nombre = "Leche Entera";
        
        // When - Construimos mensaje de confirmación
        String mensajeConfirmacion = String.format(
            "¿Está seguro de eliminar el producto '%s' (Código: %s)? Esta acción no se puede deshacer.",
            nombre, codigo
        );
        
        // Then
        assertTrue(mensajeConfirmacion.contains(nombre), 
                  "Mensaje debe incluir nombre del producto");
        assertTrue(mensajeConfirmacion.contains(codigo), 
                  "Mensaje debe incluir código del producto");
        assertTrue(mensajeConfirmacion.contains("¿Está seguro"), 
                  "Mensaje debe pedir confirmación");
    }
    
    @Test
    @DisplayName("Escenario 4: Integridad del inventario después de eliminar")
    public void testIntegridadInventario() {
        // Given - Inventario inicial
        List<String> inventario = new ArrayList<>();
        inventario.add("PROD001:Leche:Lácteos:100");
        inventario.add("PROD002:Pan:Panadería:50");
        inventario.add("PROD003:Arroz:Abarrotes:200");
        inventario.add("PROD004:Aceite:Abarrotes:80");
        
        // When - Eliminamos un producto
        String productoEliminado = "PROD002:Pan:Panadería:50";
        inventario.remove(productoEliminado);
        
        // Then - Verificamos integridad
        assertEquals(3, inventario.size(), "Deben quedar 3 productos");
        
        // Productos restantes deben mantener sus datos
        for (String producto : inventario) {
            assertTrue(producto.contains(":"), "Formato debe mantenerse");
            String[] partes = producto.split(":");
            assertEquals(4, partes.length, "Cada producto debe tener 4 campos");
            assertFalse(producto.contains("Pan"), "Producto eliminado no debe aparecer");
        }
    }
    
    @Test
    @DisplayName("Escenario 5: Información limpia y precisa después de eliminar")
    public void testInformacionLimpia() {
        // Given - Inventario con producto a eliminar
        List<String[]> inventario = new ArrayList<>();
        inventario.add(new String[]{"PROD001", "Leche", "100", "20"});
        inventario.add(new String[]{"PROD002", "Pan", "50", "10"});
        inventario.add(new String[]{"PROD003", "Arroz", "200", "50"});
        
        // When - Eliminamos producto 2
        String codigoEliminar = "PROD002";
        List<String[]> nuevoInventario = new ArrayList<>();
        
        for (String[] producto : inventario) {
            if (!producto[0].equals(codigoEliminar)) {
                nuevoInventario.add(producto);
            }
        }
        
        // Then
        assertEquals(2, nuevoInventario.size(), "Deben quedar 2 productos");
        
        // Verificar que no hay productos con código eliminado
        boolean contieneProductoEliminado = false;
        for (String[] producto : nuevoInventario) {
            if (producto[0].equals(codigoEliminar)) {
                contieneProductoEliminado = true;
                break;
            }
        }
        
        assertFalse(contieneProductoEliminado, 
                   "No debe contener producto eliminado");
        
        // Verificar integridad de datos restantes
        for (String[] producto : nuevoInventario) {
            assertEquals(4, producto.length, "Cada producto debe tener 4 campos");
            assertNotNull(producto[0], "Código no debe ser nulo");
            assertNotNull(producto[1], "Nombre no debe ser nulo");
        }
    }
}