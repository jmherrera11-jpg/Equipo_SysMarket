package repository;

import model.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    private MongoCollection<Document> usuariosCollection;
    
    public UsuarioRepository(MongoDatabase database) {
        this.usuariosCollection = database.getCollection("usuarios");
    }
    
    public Usuario autenticar(String username, String password) {
        try {
            Bson filter = Filters.and(
                Filters.eq("username", username),
                Filters.eq("password", password)
            );
            Document doc = usuariosCollection.find(filter).first();
            return (doc != null) ? Usuario.fromDocument(doc) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación: " + e.getMessage(), e);
        }
    }
    
    public Usuario buscarPorUsername(String username) {
        try {
            Bson filter = Filters.eq("username", username);
            Document doc = usuariosCollection.find(filter).first();
            return (doc != null) ? Usuario.fromDocument(doc) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }
    
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            for (Document doc : usuariosCollection.find()) {
                usuarios.add(Usuario.fromDocument(doc));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }
    
    public void agregarUsuario(Usuario usuario) {
        try {
            // Verificar que el usuario no exista
            if (buscarPorUsername(usuario.getUsername()) != null) {
                throw new IllegalArgumentException("El usuario ya existe: " + usuario.getUsername());
            }
            usuariosCollection.insertOne(usuario.toDocument());
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar usuario: " + e.getMessage(), e);
        }
    }
    
    public void actualizarUsuario(String username, Usuario usuario) {
        try {
            Bson filter = Filters.eq("username", username);
            Document updateDoc = new Document("$set", usuario.toDocument());
            usuariosCollection.updateOne(filter, updateDoc);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }
    
    public void eliminarUsuario(String username) {
        try {
            // No permitir eliminar al superusuario por defecto
            if ("admin".equals(username)) {
                throw new IllegalArgumentException("No se puede eliminar al superusuario principal");
            }
            Bson filter = Filters.eq("username", username);
            usuariosCollection.deleteOne(filter);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }
    
    public boolean existeUsuario(String username) {
        try {
            Bson filter = Filters.eq("username", username);
            return usuariosCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar usuario: " + e.getMessage(), e);
        }
    }
}
