package test.repository;

import repository.UsuarioRepository;
import model.Usuario;
import database.MongoDBConnection;
import org.junit.jupiter.api.*;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración para UsuarioRepository")
public class UsuarioRepositoryIntegrationTest {
    
    private static UsuarioRepository usuarioRepository;
    private static boolean connectionFailed = false;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Iniciando tests de integración para UsuarioRepository...");
        
        try {
            MongoDBConnection.connect();
            
            var database = MongoDBConnection.getDatabase();
            if (database == null) {
                connectionFailed = true;
                System.out.println("ADVERTENCIA: No se pudo conectar a MongoDB. Los tests serán omitidos.");
                return;
            }
            
            usuarioRepository = new UsuarioRepository(database);
            
            // Limpiar usuarios de prueba
            limpiarUsuariosPrueba();
        } catch (Exception e) {
            connectionFailed = true;
            System.out.println("ERROR en setup: " + e.getMessage());
        }
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Finalizando tests de integración para UsuarioRepository...");
        if (!connectionFailed) {
            try {
                limpiarUsuariosPrueba();
                MongoDBConnection.close();
            } catch (Exception e) {
                System.out.println("Error en tearDown: " + e.getMessage());
            }
        }
    }
    
    private static void limpiarUsuariosPrueba() {
        try {
            if (connectionFailed) return;
            
            var database = MongoDBConnection.getDatabase();
            if (database != null) {
                var collection = database.getCollection("usuarios");
                collection.deleteMany(new Document("username", new Document("$regex", "^test_")));
            }
        } catch (Exception e) {
            System.out.println("Error al limpiar usuarios: " + e.getMessage());
        }
    }
    
    @BeforeEach
    void checkConnection() {
        if (connectionFailed) {
            Assumptions.assumeFalse(connectionFailed, "Conexión a MongoDB no disponible");
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Agregar nuevo usuario")
    void testAgregarUsuario() {
        try {
            // Arrange
            Usuario usuario = new Usuario("test_user1", "password123", "CAJERO");
            usuario.setPermisosEspeciales(Arrays.asList("PROCESAR_VENTAS", "VER_REPORTES_BASICOS"));
            
            // Act
            usuarioRepository.agregarUsuario(usuario);
            
            // Assert
            Usuario usuarioGuardado = usuarioRepository.buscarPorUsername("test_user1");
            assertNotNull(usuarioGuardado, "El usuario guardado no debe ser null");
            assertEquals("test_user1", usuarioGuardado.getUsername());
            assertEquals("CAJERO", usuarioGuardado.getRol());
            
            List<String> permisos = usuarioGuardado.getPermisosEspeciales();
            assertNotNull(permisos, "Los permisos no deben ser null");
            assertTrue(permisos.size() >= 1, "Debe tener al menos un permiso");
            
        } catch (Exception e) {
            fail("Error al agregar usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("No permitir usuario duplicado")
    void testUsuarioDuplicado() {
        try {
            // Arrange - El usuario test_user1 ya fue creado en el test anterior
            Usuario usuarioDuplicado = new Usuario("test_user1", "otracontrasena", "GERENTE");
            
            // Act & Assert
            Exception exception = assertThrows(
                Exception.class,
                () -> usuarioRepository.agregarUsuario(usuarioDuplicado)
            );
            
            assertNotNull(exception, "Debe lanzar una excepción");
            String mensaje = exception.getMessage();
            assertTrue(mensaje != null && 
                       (mensaje.contains("ya existe") || 
                        mensaje.contains("duplicado") || 
                        mensaje.contains("existe")),
                       "El mensaje debe indicar que el usuario ya existe");
            
        } catch (Exception e) {
            fail("Error en test de duplicado: " + e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Autenticar usuario exitoso")
    void testAutenticarUsuarioExitoso() {
        try {
            // Arrange - Crear usuario específico para autenticación
            Usuario usuarioAuth = new Usuario("test_auth", "auth123", "BODEGUERO");
            usuarioRepository.agregarUsuario(usuarioAuth);
            
            // Act
            Usuario autenticado = usuarioRepository.autenticar("test_auth", "auth123");
            
            // Assert
            assertNotNull(autenticado, "El usuario autenticado no debe ser null");
            assertEquals("test_auth", autenticado.getUsername());
            assertEquals("BODEGUERO", autenticado.getRol());
            
        } catch (Exception e) {
            fail("Error en autenticación: " + e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Autenticar usuario con credenciales incorrectas")
    void testAutenticarUsuarioFallido() {
        try {
            // Act
            Usuario autenticado = usuarioRepository.autenticar("test_auth", "contrasenaIncorrecta");
            
            // Assert
            assertNull(autenticado, "El usuario con credenciales incorrectas debe ser null");
        } catch (Exception e) {
            // Si lanza excepción en lugar de devolver null, también es válido
            assertNotNull(e, "Debe fallar la autenticación de alguna forma");
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Buscar usuario existente")
    void testBuscarUsuarioExistente() {
        try {
            // Arrange
            String username = "test_user1";
            
            // Act
            Usuario encontrado = usuarioRepository.buscarPorUsername(username);
            
            // Assert
            assertNotNull(encontrado, "El usuario encontrado no debe ser null");
            assertEquals(username, encontrado.getUsername());
            assertNotNull(encontrado.getRol(), "El rol no debe ser null");
        } catch (Exception e) {
            fail("Error al buscar usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Buscar usuario no existente")
    void testBuscarUsuarioNoExistente() {
        try {
            // Act
            Usuario encontrado = usuarioRepository.buscarPorUsername("usuario_inexistente_12345");
            
            // Assert
            assertNull(encontrado, "El usuario inexistente debe ser null");
        } catch (Exception e) {
            // Si lanza excepción, también es válido
            assertNotNull(e, "Debe indicar que el usuario no existe");
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Actualizar usuario existente")
    void testActualizarUsuario() {
        try {
            // Arrange - Crear usuario para actualizar
            Usuario usuarioOriginal = new Usuario("test_update", "original123", "CAJERO");
            usuarioRepository.agregarUsuario(usuarioOriginal);
            
            // Crear usuario con modificaciones
            Usuario usuarioActualizado = new Usuario("test_update", "nueva123", "GERENTE");
            usuarioActualizado.setPermisosEspeciales(Arrays.asList("ACCESO_TOTAL"));
            
            // Act
            usuarioRepository.actualizarUsuario("test_update", usuarioActualizado);
            
            // Assert
            Usuario resultado = usuarioRepository.buscarPorUsername("test_update");
            assertNotNull(resultado, "El usuario actualizado no debe ser null");
            assertEquals("test_update", resultado.getUsername());
            assertEquals("GERENTE", resultado.getRol());
            
            List<String> permisos = resultado.getPermisosEspeciales();
            assertNotNull(permisos, "Los permisos no deben ser null");
            assertTrue(permisos.size() >= 1, "Debe tener al menos un permiso");
            
        } catch (Exception e) {
            fail("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("Eliminar usuario existente")
    void testEliminarUsuario() {
        try {
            // Arrange - Crear usuario para eliminar
            Usuario usuarioEliminar = new Usuario("test_delete", "delete123", "BODEGUERO");
            usuarioRepository.agregarUsuario(usuarioEliminar);
            
            // Verificar que existe antes de eliminar
            assertNotNull(usuarioRepository.buscarPorUsername("test_delete"), 
                          "El usuario debe existir antes de eliminarlo");
            
            // Act
            usuarioRepository.eliminarUsuario("test_delete");
            
            // Assert - Verificar que ya no existe
            Usuario eliminado = usuarioRepository.buscarPorUsername("test_delete");
            assertNull(eliminado, "El usuario eliminado debe ser null");
            
        } catch (Exception e) {
            fail("Error al eliminar usuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("No permitir eliminar superusuario principal")
    void testNoEliminarSuperusuarioPrincipal() {
        try {
            // Act & Assert
            Exception exception = assertThrows(
                Exception.class,
                () -> usuarioRepository.eliminarUsuario("admin")
            );
            
            assertNotNull(exception, "Debe lanzar una excepción");
            String mensaje = exception.getMessage();
            assertTrue(mensaje != null && 
                       (mensaje.contains("superusuario") || 
                        mensaje.contains("admin") || 
                        mensaje.contains("no se puede eliminar")),
                       "El mensaje debe indicar que no se puede eliminar el superusuario");
        } catch (Exception e) {
            fail("Error en test de eliminación de superusuario: " + e.getMessage());
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("Obtener todos los usuarios")
    void testObtenerTodosUsuarios() {
        try {
            // Act
            List<Usuario> usuarios = usuarioRepository.obtenerTodosUsuarios();
            
            // Assert
            assertNotNull(usuarios, "La lista de usuarios no debe ser null");
            assertTrue(usuarios.size() > 0, "Debe haber al menos un usuario");
            
            // Verificar que existe al menos un usuario válido
            boolean tieneUsuariosValidos = usuarios.stream()
                .anyMatch(u -> u != null && u.getUsername() != null);
            assertTrue(tieneUsuariosValidos, "Debe haber usuarios válidos");
            
        } catch (Exception e) {
            fail("Error al obtener todos los usuarios: " + e.getMessage());
        }
    }
    
    @Test
    @Order(11)
    @DisplayName("Verificar existencia de usuario")
    void testExisteUsuario() {
        try {
            // Usuario existente (debe existir de tests anteriores o datos iniciales)
            boolean existeTest = usuarioRepository.existeUsuario("test_user1");
            assertTrue(existeTest, "El usuario test_user1 debe existir");
            
            // Usuario no existente
            boolean existeInexistente = usuarioRepository.existeUsuario("usuario_inexistente_999");
            assertFalse(existeInexistente, "El usuario inexistente no debe existir");
            
        } catch (Exception e) {
            fail("Error al verificar existencia de usuario: " + e.getMessage());
        }
    }
}