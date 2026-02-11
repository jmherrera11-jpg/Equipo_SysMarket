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
    
    // Permisos por defecto según rol
    private static final List<String> PERMISOS_SUPERUSUARIO = Arrays.asList(
        "ACCESO_TOTAL", "GESTIONAR_USUARIOS", "GESTIONAR_PRODUCTOS",
        "VER_REPORTES", "EXPORTAR_DATOS", "CONFIGURAR_SISTEMA"
    );
    
    private static final List<String> PERMISOS_GERENTE = Arrays.asList(
        "ACCEDER_GESTION_PRODUCTOS", "ACCEDER_CONTROL_VENTAS",
        "ACCEDER_REPORTES", "ACCEDER_DASHBOARD", "VER_REPORTES",
        "EXPORTAR_DATOS", "GESTIONAR_PRODUCTOS"
    );
    
    private static final List<String> PERMISOS_BODEGUERO = Arrays.asList(
        "AGREGAR_PRODUCTOS", "EDITAR_PRODUCTOS", "ELIMINAR_PRODUCTOS",
        "MODIFICAR_PRECIOS", "AJUSTAR_STOCK", "GESTIONAR_CATEGORIAS"
    );
    
    private static final List<String> PERMISOS_CAJERO = Arrays.asList(
        "PROCESAR_VENTAS", "AGREGAR_CARRITO", "QUITAR_CARRITO",
        "LIMPIAR_CARRITO", "REALIZAR_VENTA", "VER_REPORTES_BASICOS"
    );
    
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
        "VER_REPORTES",
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
        asignarPermisosPorDefecto();
    }
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { 
        this.rol = rol;
        asignarPermisosPorDefecto();
    }
    
    public List<String> getPermisosEspeciales() { 
        return permisosEspeciales; 
    }
    
    public void setPermisosEspeciales(List<String> permisosEspeciales) { 
        if (permisosEspeciales == null) {
            this.permisosEspeciales = new ArrayList<>();
        } else {
            this.permisosEspeciales = new ArrayList<>(permisosEspeciales);
        }
    }
    
    // Método privado para asignar permisos por defecto según rol
    private void asignarPermisosPorDefecto() {
        if (permisosEspeciales == null) {
            permisosEspeciales = new ArrayList<>();
        }
        
        // Limpiar y asignar permisos por defecto
        permisosEspeciales.clear();
        
        switch (rol) {
            case ROL_SUPERUSUARIO:
                permisosEspeciales.addAll(PERMISOS_SUPERUSUARIO);
                break;
            case ROL_GERENTE:
                permisosEspeciales.addAll(PERMISOS_GERENTE);
                break;
            case ROL_BODEGUERO:
                permisosEspeciales.addAll(PERMISOS_BODEGUERO);
                break;
            case ROL_CAJERO:
                permisosEspeciales.addAll(PERMISOS_CAJERO);
                break;
        }
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
    
    public void quitarPermiso(String permiso) {
        if (permiso != null && !permiso.equals("ACCESO_TOTAL") && esSuperusuario()) {
            // No permitir quitar ACCESO_TOTAL a superusuarios
            return;
        }
        permisosEspeciales.remove(permiso);
    }
    
    public boolean tienePermiso(String permiso) {
        if (permiso == null) return false;
        
        // Si tiene ACCESO_TOTAL, tiene todos los permisos
        if (tieneAccesoTotal()) {
            return true;
        }
        
        // Verificar si tiene el permiso específico
        return permisosEspeciales.contains(permiso);
    }
    
    public boolean tieneAccesoTotal() {
        return permisosEspeciales.contains("ACCESO_TOTAL") || esSuperusuario();
    }
    
    // Método para validar si un permiso es válido para el rol
    public static boolean esPermisoValidoParaRol(String permiso, String rol) {
        if (ROL_SUPERUSUARIO.equals(rol)) {
            return true; // Superusuario puede tener cualquier permiso
        }
        
        // Definir permisos válidos para cada rol
        switch (rol) {
            case ROL_GERENTE:
                return permiso.startsWith("ACCEDER_") || 
                       permiso.startsWith("VER_") ||
                       permiso.equals("EXPORTAR_DATOS") ||
                       permiso.equals("GESTIONAR_PRODUCTOS") ||
                       permiso.equals("GESTIONAR_CATEGORIAS");
            case ROL_BODEGUERO:
                return permiso.contains("PRODUCTOS") || 
                       permiso.equals("AJUSTAR_STOCK") ||
                       permiso.equals("GESTIONAR_CATEGORIAS") ||
                       permiso.equals("MODIFICAR_PRECIOS");
            case ROL_CAJERO:
                return permiso.contains("CARRITO") || 
                       permiso.contains("VENTA") ||
                       permiso.equals("PROCESAR_VENTAS") ||
                       permiso.equals("GENERAR_COMPROBANTES") ||
                       permiso.equals("VER_REPORTES_BASICOS");
            default:
                return false;
        }
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
        } else {
            // Si no hay permisos en el documento, asignar por defecto
            usuario.asignarPermisosPorDefecto();
        }
        
        return usuario;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{username='%s', rol='%s', permisos=%s}", 
                username, rol, permisosEspeciales);
    }
    
    // Métodos de validación de permisos simplificados
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
               tienePermiso("VER_REPORTES") ||
               puedeAccederReportes() ||
               tieneAccesoTotal();
    }
    
    public boolean puedeGestionarUsuarios() {
        return tienePermiso("GESTIONAR_USUARIOS") || tieneAccesoTotal();
    }
    
    public boolean puedeExportarDatos() {
        return tienePermiso("EXPORTAR_DATOS") || tieneAccesoTotal();
    }
    
    public boolean puedeConfigurarSistema() {
        return tienePermiso("CONFIGURAR_SISTEMA") || tieneAccesoTotal();
    }
    
    public boolean puedeGestionarCategorias() {
        return tienePermiso("GESTIONAR_CATEGORIAS") || tieneAccesoTotal();
    }
}