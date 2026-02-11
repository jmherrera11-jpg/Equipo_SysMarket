package controller;


import model.Producto;
import model.Usuario;
import view.BodegueroView;
import view.LoginView;

import javax.swing.*;
import java.util.List;

public class BodegueroController {
    private BodegueroView view;
    private ProductoController productoController;
    private Usuario usuarioActual; // AGREGADO
    
    // CONSTRUCTOR MODIFICADO: Agregar usuarioActual como parámetro
    public BodegueroController(BodegueroView view, ProductoController productoController, Usuario usuarioActual) {
        this.view = view;
        this.productoController = productoController;
        this.usuarioActual = usuarioActual; // AGREGADO
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
            categorias.add("Confitería");
            
            view.actualizarTablaProductos(productos);
            view.actualizarComboCategorias(categorias);
            
        } catch (Exception e) {
            view.mostrarError("Error al cargar datos iniciales: " + e.getMessage());
        }
    }
    
    private void configurarListeners() {
        // Listener para agregar producto
        view.addAgregarListener(e -> {
            try {
                // Verificar permiso para agregar
                if (!usuarioActual.puedeAgregarProductos()) { // MODIFICADO: usar usuarioActual del controlador
                    view.mostrarError("No tiene permiso para agregar productos");
                    return;
                }
                var producto = new Producto(
                    view.getCodigo(),
                    view.getNombre(),
                    view.getCategoria(),
                    view.getPrecioCompra(),
                    view.getPrecioVenta(),
                    view.getStock(),
                    view.getStockMinimo()
                );
                
                productoController.agregarProducto(producto);
                view.mostrarExito("Producto agregado exitosamente");
                view.limpiarFormulario();
                cargarDatosIniciales();
                
            } catch (NumberFormatException ex) {
                view.mostrarError("Por favor ingrese valores numéricos válidos");
            } catch (Exception ex) {
                view.mostrarError(ex.getMessage());
            }
        });
        
        // Listener para editar producto
        view.addEditarListener(e -> {
            try {
                String codigo = view.getCodigoFilaSeleccionada();
                if (codigo == null) {
                    view.mostrarError("Por favor seleccione un producto para editar");
                    return;
                }
                
                // Verificar permiso para modificar precios
                if (!usuarioActual.puedeModificarPrecios()) { // MODIFICADO
                    view.mostrarError("No tiene permiso para modificar precios de productos");
                    return;
                }
                
                var producto = productoController.buscarProductoPorCodigo(codigo);
                if (producto != null) {
                    view.setCodigo(producto.getCodigo());
                    view.setNombre(producto.getNombre());
                    view.setPrecioCompra(producto.getPrecioCompra());
                    view.setPrecioVenta(producto.getPrecioVenta());
                    view.setStock(producto.getStock());
                    view.setStockMinimo(producto.getStockMinimo());
                    view.setCategoria(producto.getCategoria());
                }
                
            } catch (Exception ex) {
                view.mostrarError("Error al cargar producto: " + ex.getMessage());
            }
        });
        
        // Listener para eliminar producto
        view.addEliminarListener(e -> {
            try {
                String codigo = view.getCodigoFilaSeleccionada();
                if (codigo == null) {
                    view.mostrarError("Por favor seleccione un producto para eliminar");
                    return;
                }
                // Verificar permiso para eliminar
                if (!usuarioActual.puedeEliminarProductos()) { // MODIFICADO
                    view.mostrarError("No tiene permiso para eliminar productos");
                    return;
                }
                var producto = productoController.buscarProductoPorCodigo(codigo);
                if (producto != null && view.confirmarEliminacion(producto.getNombre())) {
                    productoController.eliminarProducto(codigo);
                    view.mostrarExito("Producto eliminado exitosamente");
                    cargarDatosIniciales();
                }
                
            } catch (Exception ex) {
                view.mostrarError("Error al eliminar producto: " + ex.getMessage());
            }
        });
        
        // Listener para actualizar/guardar cambios del producto
        view.addActualizarListener(e -> {
            try {
                String codigo = view.getCodigo().trim();
                if (codigo.isEmpty()) {
                    view.mostrarError("Por favor seleccione un producto para editar o ingrese su código");
                    return;
                }
                
                // Verificar que el producto existe
                if (!productoController.existeProducto(codigo)) {
                    view.mostrarError("El producto con código: " + codigo + " no existe");
                    return;
                }
                
                // Verificar permisos según los campos modificados
                double precioCompra = view.getPrecioCompra();
                double precioVenta = view.getPrecioVenta();
                int stock = view.getStock();
                
                Producto productoActual = productoController.buscarProductoPorCodigo(codigo);
                
                // Si se modifican precios, verificar permiso
                if ((precioCompra != productoActual.getPrecioCompra() || 
                     precioVenta != productoActual.getPrecioVenta()) &&
                    !usuarioActual.puedeModificarPrecios()) {
                    view.mostrarError("No tiene permiso para modificar precios de productos");
                    return;
                }
                
                // Si se modifica stock, verificar permiso
                if (stock != productoActual.getStock() &&
                    !usuarioActual.puedeAjustarStock()) {
                    view.mostrarError("No tiene permiso para ajustar el stock de productos");
                    return;
                }
                
                // Crear producto con los nuevos datos
                var productoBuscado = productoController.buscarProductoPorCodigo(codigo);
                var productoActualizado = new Producto(
                    codigo,
                    view.getNombre(),
                    view.getCategoria(),
                    view.getPrecioCompra(),
                    view.getPrecioVenta(),
                    view.getStock(),
                    view.getStockMinimo()
                );
                
                // Copiar la fecha de creación original
                productoActualizado.setFechaCreacion(productoBuscado.getFechaCreacion());
                
                // Actualizar producto en MongoDB
                productoController.actualizarProducto(codigo, productoActualizado);
                view.mostrarExito("Producto actualizado exitosamente en MongoDB");
                view.limpiarFormulario();
                cargarDatosIniciales();
                
            } catch (NumberFormatException ex) {
                view.mostrarError("Por favor ingrese valores numéricos válidos\n" +
                                "Precio: Decimal (ej: 10.50)\n" +
                                "Stock: Número entero (ej: 100)");
            } catch (Exception ex) {
                view.mostrarError("Error al actualizar producto: " + ex.getMessage());
            }
        });
        
        // Listener para buscar por categoría
        view.addBuscarListener(e -> {
            try {
                String categoria = view.getCategoria();
                if ("TODAS".equals(categoria)) {
                    cargarDatosIniciales();
                } else {
                    var productos = productoController.buscarProductosPorCategoria(categoria);
                    view.actualizarTablaProductos(productos);
                }
            } catch (Exception ex) {
                view.mostrarError("Error al buscar productos: " + ex.getMessage());
            }
        });
        
        // Listener para limpiar filtro
        view.addLimpiarListener(e -> {
            view.limpiarFormulario();
            cargarDatosIniciales();
        });
        
        // Listener de cierre de sesión
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
    
    private void volverAlLogin() {
        view.dispose();
        new LoginView().setVisible(true);
    }
}