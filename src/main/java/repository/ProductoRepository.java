package repository;

import model.Producto;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {
    private MongoCollection<Document> productosCollection;
    
    public ProductoRepository(MongoDatabase database) {
        this.productosCollection = database.getCollection("productos");
    }
    
    public void agregarProducto(Producto producto) {
        try {
            productosCollection.insertOne(producto.toDocument());
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto: " + e.getMessage(), e);
        }
    }
    
    public void actualizarProducto(String codigo, Producto producto) {
        try {
            Bson filter = Filters.eq("codigo", codigo);
            Bson update = Updates.combine(
                Updates.set("nombre", producto.getNombre()),
                Updates.set("categoria", producto.getCategoria()),
                Updates.set("precioCompra", producto.getPrecioCompra()),
                Updates.set("precioVenta", producto.getPrecioVenta()),
                Updates.set("stock", producto.getStock()),
                Updates.set("stockMinimo", producto.getStockMinimo()),
                Updates.set("estado", producto.getEstado())
            );
            productosCollection.updateOne(filter, update);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }
    
    public void eliminarProducto(String codigo) {
        try {
            Bson filter = Filters.eq("codigo", codigo);
            productosCollection.deleteOne(filter);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }
    
    public List<Producto> obtenerTodosProductos() {
        List<Producto> productos = new ArrayList<>();
        try {
            for (Document doc : productosCollection.find()) {
                productos.add(Producto.fromDocument(doc));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos: " + e.getMessage(), e);
        }
        return productos;
    }
    
    public List<Producto> buscarProductosPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        try {
            Bson filter = Filters.eq("categoria", categoria);
            for (Document doc : productosCollection.find(filter)) {
                productos.add(Producto.fromDocument(doc));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos por categoría: " + e.getMessage(), e);
        }
        return productos;
    }
    
    public Producto buscarProductoPorCodigo(String codigo) {
        try {
            Bson filter = Filters.eq("codigo", codigo);
            Document doc = productosCollection.find(filter).first();
            return (doc != null) ? Producto.fromDocument(doc) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar producto: " + e.getMessage(), e);
        }
    }
    
    public boolean existeProducto(String codigo) {
        try {
            Bson filter = Filters.eq("codigo", codigo);
            return productosCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar producto: " + e.getMessage(), e);
        }
    }
    
    public void actualizarStock(String codigo, int cantidad) {
        try {
            Bson filter = Filters.eq("codigo", codigo);
            Producto producto = buscarProductoPorCodigo(codigo);
            if (producto != null) {
                int nuevoStock = producto.getStock() - cantidad;
                productosCollection.updateOne(filter, Updates.set("stock", nuevoStock));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage(), e);
        }
    }
}
