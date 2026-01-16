package repository;

import model.Venta;
import model.Producto;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

public class VentaRepository {
    private MongoCollection<Document> ventasCollection;
    
    public VentaRepository(MongoDatabase database) {
        this.ventasCollection = database.getCollection("ventas");
    }
    
    public void registrarVenta(Venta venta) {
        try {
            ventasCollection.insertOne(venta.toDocument());
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar venta: " + e.getMessage(), e);
        }
    }
    
    public List<Venta> obtenerTodasVentas() {
        List<Venta> ventas = new ArrayList<>();
        try {
            for (Document doc : ventasCollection.find()) {
                Venta venta = Venta.fromDocument(doc);
                if (venta != null) {
                    ventas.add(venta);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ventas: " + e.getMessage(), e);
        }
        return ventas;
    }
    
    public List<Venta> obtenerVentasPorRangoFechas(String fechaInicio, String fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        try {
            // Implementación simplificada - filtrar por fecha
            // En producción, usarías un parser de fechas real
            for (Document doc : ventasCollection.find()) {
                Venta venta = Venta.fromDocument(doc);
                if (venta != null) {
                    // Filtro básico por fecha (deberías implementar lógica real aquí)
                    ventas.add(venta);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ventas por fecha: " + e.getMessage(), e);
        }
        return ventas;
    }
    
    public List<Venta> obtenerVentasPorUsuario(String usuario) {
        List<Venta> ventas = new ArrayList<>();
        try {
            Bson filter = Filters.eq("usuario", usuario);
            for (Document doc : ventasCollection.find(filter)) {
                Venta venta = Venta.fromDocument(doc);
                if (venta != null) {
                    ventas.add(venta);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ventas por usuario: " + e.getMessage(), e);
        }
        return ventas;
    }
}