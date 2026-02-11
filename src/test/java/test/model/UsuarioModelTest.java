package test.model;

import model.Usuario;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Usuario (Modelo)")
public class UsuarioModelTest {
    
    @Test
    @DisplayName("Crear usuario con constructor básico")
    void testCrearUsuarioBasico() {
        // Arrange & Act
        Usuario usuario = new Usuario("testuser", "testpass", "CAJERO");
        
        // Assert
        assertEquals("testuser", usuario.getUsername());
        assertEquals("testpass", usuario.getPassword());
        assertEquals("CAJERO", usuario.getRol());
        assertNotNull(usuario.getPermisosEspeciales());
        assertTrue(usuario.getPermisosEspeciales().isEmpty());
    }
    
    @Test
    @DisplayName("Verificar métodos de rol")
    void testMetodosRol() {
        Usuario usuario = new Usuario();
        
        usuario.setRol(Usuario.ROL_SUPERUSUARIO);
        assertTrue(usuario.esSuperusuario());
        
        usuario.setRol(Usuario.ROL_GERENTE);
        assertTrue(usuario.esGerente());
        
        usuario.setRol(Usuario.ROL_BODEGUERO);
        assertTrue(usuario.esBodeguero());
        
        usuario.setRol(Usuario.ROL_CAJERO);
        assertTrue(usuario.esCajero());
    }
    
    @Test
    @DisplayName("Agregar y verificar permisos")
    void testPermisos() {
        Usuario usuario = new Usuario("test", "pass", "GERENTE");
        
        // Agregar permisos
        usuario.agregarPermiso("AGREGAR_PRODUCTOS");
        usuario.agregarPermiso("VER_REPORTES");
        
        // Verificar
        assertTrue(usuario.tienePermiso("AGREGAR_PRODUCTOS"));
        assertTrue(usuario.tienePermiso("VER_REPORTES"));
        assertFalse(usuario.tienePermiso("PERMISO_INEXISTENTE"));
    }
    
    @Test
    @DisplayName("Convertir a Document MongoDB")
    void testToDocument() {
        // Arrange
        Usuario usuario = new Usuario("admin", "admin123", Usuario.ROL_SUPERUSUARIO);
        usuario.setPermisosEspeciales(Arrays.asList("ACCESO_TOTAL", "GESTIONAR_USUARIOS"));
        
        // Act
        Document doc = usuario.toDocument();
        
        // Assert
        assertNotNull(doc);
        assertEquals("admin", doc.getString("username"));
        assertEquals("admin123", doc.getString("password"));
        assertEquals("SUPERUSUARIO", doc.getString("rol"));
        
        List<String> permisos = doc.getList("permisosEspeciales", String.class);
        assertNotNull(permisos);
        assertEquals(2, permisos.size());
        assertTrue(permisos.contains("ACCESO_TOTAL"));
    }
    
    @Test
    @DisplayName("Crear desde Document MongoDB")
    void testFromDocument() {
        // Arrange
        Document doc = new Document("username", "gerente")
                .append("password", "gerente123")
                .append("rol", "GERENTE")
                .append("permisosEspeciales", Arrays.asList("VER_REPORTES", "EXPORTAR_DATOS"));
        
        // Act
        Usuario usuario = Usuario.fromDocument(doc);
        
        // Assert
        assertNotNull(usuario);
        assertEquals("gerente", usuario.getUsername());
        assertEquals("gerente123", usuario.getPassword());
        assertEquals("GERENTE", usuario.getRol());
        assertEquals(2, usuario.getPermisosEspeciales().size());
        assertTrue(usuario.getPermisosEspeciales().contains("VER_REPORTES"));
    }
    
    @Test
    @DisplayName("Métodos de validación de permisos")
    void testMetodosValidacionPermisos() {
        Usuario usuario = new Usuario("test", "pass", "BODEGUERO");
        usuario.setPermisosEspeciales(Arrays.asList("AGREGAR_PRODUCTOS", "EDITAR_PRODUCTOS"));
        
        assertTrue(usuario.puedeAgregarProductos());
        assertTrue(usuario.puedeEditarProductos());
        assertFalse(usuario.puedeEliminarProductos()); // No tiene este permiso
    }
    
    @Test
    @DisplayName("Acceso total para superusuario")
    void testAccesoTotalSuperusuario() {
        Usuario superusuario = new Usuario("admin", "pass", Usuario.ROL_SUPERUSUARIO);
        
        assertTrue(superusuario.tieneAccesoTotal());
        assertTrue(superusuario.esSuperusuario());
    }
}