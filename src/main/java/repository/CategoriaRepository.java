package repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {
    private MongoCollection<Document> categoriasCollection;
    
    public CategoriaRepository(MongoDatabase database) {
        this.categoriasCollection = database.getCollection("categorias");
    }
    
    public List<String> obtenerTodasCategorias() {
        List<String> categorias = new ArrayList<>();
        try {
            for (Document doc : categoriasCollection.find()) {
                categorias.add(doc.getString("nombre"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías: " + e.getMessage(), e);
        }
        return categorias;
    }
    
    public void agregarCategoria(String nombre) {
        try {
            Document categoria = new Document("nombre", nombre);
            categoriasCollection.insertOne(categoria);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar categoría: " + e.getMessage(), e);
        }
    }
    
    public boolean existeCategoria(String nombre) {
        try {
            Document filter = new Document("nombre", nombre);
            return categoriasCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar categoría: " + e.getMessage(), e);
        }
    }
}