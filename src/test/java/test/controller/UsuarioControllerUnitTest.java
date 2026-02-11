package test.controller;

import controller.UsuarioController;
import model.Usuario;
import repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para UsuarioController")
public class UsuarioControllerUnitTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    private UsuarioController usuarioController;
    
    @BeforeEach
    void setUp() {
        usuarioController = new UsuarioController(usuarioRepository);
    }
    
    @Test
    @DisplayName("Autenticar usuario válido")
    void testAutenticarUsuarioValido() {
        // Arrange
        Usuario usuarioMock = new Usuario("admin", "admin123", "SUPERUSUARIO");
        
        when(usuarioRepository.autenticar("admin", "admin123")).thenReturn(usuarioMock);
        
        // Act
        Usuario resultado = usuarioController.autenticar("admin", "admin123");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
        assertEquals("SUPERUSUARIO", resultado.getRol());
        verify(usuarioRepository, times(1)).autenticar("admin", "admin123");
    }
    
    @Test
    @DisplayName("Autenticar con usuario vacío")
    void testAutenticarUsuarioVacio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.autenticar("", "password123")
        );
        assertEquals("El usuario es obligatorio", exception.getMessage());
        
        verify(usuarioRepository, never()).autenticar(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Autenticar con contraseña vacía")
    void testAutenticarPasswordVacia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.autenticar("admin", "")
        );
        assertEquals("La contraseña es obligatoria", exception.getMessage());
        
        verify(usuarioRepository, never()).autenticar(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Autenticar con credenciales incorrectas")
    void testAutenticarCredencialesIncorrectas() {
        // Arrange
        when(usuarioRepository.autenticar("admin", "wrongpass")).thenReturn(null);
        
        // Act & Assert
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> usuarioController.autenticar("admin", "wrongpass")
        );
        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
        
        verify(usuarioRepository, times(1)).autenticar("admin", "wrongpass");
    }
    
    @Test
    @DisplayName("Buscar usuario existente")
    void testBuscarUsuarioExistente() {
        // Arrange
        Usuario usuarioMock = new Usuario("gerente", "gerente123", "GERENTE");
        
        when(usuarioRepository.buscarPorUsername("gerente")).thenReturn(usuarioMock);
        
        // Act
        Usuario resultado = usuarioController.buscarUsuario("gerente");
        
        // Assert
        assertNotNull(resultado);
        assertEquals("gerente", resultado.getUsername());
        assertEquals("GERENTE", resultado.getRol());
        verify(usuarioRepository, times(1)).buscarPorUsername("gerente");
    }
    
    @Test
    @DisplayName("Buscar usuario no existente")
    void testBuscarUsuarioNoExistente() {
        // Arrange
        when(usuarioRepository.buscarPorUsername("inexistente")).thenReturn(null);
        
        // Act
        Usuario resultado = usuarioController.buscarUsuario("inexistente");
        
        // Assert
        assertNull(resultado);
        verify(usuarioRepository, times(1)).buscarPorUsername("inexistente");
    }
    
    @Test
    @DisplayName("Autenticar usuario nulo")
    void testAutenticarUsuarioNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.autenticar(null, "password123")
        );
        assertEquals("El usuario es obligatorio", exception.getMessage());
        
        verify(usuarioRepository, never()).autenticar(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Autenticar contraseña nula")
    void testAutenticarPasswordNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioController.autenticar("admin", null)
        );
        assertEquals("La contraseña es obligatoria", exception.getMessage());
        
        verify(usuarioRepository, never()).autenticar(anyString(), anyString());
    }
}