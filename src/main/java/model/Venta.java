package model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.bson.Document;

public class Venta {
    private String id;
    private Date fecha;
    private String usuario;
    private String cedulaCliente;  // AGREGADO: Cédula del cliente
    private List<Producto> productos;
    private double total;
    private String metodoPago;
    private String estado;
    
    // Constructor para nuevas ventas SIN cédula
    public Venta(String usuario, List<Producto> productos, double total, String metodoPago) {
        this.id = "VTA-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.fecha = new Date();
        this.usuario = usuario;
        this.cedulaCliente = "";  // AGREGADO
        this.productos = productos != null ? productos : new ArrayList<>();
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = "COMPLETADA";
    }
    
    // Constructor para nuevas ventas CON cédula
    public Venta(String usuario, String cedulaCliente, List<Producto> productos, double total, String metodoPago) {
        this.id = "VTA-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.fecha = new Date();
        this.usuario = usuario;
        this.cedulaCliente = cedulaCliente != null ? cedulaCliente : "";  // AGREGADO
        this.productos = productos != null ? productos : new ArrayList<>();
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = "COMPLETADA";
    }
    
    // Constructor completo
    public Venta(String id, Date fecha, String usuario, String cedulaCliente, List<Producto> productos, 
                 double total, String metodoPago, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.cedulaCliente = cedulaCliente != null ? cedulaCliente : "";  // AGREGADO
        this.productos = productos != null ? productos : new ArrayList<>();
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }
    
    // Getters
    public String getId() { return id; }
    public Date getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getCedulaCliente() { return cedulaCliente; }  // AGREGADO
    public List<Producto> getProductos() { return productos; }
    public double getTotal() { return total; }
    public String getMetodoPago() { return metodoPago; }
    public String getEstado() { return estado; }
    
    // Setter para cédula
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }  // AGREGADO
    
    // Método para convertir a Document
    public Document toDocument() {
        List<Document> productosDoc = new ArrayList<>();
        for (Producto producto : productos) {
            productosDoc.add(producto.toDocument());
        }
        
        return new Document("id", id)
                .append("fecha", fecha)
                .append("usuario", usuario)
                .append("cedulaCliente", cedulaCliente)  // AGREGADO
                .append("productos", productosDoc)
                .append("total", total)
                .append("metodoPago", metodoPago)
                .append("estado", estado);
    }
    
    // Método estático para crear desde Document
    public static Venta fromDocument(Document doc) {
        try {
            String id = doc.getString("id");
            Date fecha = doc.getDate("fecha");
            String usuario = doc.getString("usuario");
            String cedulaCliente = doc.getString("cedulaCliente");  // AGREGADO
            double total = doc.getDouble("total");
            String metodoPago = doc.getString("metodoPago");
            String estado = doc.getString("estado");
            
            List<Producto> productos = new ArrayList<>();
            List<Document> productosDoc = (List<Document>) doc.get("productos");
            if (productosDoc != null) {
                for (Document productoDoc : productosDoc) {
                    productos.add(Producto.fromDocument(productoDoc));
                }
            }
            
            return new Venta(id, fecha, usuario, cedulaCliente, productos, total, metodoPago, estado);  // MODIFICADO
        } catch (Exception e) {
            System.err.println("Error al convertir Document a Venta: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Venta{id='%s', usuario='%s', cedula='%s', total=%.2f, fecha=%s}", 
                id, usuario, cedulaCliente, total, fecha);
    }
}