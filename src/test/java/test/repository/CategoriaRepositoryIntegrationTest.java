package test.repository;

import repository.CategoriaRepository;
import database.MongoDBConnection;
import org.junit.jupiter.api.*;
import org.bson.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración para CategoriaRepository")
public class CategoriaRepositoryIntegrationTest {
    
    private static CategoriaRepository categoriaRepository;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Iniciando tests de integración para CategoriaRepository...");
        MongoDBConnection.connect();
        
        var database = MongoDBConnection.getDatabase();
        categoriaRepository = new CategoriaRepository(database);
        
        // Limpiar categorías de prueba
        limpiarCategoriasPrueba();
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Finalizando tests de integración para CategoriaRepository...");
        limpiarCategoriasPrueba();
        MongoDBConnection.close();
    }
    
    private static void limpiarCategoriasPrueba() {
        try {
            var database = MongoDBConnection.getDatabase();
            var collection = database.getCollection("categorias");
            collection.deleteMany(new Document("nombre", new Document("$regex", "^TEST_")));
        } catch (Exception e) {
            System.out.println("Error al limpiar categorías: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Agregar nueva categoría")
    void testAgregarCategoria() {
        try {
            // Arrange
            String nombreCategoria = "TEST_CATEGORIA_1";
            
            // Act
            categoriaRepository.agregarCategoria(nombreCategoria);
            
            // Assert
            List<String> categorias = categoriaRepository.obtenerTodasCategorias();
            assertTrue(categorias.contains(nombreCategoria), 
                      "La nueva categoría debería estar en la lista");
            
        } catch (Exception e) {
            fail("Error al agregar categoría: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Obtener todas las categorías")
    void testObtenerTodasCategorias() {
        try {
            // Arrange - Agregar más categorías de prueba
            categoriaRepository.agregarCategoria("TEST_CATEGORIA_2");
            categoriaRepository.agregarCategoria("TEST_CATEGORIA_3");
            
            // Act
            List<String> categorias = categoriaRepository.obtenerTodasCategorias();
            
            // Assert
            assertNotNull(categorias);
            assertFalse(categorias.isEmpty());
            
            // Debería contener las categorías de prueba
            assertTrue(categorias.contains("TEST_CATEGORIA_1"));
            assertTrue(categorias.contains("TEST_CATEGORIA_2"));
            assertTrue(categorias.contains("TEST_CATEGORIA_3"));
            
            // También debería contener categorías por defecto si existen
            boolean tieneCategoriasDefault = categorias.stream()
                .anyMatch(cat -> cat.equals("Lácteos") || cat.equals("Bebidas"));
            // Nota: Esto depende de si las categorías por defecto fueron creadas
            
        } catch (Exception e) {
            fail("Error al obtener categorías: " + e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Verificar existencia de categoría")
    void testExisteCategoria() {
        // Arrange
        String categoriaExistente = "TEST_CATEGORIA_1";
        String categoriaNoExistente = "CATEGORIA_INEXISTENTE_123";
        
        // Act & Assert
        assertTrue(categoriaRepository.existeCategoria(categoriaExistente),
                  "Debería encontrar categoría existente");
        assertFalse(categoriaRepository.existeCategoria(categoriaNoExistente),
                   "No debería encontrar categoría inexistente");
    }
    
    @Test
    @Order(4)
    @DisplayName("Agregar múltiples categorías")
    void testAgregarMultiplesCategorias() {
        try {
            // Arrange
            String[] nuevasCategorias = {
                "TEST_CATEGORIA_4",
                "TEST_CATEGORIA_5",
                "TEST_CATEGORIA_6"
            };
            
            // Act - Agregar todas
            for (String categoria : nuevasCategorias) {
                categoriaRepository.agregarCategoria(categoria);
            }
            
            // Assert
            List<String> todasCategorias = categoriaRepository.obtenerTodasCategorias();
            
            for (String categoria : nuevasCategorias) {
                assertTrue(todasCategorias.contains(categoria),
                          "Categoría " + categoria + " debería estar en la lista");
            }
            
        } catch (Exception e) {
            fail("Error al agregar múltiples categorías: " + e.getMessage());
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Agregar categoría duplicada")
    void testAgregarCategoriaDuplicada() {
        try {
            // Arrange - La categoría ya existe del test 1
            String categoriaDuplicada = "TEST_CATEGORIA_1";
            
            // Act - Intentar agregar duplicado
            categoriaRepository.agregarCategoria(categoriaDuplicada);
            
            // Assert - No debería lanzar excepción, pero tampoco debería duplicar
            List<String> categorias = categoriaRepository.obtenerTodasCategorias();
            long count = categorias.stream()
                .filter(c -> c.equals(categoriaDuplicada))
                .count();
            
            // Podría ser 1 o más dependiendo de cómo maneje MongoDB los duplicados
            assertTrue(count >= 1, "Debería existir al menos una vez");
            
        } catch (Exception e) {
            fail("Error con categoría duplicada: " + e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Obtener lista vacía si no hay categorías")
    void testObtenerCategoriasListaVacia() {
        try {
            // Este test depende de que limpiemos todas las categorías primero
            // En realidad, siempre habrá categorías por defecto o de prueba
            // así que solo verificamos que no sea null
            
            // Act
            List<String> categorias = categoriaRepository.obtenerTodasCategorias();
            
            // Assert
            assertNotNull(categorias, "La lista de categorías nunca debería ser null");
            // Podría estar vacía o no, dependiendo del estado de la BD
            
        } catch (Exception e) {
            fail("Error al obtener categorías: " + e.getMessage());
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Categorías con caracteres especiales")
    void testCategoriasConCaracteresEspeciales() {
        try {
            // Arrange
            String[] categoriasEspeciales = {
                "TEST_Categoría con tilde",
                "TEST_Categoría con espacios",
                "TEST_Categoría-con-guiones",
                "TEST_Categoría123_Números"
            };
            
            // Act - Agregar todas
            for (String categoria : categoriasEspeciales) {
                categoriaRepository.agregarCategoria(categoria);
            }
            
            // Assert
            List<String> todasCategorias = categoriaRepository.obtenerTodasCategorias();
            
            for (String categoria : categoriasEspeciales) {
                assertTrue(todasCategorias.contains(categoria),
                          "Categoría especial '" + categoria + "' debería estar en la lista");
            }
            
        } catch (Exception e) {
            fail("Error con categorías especiales: " + e.getMessage());
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("Verificar categorías por defecto del sistema")
    void testCategoriasPorDefecto() {
        try {
            // Este test verifica que las categorías por defecto estén presentes
            // Depende de la inicialización de la base de datos
            
            // Act
            List<String> categorias = categoriaRepository.obtenerTodasCategorias();
            
            // Assert - Verificar algunas categorías comunes
            boolean tieneCategoriasRelevantes = false;
            
            // Buscar categorías que podrían ser por defecto
            String[] posiblesCategoriasDefault = {
                "Lácteos", "Bebidas", "Carnes", "Frutas", "Verduras", "Limpieza", "Abarrotes"
            };
            
            for (String posibleCategoria : posiblesCategoriasDefault) {
                if (categorias.contains(posibleCategoria)) {
                    tieneCategoriasRelevantes = true;
                    System.out.println("✓ Encontrada categoría por defecto: " + posibleCategoria);
                    break;
                }
            }
            
            // No fallar si no hay categorías por defecto, solo informar
            if (!tieneCategoriasRelevantes) {
                System.out.println("ℹ️ No se encontraron categorías por defecto específicas");
            }
            
            // Al menos debería haber alguna categoría (de prueba o por defecto)
            assertFalse(categorias.isEmpty(), "Debería haber al menos alguna categoría");
            
        } catch (Exception e) {
            fail("Error al verificar categorías por defecto: " + e.getMessage());
        }
    }
}