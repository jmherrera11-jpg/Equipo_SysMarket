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
    
    // CONSTRUCTOR CORREGIDO: Eliminado el parámetro extra "usuarioActual.getUsername()"
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
                if (!puedeAcceder("BUSCAR")) {
                    view.mostrarError("No tiene permiso para buscar productos");
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
            } catch (Exception ex) {
                view.mostrarError("Error al buscar productos: " + ex.getMessage());
            }
        });
        
        // Listener para agregar al carrito
        view.addAgregarCarritoListener(e -> {
            try {
                if (!puedeAcceder("AGREGAR_CARRITO")) {
                    view.mostrarError("No tiene permiso para agregar productos al carrito");
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
                
                view.agregarAlCarrito(producto, cantidad);
                view.mostrarExito("Producto agregado al carrito: " + producto.getNombre());
                
            } catch (Exception ex) {
                view.mostrarError("Error al agregar al carrito: " + ex.getMessage());
            }
        });
        
        // Listener para quitar del carrito
        view.addQuitarCarritoListener(e -> {
            try {
                if (!puedeAcceder("QUITAR_CARRITO")) {
                    view.mostrarError("No tiene permiso para quitar productos del carrito");
                    return;
                }
                
                int fila = view.getFilaCarritoSeleccionada();
                if (fila == -1) {
                    view.mostrarError("Seleccione un producto del carrito para quitar");
                    return;
                }
                
                view.quitarDelCarrito(fila);
                view.mostrarExito("Producto removido del carrito");
                
            } catch (Exception ex) {
                view.mostrarError("Error al quitar del carrito: " + ex.getMessage());
            }
        });
        
        // Listener para limpiar carrito
        view.addLimpiarCarritoListener(e -> {
            if (!puedeAcceder("LIMPIAR_CARRITO")) {
                view.mostrarError("No tiene permiso para limpiar el carrito");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(view,
                "¿Está seguro de limpiar el carrito?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.limpiarCarrito();
                view.mostrarExito("Carrito limpiado");
            }
        });
        
        // Listener para realizar venta
        view.addRealizarVentaListener(e -> {
            if (!puedeAcceder("REALIZAR_VENTA")) {
                view.mostrarError("No tiene permiso para realizar ventas");
                return;
            }
            
            try {
                List<Producto> carrito = view.getCarrito();
                if (carrito.isEmpty()) {
                    view.mostrarError("El carrito está vacío");
                    return;
                }
                
                // Confirmar venta
                int confirm = JOptionPane.showConfirmDialog(view,
                    String.format("¿Confirmar venta por S/. %.2f?", view.getTotalVenta()),
                    "Confirmar Venta",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                
                // Seleccionar método de pago
                String metodoPago = view.mostrarDialogoMetodoPago();
                if (metodoPago == null) {
                    return; // Usuario canceló
                }
                
                // Registrar la venta
                Venta venta = ventaController.registrarVenta(
                    usuarioActual.getUsername(),
                    carrito,
                    view.getTotalVenta(),
                    metodoPago
                );
                
                // Mostrar resumen
                view.mostrarResumenVenta(venta);
                
                // Limpiar carrito
                view.limpiarCarrito();
                
                // Actualizar lista de productos
                cargarDatosIniciales();
                
            } catch (Exception ex) {
                view.mostrarError("Error al realizar venta: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Listener para cerrar sesión
        view.addCerrarSesionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, 
                "¿Está seguro de que desea cerrar sesión?", 
                "Confirmar Cierre de Sesión", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.dispose();
                volverAlLogin();
            }
        });
    }
    
    private boolean puedeAcceder(String permiso) {
        if (usuarioActual == null) {
            return false;
        }
        
        switch(permiso) {
            case "AGREGAR_CARRITO":
                return usuarioActual.puedeAgregarCarrito();
            case "QUITAR_CARRITO":
                return usuarioActual.puedeQuitarCarrito();
            case "LIMPIAR_CARRITO":
                return usuarioActual.puedeLimpiarCarrito();
            case "REALIZAR_VENTA":
                return usuarioActual.puedeRealizarVenta();
            case "BUSCAR":
                return usuarioActual.puedeAccederGestionProductos() || usuarioActual.tieneAccesoTotal();
            default:
                return false;
        }
    }
    
    private void volverAlLogin() {
        new LoginView().setVisible(true);
    }
}