package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.swing.*;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    // REEMPLAZA ESTA URI CON TU CONEXIÓN DE ATLAS
    private static final String CONNECTION_STRING = "mongodb+srv://christopher:1234@cluster0.caomfib.mongodb.net/?appName=Cluster0";
    private static final String DATABASE_NAME = "minimarket_db";
    
    public static void connect() {
        try {
            System.out.println("🔄 Conectando a MongoDB Atlas...");
            
            // Conexión a MongoDB Atlas
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
            
            // Test de conexión
            database.runCommand(new Document("ping", 1));
            
            System.out.println("✅ Conexión a MongoDB Atlas establecida correctamente");
            System.out.println("📊 Base de datos: " + DATABASE_NAME);
            
            // Crear colecciones si no existen
            createCollections();
            // Insertar datos iniciales
            initializeData();
            
        } catch (Exception e) {
            System.err.println("❌ Error al conectar con MongoDB Atlas: " + e.getMessage());
            
            // Mostrar mensaje amigable
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                    "No se pudo conectar a la base de datos.\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Verifique:\n" +
                    "1. Su conexión a internet\n" +
                    "2. La URI de MongoDB Atlas\n" +
                    "3. Que el cluster esté activo\n\n" +
                    "Usando base de datos local temporal...",
                    "Error de Conexión", 
                    JOptionPane.WARNING_MESSAGE);
            });
        }
    }
    
    private static void createCollections() {
        try {
            // Lista de colecciones necesarias
            String[] colecciones = {"usuarios", "productos", "categorias", "ventas"};
            
            for (String coleccion : colecciones) {
                if (!collectionExists(coleccion)) {
                    database.createCollection(coleccion);
                    System.out.println("✅ Colección creada: " + coleccion);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Error al crear colecciones: " + e.getMessage());
        }
    }
    
    private static void initializeData() {
        try {
            // Insertar usuarios por defecto
            MongoCollection<Document> usuariosCollection = database.getCollection("usuarios");
            
            if (usuariosCollection.countDocuments() == 0) {
                System.out.println("📝 Insertando usuarios por defecto...");
                
                Document admin = new Document("username", "admin")
                    .append("password", "admin123")
                    .append("rol", "SUPERUSUARIO")
                    .append("permisosEspeciales", java.util.Arrays.asList(
                        "ACCESO_TOTAL", "GESTIONAR_USUARIOS", "GESTIONAR_PRODUCTOS",
                        "VER_REPORTES", "EXPORTAR_DATOS", "CONFIGURAR_SISTEMA"
                    ));
                
                Document gerente = new Document("username", "gerente")
                    .append("password", "gerente123")
                    .append("rol", "GERENTE")
                    .append("permisosEspeciales", java.util.Arrays.asList(
                        "VER_REPORTES", "EXPORTAR_DATOS", "GESTIONAR_PRODUCTOS",
                        "ACCEDER_GESTION_PRODUCTOS", "ACCEDER_CONTROL_VENTAS",
                        "ACCEDER_REPORTES", "ACCEDER_DASHBOARD"
                    ));
                
                Document bodeguero = new Document("username", "bodeguero")
                    .append("password", "bodega123")
                    .append("rol", "BODEGUERO")
                    .append("permisosEspeciales", java.util.Arrays.asList(
                        "AGREGAR_PRODUCTOS", "EDITAR_PRODUCTOS", "ELIMINAR_PRODUCTOS",
                        "MODIFICAR_PRECIOS", "AJUSTAR_STOCK", "GESTIONAR_CATEGORIAS"
                    ));
                
                Document cajero = new Document("username", "cajero")
                    .append("password", "caja123")
                    .append("rol", "CAJERO")
                    .append("permisosEspeciales", java.util.Arrays.asList(
                        "PROCESAR_VENTAS", "GESTIONAR_CARRITO", "VER_REPORTES_BASICOS",
                        "AGREGAR_CARRITO", "QUITAR_CARRITO", "LIMPIAR_CARRITO", "REALIZAR_VENTA"
                    ));
                
                usuariosCollection.insertMany(java.util.Arrays.asList(admin, gerente, bodeguero, cajero));
                System.out.println("✅ Usuarios por defecto creados");
            }
            
            // Insertar categorías por defecto
            MongoCollection<Document> categoriasCollection = database.getCollection("categorias");
            if (categoriasCollection.countDocuments() == 0) {
                System.out.println("📝 Insertando categorías por defecto...");
                
                String[] categorias = {"Bebidas", "Lácteos", "Carnes", "Frutas", "Verduras", "Limpieza", "Abarrotes"};
                for (String categoria : categorias) {
                    categoriasCollection.insertOne(new Document("nombre", categoria));
                }
                System.out.println("✅ Categorías por defecto creadas");
            }
            
        } catch (Exception e) {
            System.err.println("⚠ Error al inicializar datos: " + e.getMessage());
        }
    }
    
    private static boolean collectionExists(String collectionName) {
        try {
            for (String name : database.listCollectionNames()) {
                if (name.equals(collectionName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect();
        }
        return database;
    }
    
    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("🔒 Conexión a MongoDB cerrada");
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}