package controller;

import model.Producto;
import model.Usuario;
import model.Venta;
import view.CajeroView;
import view.LoginView;

import javax.swing.*;
import java.util.List;

public class CajeroController {
    private CajeroView view;
    private ProductoController productoController;
    private VentaController ventaController;
    private Usuario usuarioActual;
    
    public CajeroController(CajeroView view, ProductoController productoController, 
                           VentaController ventaController, Usuario usuarioActual) {
        this.view = view;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.usuarioActual = usuarioActual;
        initializeController();
    }
    
    private void initializeController() {
        cargarDatosIniciales();
        configurarListeners();
        
        // Log de permisos
        System.out.println("=== INFORMACIÓN DE USUARIO CAJERO ===");
        System.out.println("Usuario: " + usuarioActual.getUsername());
        System.out.println("Rol: " + usuarioActual.getRol());
        System.out.println("Permisos: " + usuarioActual.getPermisosEspeciales());
        System.out.println("Puede agregar carrito: " + usuarioActual.puedeAgregarCarrito());
        System.out.println("Puede quitar carrito: " + usuarioActual.puedeQuitarCarrito());
        System.out.println("Puede limpiar carrito: " + usuarioActual.puedeLimpiarCarrito());
        System.out.println("Puede realizar venta: " + usuarioActual.puedeRealizarVenta());
        System.out.println("Puede buscar productos: " + usuarioActual.puedeAccederGestionProductos());
        System.out.println("====================================");
    }
    
    private void cargarDatosIniciales() {
        try {
            var productos = productoController.obtenerTodosProductos();
            var categorias = productoController.obtenerCategorias();
            
            view.actualizarTablaProductos(productos);
            view.actualizarComboCategorias(categorias);
            
        } catch (Exception e) {
            view.mostrarError("Error al cargar datos iniciales: " + e.getMessage());
        }
    }
    
