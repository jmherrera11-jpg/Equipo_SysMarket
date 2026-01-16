package controller;

import view.GerenteView;
import view.LoginView;
import javax.swing.*;
import model.Usuario;

public class GerenteViewController {
    private GerenteView view;
    private ProductoController productoController;
    private VentaController ventaController;
    private GerenteController gerenteController;
    private Usuario usuarioActual;
    
    public GerenteViewController(GerenteView view, ProductoController productoController,
                                VentaController ventaController, GerenteController gerenteController,
                                Usuario usuarioActual) {
        this.view = view;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.gerenteController = gerenteController;
        this.usuarioActual = usuarioActual;
        
        initializeController();
    }
    
    private void initializeController() {
        cargarDatosIniciales();
        configurarListeners();
        configurarPermisosIniciales();
    }
    
    private void cargarDatosIniciales() {
        try {
            var productos = productoController.obtenerTodosProductos();
            var categorias = productoController.obtenerCategorias();
            var ventas = ventaController.obtenerTodasVentas();
            var estadisticas = gerenteController.obtenerEstadisticasDashboard();
            
            categorias.add("Confitería");
            
            view.actualizarTablaProductos(productos);
            view.actualizarComboCategorias(categorias);
            view.actualizarTablaVentas(ventas);
            
            // Actualizar dashboard con estadísticas
            if (estadisticas != null) {
                view.actualizarDashboard(
                    (Double) estadisticas.get("totalVentas"),
                    (String) estadisticas.get("productoMasVendido"),
                    (String) estadisticas.get("categoriaMasVendida"),
                    (Integer) estadisticas.get("stockCritico")
                );
            } else {
                view.actualizarDashboard(0.0, "Sin datos", "Sin datos", 0);
            }
            
        } catch (Exception e) {
            view.mostrarError("Error al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configurarListeners() {
        JTabbedPane tabbedPane = view.getTabbedPane();
        
        // Listener para cambios de pestaña
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String tabName = tabbedPane.getTitleAt(selectedIndex);
            
            if (!validarAccesoAPestana(tabName)) {
                cambiarAPrimeraPestañaPermitida(tabbedPane);
            }
        });
        
        // Listener para buscar productos
        view.addBuscarProductosListener(e -> {
            try {
                if (!puedeAccederGestionProductos()) {
                    view.mostrarError("No tiene permiso para acceder a Gestión de Productos");
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
        
        // Listener para generar reporte de stock
        view.addReporteStockListener(e -> {
            try {
                if (!puedeAccederReportes()) {
                    view.mostrarError("No tiene permiso para acceder a Reportes");
                    return;
                }
                
                String reporte = gerenteController.generarReporteStock();
                view.mostrarReporte(reporte);
            } catch (Exception ex) {
                view.mostrarError("Error al generar reporte: " + ex.getMessage());
            }
        });
        
        // Listener para generar reportes desde combo
        view.addGenerarReporteListener(e -> {
            try {
                if (!puedeAccederReportes()) {
                    view.mostrarError("No tiene permiso para acceder a Reportes");
                    return;
                }
                
                String tipoReporte = view.getTipoReporte();
                String fechaInicio = view.getFechaInicio();
                String fechaFin = view.getFechaFin();
                String reporte = "";
                
                switch (tipoReporte) {
                    case "Ventas por Período":
                    case "Ventas por Categoría":
                        reporte = gerenteController.generarReporteVentas(fechaInicio, fechaFin);
                        break;
                    case "Stock Crítico":
                        reporte = gerenteController.generarReporteStock();
                        break;
                    case "Productos Más Vendidos":
                        reporte = gerenteController.generarReporteProductosMasVendidos(fechaInicio, fechaFin);
                        break;
                    case "Rentabilidad por Producto":
                        reporte = gerenteController.generarReporteRentabilidad();
                        break;
                    default:
                        reporte = "Tipo de reporte no reconocido: " + tipoReporte;
                }
                
                view.mostrarReporte(reporte);
                
            } catch (Exception ex) {
                view.mostrarError("Error al generar reporte: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Listener para filtrar ventas
        view.addFiltrarVentasListener(e -> {
            try {
                if (!puedeAccederControlVentas()) {
                    view.mostrarError("No tiene permiso para acceder a Control de Ventas");
                    return;
                }
                
                String fechaInicio = view.getFechaInicio();
                String fechaFin = view.getFechaFin();
                var ventas = ventaController.obtenerVentasPorRangoFechas(fechaInicio, fechaFin);
                view.actualizarTablaVentas(ventas);
                
            } catch (Exception ex) {
                view.mostrarError("Error al filtrar ventas: " + ex.getMessage());
            }
        });
        
        // Listener para reporte de ventas
        view.addReporteVentasListener(e -> {
            try {
                if (!puedeAccederReportes()) {
                    view.mostrarError("No tiene permiso para acceder a Reportes");
                    return;
                }
                
                String fechaInicio = view.getFechaInicio();
                String fechaFin = view.getFechaFin();
                String reporte = gerenteController.generarReporteVentas(fechaInicio, fechaFin);
                view.mostrarReporte(reporte);
                
            } catch (Exception ex) {
                view.mostrarError("Error al generar reporte de ventas: " + ex.getMessage());
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
    
    // Métodos de validación de permisos
    private boolean puedeAccederGestionProductos() {
        return usuarioActual != null && 
               (usuarioActual.puedeAccederGestionProductos() || 
                usuarioActual.tieneAccesoTotal());
    }
    
    private boolean puedeAccederControlVentas() {
        return usuarioActual != null && 
               (usuarioActual.puedeAccederControlVentas() || 
                usuarioActual.tieneAccesoTotal());
    }
    
    private boolean puedeAccederReportes() {
        return usuarioActual != null && 
               (usuarioActual.puedeAccederReportes() || 
                usuarioActual.puedeVerReportes() ||
                usuarioActual.tieneAccesoTotal());
    }
    
    private boolean puedeAccederDashboard() {
        return usuarioActual != null && 
               (usuarioActual.puedeAccederDashboard() || 
                usuarioActual.tieneAccesoTotal());
    }
    
    private boolean validarAccesoAPestana(String tabName) {
        switch(tabName) {
            case "Gestión de Productos":
                return puedeAccederGestionProductos();
            case "Control de Ventas":
                return puedeAccederControlVentas();
            case "Reportes":
                return puedeAccederReportes();
            case "Dashboard":
                return puedeAccederDashboard();
            default:
                return true;
        }
    }
    
    private void cambiarAPrimeraPestañaPermitida(JTabbedPane tabbedPane) {
        if (puedeAccederGestionProductos()) {
            tabbedPane.setSelectedIndex(0);
        } else if (puedeAccederControlVentas()) {
            tabbedPane.setSelectedIndex(1);
        } else if (puedeAccederReportes()) {
            tabbedPane.setSelectedIndex(2);
        } else if (puedeAccederDashboard()) {
            tabbedPane.setSelectedIndex(3);
        } else {
            view.mostrarError("No tiene acceso a ningún panel. Contacte al administrador.");
        }
    }
    
    private void configurarPermisosIniciales() {
        JTabbedPane tabbedPane = view.getTabbedPane();
        
        // Verificar cada pestaña y ocultar si no tiene permiso
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String tabName = tabbedPane.getTitleAt(i);
            if (!validarAccesoAPestana(tabName)) {
                tabbedPane.setEnabledAt(i, false);
            }
        }
    }
    
    private void volverAlLogin() {
        new LoginView().setVisible(true);
    }
}