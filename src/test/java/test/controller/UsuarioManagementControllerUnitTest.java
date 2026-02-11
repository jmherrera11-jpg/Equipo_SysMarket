package test.controller;

import controller.UsuarioManagementController;
import model.Usuario;
import repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para UsuarioManagementController")
public class UsuarioManagementControllerUnitTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    private UsuarioManagementController usuarioController;
    
    @BeforeEach
    void setUp() {
        usuarioController = new UsuarioManagementController(usuarioRepository);
    }
    
    @Test
    @DisplayName("Superusuario puede agregar usuario válido")
    void testSuperusuarioAgregarUsuarioValido() {
        // Arrange
        Usuario superusuario = crearSuperusuario();
        Usuario nuevoUsuario = new Usuario("nuevo", "password123", "CAJERO");
        
        // Act
        assertDoesNotThrow(() -> usuarioController.agregarUsuario(nuevoUsuario, superusuario));
        
        // Assert
        verify(usuarioRepository, times(1)).agregarUsuario(nuevoUsuario);
    }
    
    @Test
    @DisplayName("No superusuario no puede agregar usuarios")
    void testNoSuperusuarioNoPuedeAgregar() {
        // Arrange
        Usuario gerente = new Usuario("gerente", "pass", "GERENTE");
        Usuario nuevoUsuario = new Usuario("nuevo", "pass", "CAJERO");
        
        // Act & Assert
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> usuarioController.agregarUsuario(nuevoUsuario, gerente)
        );
        assertEquals("Solo los superusuarios pueden agregar usuarios", exception.getMessage());
        
        verify(usuarioRepository, never()).agregarUsuario(any());
    }
    
    @Test
    @DisplayName("Validar campos obligatorios al agregar usuario")
    void testValidarCamposObligatorios() {
        Usuario superusuario = crearSuperusuario();
        
        // Usuario sin username
        Usuario usuarioSinUsername = new Usuario("", "pass", "CAJERO");
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.agregarUsuario(usuarioSinUsername, superusuario)
        );
        assertEquals("El nombre de usuario es obligatorio", exception1.getMessage());
        
        // Usuario sin password
        Usuario usuarioSinPassword = new Usuario("usuario", "", "CAJERO");
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.agregarUsuario(usuarioSinPassword, superusuario)
        );
        assertEquals("La contraseña es obligatoria", exception2.getMessage());
        
        // Usuario sin rol
        Usuario usuarioSinRol = new Usuario("usuario", "pass", "");
        IllegalArgumentException exception3 = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.agregarUsuario(usuarioSinRol, superusuario)
        );
        assertEquals("El rol es obligatorio", exception3.getMessage());
        
        verify(usuarioRepository, never()).agregarUsuario(any());
    }
    
    @Test
    @DisplayName("Validar rol inválido")
    void testValidarRolInvalido() {
        Usuario superusuario = crearSuperusuario();
        Usuario usuarioRolInvalido = new Usuario("usuario", "pass", "ROL_INVALIDO");
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.agregarUsuario(usuarioRolInvalido, superusuario)
        );
        assertTrue(exception.getMessage().contains("Rol no válido"));
        
        verify(usuarioRepository, never()).agregarUsuario(any());
    }
    
    @Test
    @DisplayName("Roles válidos")
    void testRolesValidos() {
        Usuario superusuario = crearSuperusuario();
        
        String[] rolesValidos = {"SUPERUSUARIO", "GERENTE", "BODEGUERO", "CAJERO"};
        
        for (String rol : rolesValidos) {
            Usuario usuario = new Usuario("user_" + rol, "pass", rol);
            assertDoesNotThrow(() -> usuarioController.agregarUsuario(usuario, superusuario));
        }
        
        verify(usuarioRepository, times(rolesValidos.length)).agregarUsuario(any());
    }
    
    @Test
    @DisplayName("Superusuario puede eliminar usuario")
    void testSuperusuarioEliminarUsuario() {
        // Arrange
        Usuario superusuario = crearSuperusuario();
        
        // Act
        assertDoesNotThrow(() -> usuarioController.eliminarUsuario("usuarioAEliminar", superusuario));
        
        // Assert
        verify(usuarioRepository, times(1)).eliminarUsuario("usuarioAEliminar");
        // NO se verifica buscarPorUsername porque el método eliminarUsuario no lo llama
    }
    
    @Test
    @DisplayName("No puede eliminarse a sí mismo")
    void testNoPuedeEliminarseASiMismo() {
        Usuario superusuario = crearSuperusuario();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.eliminarUsuario("admin", superusuario)
        );
        assertEquals("No puede eliminarse a sí mismo", exception.getMessage());
        
        verify(usuarioRepository, never()).eliminarUsuario(anyString());
    }
    
    @Test
    @DisplayName("Verificar si usuario puede gestionar usuarios")
    void testPuedeGestionarUsuarios() {
        // Creamos un mock de Usuario para probar el método puedeGestionarUsuarios
        // Primero necesitamos que el método esSuperusuario() funcione
        
        // Para esta prueba, asumimos que el método esSuperusuario() existe
        // Si no existe en tu clase Usuario, necesitas agregarlo:
        // public boolean esSuperusuario() { return "SUPERUSUARIO".equals(this.rol); }
        
        Usuario superusuario = new Usuario("admin", "pass", "SUPERUSUARIO");
        Usuario gerente = new Usuario("gerente", "pass", "GERENTE");
        
        // El método puedeGestionarUsuarios debe usar usuario.esSuperusuario()
        // Si tu controlador usa otra lógica, ajusta esta prueba
        assertTrue(usuarioController.puedeGestionarUsuarios(superusuario));
        assertFalse(usuarioController.puedeGestionarUsuarios(gerente));
        assertFalse(usuarioController.puedeGestionarUsuarios(null));
    }
    
    @Test
    @DisplayName("Obtener todos los usuarios")
    void testObtenerTodosUsuarios() {
        // Arrange
        Usuario u1 = new Usuario("admin", "pass", "SUPERUSUARIO");
        Usuario u2 = new Usuario("gerente", "pass", "GERENTE");
        List<Usuario> usuariosMock = Arrays.asList(u1, u2);
        
        when(usuarioRepository.obtenerTodosUsuarios()).thenReturn(usuariosMock);
        
        // Act
        List<Usuario> resultado = usuarioController.obtenerTodosUsuarios();
        
        // Assert
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).obtenerTodosUsuarios();
    }
    
    @Test
    @DisplayName("Agregar usuario con permisos especiales")
    void testAgregarUsuarioConPermisos() {
        // Arrange
        Usuario superusuario = crearSuperusuario();
        Usuario nuevoUsuario = new Usuario("nuevo", "pass", "CAJERO");
        List<String> permisos = Arrays.asList("PROCESAR_VENTAS", "VER_REPORTES_BASICOS");
        
        // Act
        assertDoesNotThrow(() -> 
            usuarioController.agregarUsuarioConPermisos(nuevoUsuario, permisos, superusuario)
        );
        
        // Assert
        verify(usuarioRepository, times(1)).agregarUsuario(nuevoUsuario);
    }
    
    @Test
    @DisplayName("Actualizar permisos de usuario")
    void testActualizarPermisosUsuario() {
        // Arrange
        Usuario superusuario = crearSuperusuario();
        Usuario usuarioExistente = new Usuario("usuario", "pass", "CAJERO");
        List<String> permisos = Arrays.asList("NUEVO_PERMISO");
        
        when(usuarioRepository.buscarPorUsername("usuario")).thenReturn(usuarioExistente);
        
        // Act
        assertDoesNotThrow(() -> 
            usuarioController.actualizarPermisosUsuario("usuario", permisos, superusuario)
        );
        
        // Assert
        verify(usuarioRepository, times(1)).buscarPorUsername("usuario");
        verify(usuarioRepository, times(1)).actualizarUsuario(eq("usuario"), any(Usuario.class));
    }
    
    @Test
    @DisplayName("No superusuario no puede actualizar permisos")
    void testNoSuperusuarioNoPuedeActualizarPermisos() {
        // Arrange
        Usuario gerente = new Usuario("gerente", "pass", "GERENTE");
        List<String> permisos = Arrays.asList("NUEVO_PERMISO");
        
        // Act & Assert
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> usuarioController.actualizarPermisosUsuario("usuario", permisos, gerente)
        );
        assertEquals("Solo los superusuarios pueden actualizar permisos", exception.getMessage());
        
        verify(usuarioRepository, never()).buscarPorUsername(anyString());
        verify(usuarioRepository, never()).actualizarUsuario(anyString(), any());
    }
    
    // Método helper para crear superusuario
    private Usuario crearSuperusuario() {
        // Si tu clase Usuario tiene constantes para roles, úsalas
        // Si no, usa strings directamente
        return new Usuario("admin", "pass", "SUPERUSUARIO");
    }
}