    private void configurarListeners() {
        // Listener para buscar productos
        view.addBuscarListener(e -> {
            try {
                // Verificar permiso usando el método de la vista
                if (!view.verificarPermisoAccion("BUSCAR_PRODUCTOS", true)) {
                    return;
                }
                
                String categoria = view.getCategoriaSeleccionada();
                if ("TODAS".equals(categoria)) {
                    var productos = productoController.obtenerTodosProductos();
                    view.actualizarTablaProductos(productos);
                } else {
                    var productos = productoController.buscarProductosPorCategoria(categoria);
                    view.actualizarTablaProductos(productos);
                }
                
                view.mostrarExito("Búsqueda completada");
            } catch (Exception ex) {
                view.mostrarError("Error al buscar productos: " + ex.getMessage());
            }
        });
        
        // Listener para agregar al carrito
        view.addAgregarCarritoListener(e -> {
            try {
                // Verificar permiso usando el método de la vista
                if (!view.verificarPermisoAccion("AGREGAR_CARRITO", true)) {
                    return;
                }
                
                String codigo = view.getCodigoProductoSeleccionado();
                if (codigo == null) {
                    view.mostrarError("Por favor seleccione un producto de la lista");
                    return;
                }
                
                int cantidad = view.getCantidad();
                if (cantidad <= 0) {
                    view.mostrarError("La cantidad debe ser mayor a 0");
                    return;
                }
                
                Producto producto = productoController.buscarProductoPorCodigo(codigo);
                if (producto == null) {
                    view.mostrarError("Producto no encontrado");
                    return;
                }
                
                if (producto.getStock() < cantidad) {
                    view.mostrarError("Stock insuficiente. Disponible: " + producto.getStock());
                    return;
                }
                
                // Verificar si el producto ya está en el carrito
                boolean productoExistente = false;
                List<Producto> carritoActual = view.getCarrito();
                for (Producto p : carritoActual) {
                    if (p.getCodigo().equals(codigo)) {
                        productoExistente = true;
                        break;
                    }
                }
                
                if (productoExistente) {
                    int opcion = JOptionPane.showConfirmDialog(view,
                        "Este producto ya está en el carrito. ¿Desea agregar más unidades?",
                        "Producto Existente",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (opcion != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                view.agregarAlCarrito(producto, cantidad);
                view.mostrarExito("Producto agregado al carrito: " + producto.getNombre());
                
            } catch (Exception ex) {
                view.mostrarError("Error al agregar al carrito: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Listener para quitar del carrito
        view.addQuitarCarritoListener(e -> {
            try {
                // Verificar permiso usando el método de la vista
                if (!view.verificarPermisoAccion("QUITAR_CARRITO", true)) {
                    return;
                }
                
                int fila = view.getFilaCarritoSeleccionada();
                if (fila == -1) {
                    view.mostrarError("Seleccione un producto del carrito para quitar");
                    return;
                }
                
                List<Producto> carrito = view.getCarrito();
                if (fila >= 0 && fila < carrito.size()) {
                    Producto producto = carrito.get(fila);
                    int confirm = JOptionPane.showConfirmDialog(view,
                        "¿Quitar " + producto.getNombre() + " del carrito?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        view.quitarDelCarrito(fila);
                        view.mostrarExito("Producto removido del carrito");
                    }
                }
                
            } catch (Exception ex) {
                view.mostrarError("Error al quitar del carrito: " + ex.getMessage());
            }
        });
        
        // Listener para limpiar carrito
        view.addLimpiarCarritoListener(e -> {
            // Verificar permiso usando el método de la vista
            if (!view.verificarPermisoAccion("LIMPIAR_CARRITO", true)) {
                return;
            }
            
            List<Producto> carrito = view.getCarrito();
            if (carrito.isEmpty()) {
                view.mostrarError("El carrito ya está vacío");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(view,
                "¿Está seguro de limpiar todo el carrito?\n" +
                "Se perderán " + carrito.size() + " productos.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.limpiarCarrito();
                view.mostrarExito("Carrito limpiado exitosamente");
            }
        });
        
        // Listener para realizar venta
        view.addRealizarVentaListener(e -> {
            // Verificar permiso usando el método de la vista
            if (!view.verificarPermisoAccion("REALIZAR_VENTA", true)) {
                return;
            }
            
            try {
                List<Producto> carrito = view.getCarrito();
                if (carrito.isEmpty()) {
                    view.mostrarError("El carrito está vacío. Agregue productos antes de realizar una venta.");
                    return;
                }
                
                // Verificar stock actual antes de proceder
                boolean stockSuficiente = true;
                StringBuilder erroresStock = new StringBuilder();
                
                for (Producto productoCarrito : carrito) {
                    Producto productoBD = productoController.buscarProductoPorCodigo(productoCarrito.getCodigo());
                    if (productoBD == null) {
                        stockSuficiente = false;
                        erroresStock.append("- Producto no encontrado: ").append(productoCarrito.getNombre()).append("\n");
                    } else if (productoBD.getStock() < productoCarrito.getStock()) {
                        stockSuficiente = false;
                        erroresStock.append("- Stock insuficiente para ").append(productoCarrito.getNombre())
                                   .append(" (Disponible: ").append(productoBD.getStock())
                                   .append(", Solicitado: ").append(productoCarrito.getStock()).append(")\n");
                    }
                }
                
                if (!stockSuficiente) {
                    view.mostrarError("Error de stock:\n" + erroresStock.toString());
                    return;
                }
                
                // Confirmar venta
                int confirm = JOptionPane.showConfirmDialog(view,
                    String.format("¿Confirmar venta por S/. %.2f?\n\n" +
                                 "Total productos: %d\n" +
                                 "Monto total: S/. %.2f",
                                 view.getTotalVenta(), carrito.size(), view.getTotalVenta()),
                    "Confirmar Venta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                
                // Seleccionar método de pago
                String metodoPago = view.mostrarDialogoMetodoPago();
                if (metodoPago == null) {
                    view.mostrarError("Venta cancelada. Debe seleccionar un método de pago.");
                    return;
                }
                
                // Registrar la venta
                Venta venta = ventaController.registrarVenta(
                    usuarioActual.getUsername(),
                    carrito,
                    view.getTotalVenta(),
                    metodoPago
                );
                
                if (venta == null) {
                    view.mostrarError("Error al registrar la venta. Intente nuevamente.");
                    return;
                }
                
                // Mostrar resumen
                view.mostrarResumenVenta(venta);
                
                // Limpiar carrito
                view.limpiarCarrito();
                
                // Actualizar lista de productos
                cargarDatosIniciales();
                
                view.mostrarExito("✅ Venta registrada exitosamente\n" +
                                 "ID de venta: " + venta.getId() + "\n" +
                                 "Total: S/. " + String.format("%.2f", venta.getTotal()));
                
            } catch (Exception ex) {
                view.mostrarError("Error al realizar venta: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Listener para cerrar sesión
        view.addCerrarSesionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, 
                "¿Está seguro de que desea cerrar sesión?\n\n" +
                "Se perderá el carrito actual si tiene productos.",
                "Confirmar Cierre de Sesión", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.dispose();
                volverAlLogin();
            }
        });
    }
    
    private void volverAlLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}