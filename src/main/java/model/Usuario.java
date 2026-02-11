package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;

public class Usuario {
    private String username;
    private String password;
    private String rol;
    private List<String> permisosEspeciales = new ArrayList<>();
    
    // Constantes para los roles
    public static final String ROL_SUPERUSUARIO = "SUPERUSUARIO";
    public static final String ROL_GERENTE = "GERENTE";
    public static final String ROL_BODEGUERO = "BODEGUERO";
    public static final String ROL_CAJERO = "CAJERO";
    
    // Constante para todos los permisos disponibles
    public static final List<String> TODOS_LOS_PERMISOS = Arrays.asList(
        "ACCESO_TOTAL",
        "AGREGAR_PRODUCTOS",
        "EDITAR_PRODUCTOS", 
        "ELIMINAR_PRODUCTOS",
        "MODIFICAR_PRECIOS",
        "AJUSTAR_STOCK",
        "PROCESAR_VENTAS",
        "AGREGAR_CARRITO",
        "QUITAR_CARRITO",
        "LIMPIAR_CARRITO",
        "REALIZAR_VENTA",
        "ACCEDER_GESTION_PRODUCTOS",
        "ACCEDER_CONTROL_VENTAS",
        "ACCEDER_REPORTES",
        "ACCEDER_DASHBOARD",
        "VER_REPORTES_BASICOS",
        "VER_REPORTES_AVANZADOS",
        "GESTIONAR_USUARIOS",
        "EXPORTAR_DATOS",
        "CONFIGURAR_SISTEMA",
        "GESTIONAR_CATEGORIAS",
        "GENERAR_COMPROBANTES"
    );
    
    public Usuario() {}
    
    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public List<String> getPermisosEspeciales() { 
        return permisosEspeciales; 
    }
    
    public void setPermisosEspeciales(List<String> permisosEspeciales) { 
        this.permisosEspeciales = permisosEspeciales != null ? permisosEspeciales : new ArrayList<>();
    }
    
    
    // Métodos de utilidad
    public boolean esSuperusuario() {
        return ROL_SUPERUSUARIO.equals(this.rol);
    }
    
    public boolean esGerente() {
        return ROL_GERENTE.equals(this.rol);
    }
    
    public boolean esBodeguero() {
        return ROL_BODEGUERO.equals(this.rol);
    }
    
    public boolean esCajero() {
        return ROL_CAJERO.equals(this.rol);
    }
    
    public void agregarPermiso(String permiso) {
        if (permiso != null && !permisosEspeciales.contains(permiso)) {
            permisosEspeciales.add(permiso);
        }
    }
    
    public boolean tienePermiso(String permiso) {
        return permisosEspeciales.contains(permiso) || tieneAccesoTotal();
    }
    
    public boolean tieneAccesoTotal() {
        return permisosEspeciales.contains("ACCESO_TOTAL") || esSuperusuario();
    }
    
    // Método adicional para verificar permisos especiales
    public boolean tienePermisoEspecial(String permiso) {
        return permisosEspeciales.contains(permiso);
    }
    
    // Convertir a Document
    public Document toDocument() {
        return new Document("username", username)
                .append("password", password)
                .append("rol", rol)
                .append("permisosEspeciales", permisosEspeciales);
    }
    
    // Crear desde Document
    public static Usuario fromDocument(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setUsername(doc.getString("username"));
        usuario.setPassword(doc.getString("password"));
        usuario.setRol(doc.getString("rol"));
        
        if (doc.get("permisosEspeciales") != null) {
            List<String> permisos = doc.getList("permisosEspeciales", String.class);
            usuario.setPermisosEspeciales(permisos);
        }
        
        return usuario;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{username='%s', rol='%s'}", username, rol);
    }
    
    // Métodos de validación de permisos (SIMPLIFICADOS)
    public boolean puedeAgregarProductos() {
        return tienePermiso("AGREGAR_PRODUCTOS") || tieneAccesoTotal();
    }
    
    public boolean puedeEditarProductos() {
        return tienePermiso("EDITAR_PRODUCTOS") || tieneAccesoTotal();
    }
    
    public boolean puedeEliminarProductos() {
        return tienePermiso("ELIMINAR_PRODUCTOS") || tieneAccesoTotal();
    }
    
    public boolean puedeModificarPrecios() {
        return tienePermiso("MODIFICAR_PRECIOS") || tieneAccesoTotal();
    }
    
    public boolean puedeAjustarStock() {
        return tienePermiso("AJUSTAR_STOCK") || tieneAccesoTotal();
    }
    
    public boolean puedeProcesarVentas() {
        return tienePermiso("PROCESAR_VENTAS") || tieneAccesoTotal();
    }
    
    public boolean puedeAgregarCarrito() {
        return tienePermiso("AGREGAR_CARRITO") || puedeProcesarVentas();
    }
    
    public boolean puedeQuitarCarrito() {
        return tienePermiso("QUITAR_CARRITO") || puedeProcesarVentas();
    }
    
    public boolean puedeLimpiarCarrito() {
        return tienePermiso("LIMPIAR_CARRITO") || puedeProcesarVentas();
    }
    
    public boolean puedeRealizarVenta() {
        return tienePermiso("REALIZAR_VENTA") || puedeProcesarVentas();
    }
    
    public boolean puedeAccederGestionProductos() {
        return tienePermiso("ACCEDER_GESTION_PRODUCTOS") || tieneAccesoTotal();
    }
    
    public boolean puedeAccederControlVentas() {
        return tienePermiso("ACCEDER_CONTROL_VENTAS") || tieneAccesoTotal();
    }
    
    public boolean puedeAccederReportes() {
        return tienePermiso("ACCEDER_REPORTES") || tieneAccesoTotal();
    }
    
    public boolean puedeAccederDashboard() {
        return tienePermiso("ACCEDER_DASHBOARD") || tieneAccesoTotal();
    }
    
    public boolean puedeVerReportes() {
        return tienePermiso("VER_REPORTES_BASICOS") || 
               tienePermiso("VER_REPORTES_AVANZADOS") || 
               puedeAccederReportes() ||
               tieneAccesoTotal();
    }
}