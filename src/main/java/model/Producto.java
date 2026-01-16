package model;

import org.bson.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Producto {
    private String codigo;
    private String nombre;
    private String categoria;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private int stockMinimo;
    private String estado;
    private LocalDateTime fechaCreacion;
    
    public Producto() {}
    
    public Producto(String codigo, String nombre, String categoria, 
                   double precioCompra, double precioVenta, 
                   int stock, int stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.estado = (stock > 0) ? "ACTIVO" : "AGOTADO";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }
    
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { 
        this.stock = stock; 
        this.estado = (stock > 0) ? "ACTIVO" : "AGOTADO";
    }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    // Convertir a Document
    public Document toDocument() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaStr = (fechaCreacion != null) ? fechaCreacion.format(formatter) : LocalDateTime.now().format(formatter);
        
        return new Document("codigo", codigo)
                .append("nombre", nombre)
                .append("categoria", categoria)
                .append("precioCompra", precioCompra)
                .append("precioVenta", precioVenta)
                .append("stock", stock)
                .append("stockMinimo", stockMinimo)
                .append("estado", estado)
                .append("fechaCreacion", fechaStr);
    }
    
    // Crear desde Document
    public static Producto fromDocument(Document doc) {
        Producto producto = new Producto();
        producto.setCodigo(doc.getString("codigo"));
        producto.setNombre(doc.getString("nombre"));
        producto.setCategoria(doc.getString("categoria"));
        producto.setPrecioCompra(doc.getDouble("precioCompra"));
        producto.setPrecioVenta(doc.getDouble("precioVenta"));
        
        // Manejar diferentes tipos de datos para stock
        Object stockObj = doc.get("stock");
        if (stockObj instanceof Integer) {
            producto.setStock((Integer) stockObj);
        } else if (stockObj instanceof Double) {
            producto.setStock(((Double) stockObj).intValue());
        } else if (stockObj instanceof String) {
            producto.setStock(Integer.parseInt((String) stockObj));
        } else {
            producto.setStock(0);
        }
        
        // Manejar stock mínimo
        Object stockMinObj = doc.get("stockMinimo");
        if (stockMinObj instanceof Integer) {
            producto.setStockMinimo((Integer) stockMinObj);
        } else if (stockMinObj instanceof Double) {
            producto.setStockMinimo(((Double) stockMinObj).intValue());
        } else {
            producto.setStockMinimo(0);
        }
        
        producto.setEstado(doc.getString("estado"));
        
        // Manejar fecha de creación
        if (doc.getString("fechaCreacion") != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                producto.setFechaCreacion(LocalDateTime.parse(doc.getString("fechaCreacion"), formatter));
            } catch (Exception e) {
                producto.setFechaCreacion(LocalDateTime.now());
            }
        } else {
            producto.setFechaCreacion(LocalDateTime.now());
        }
        
        return producto;
    }
    
    @Override
    public String toString() {
        return String.format("Producto{codigo='%s', nombre='%s', precioVenta=%.2f, stock=%d}", 
                codigo, nombre, precioVenta, stock);
    }
}