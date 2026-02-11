package view;

import model.Producto;
import model.Usuario;
import model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GerenteView extends JFrame {
    private Usuario usuarioActual;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    
    // Pestaña Productos
    private JTable tableProductos;
    private DefaultTableModel tableModelProductos;
    private JComboBox<String> comboCategorias;
    
    // Pestaña Ventas
    private JTable tableVentas;
    private DefaultTableModel tableModelVentas;
    
    // Pestaña Reportes
    private JTextArea txtReportes;
    private JComboBox<String> comboReportes;
    private JTextField txtFechaInicio, txtFechaFin;
    
    // Pestaña Dashboard
    private JTextArea txtDashboard;
    
    // Botones
    private JButton btnBuscarProductos, btnReporteStock;
    private JButton btnFiltrarVentas, btnReporteVentas;
    private JButton btnGenerarReporte;
    private JButton btnCerrarSesion;
    
    public GerenteView(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initializeUI();
        configurarPermisosUI();
    }
    
    private void initializeUI() {
        setTitle("MINIMARKET - Panel Gerencial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📦 Gestión de Productos", crearPanelProductos());
        tabbedPane.addTab("💰 Control de Ventas", crearPanelVentas());
        tabbedPane.addTab("📊 Reportes", crearPanelReportes());
        tabbedPane.addTab("📈 Dashboard", crearPanelDashboard());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel crearHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        JLabel lblTitle = new JLabel("👨‍💼 PANEL GERENCIAL", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        JLabel lblUsuario = new JLabel("Gerente: " + usuarioActual.getUsername());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnCerrarSesion = new JButton("🚪 Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(220, 53, 69));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 12));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.add(lblUsuario);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.add(btnCerrarSesion);
        
        panel.add(lblTitle, BorderLayout.CENTER);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Filtros de Productos"));
        
        searchPanel.add(new JLabel("Categoría:"));
        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("TODAS");
        comboCategorias.setPreferredSize(new Dimension(150, 25));
        searchPanel.add(comboCategorias);
        
        btnBuscarProductos = new JButton("🔍 Buscar Productos");
        btnBuscarProductos.setBackground(new Color(0, 102, 204));
        btnBuscarProductos.setForeground(Color.WHITE);
        btnBuscarProductos.setFocusPainted(false);
        searchPanel.add(btnBuscarProductos);
        
        btnReporteStock = new JButton("📋 Reporte Stock Crítico");
        btnReporteStock.setBackground(new Color(40, 167, 69));
        btnReporteStock.setForeground(Color.WHITE);
        btnReporteStock.setFocusPainted(false);
        searchPanel.add(btnReporteStock);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabla de productos
        String[] columnNames = {"Código", "Nombre", "Categoría", "P. Compra", "P. Venta", "Stock", "Stock Mín", "Estado"};
        tableModelProductos = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProductos = new JTable(tableModelProductos);
        tableProductos.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtrar Ventas"));
        
        filterPanel.add(new JLabel("Fecha inicio:"));
        txtFechaInicio = new JTextField("2025-01-01", 12);
        filterPanel.add(txtFechaInicio);
        
        filterPanel.add(new JLabel("Fecha fin:"));
        txtFechaFin = new JTextField("2025-12-31", 12);
        filterPanel.add(txtFechaFin);
        
        btnFiltrarVentas = new JButton("🔍 Filtrar Ventas");
        btnFiltrarVentas.setBackground(new Color(0, 102, 204));
        btnFiltrarVentas.setForeground(Color.WHITE);
        btnFiltrarVentas.setFocusPainted(false);
        filterPanel.add(btnFiltrarVentas);
        
        btnReporteVentas = new JButton("📊 Reporte de Ventas");
        btnReporteVentas.setBackground(new Color(40, 167, 69));
        btnReporteVentas.setForeground(Color.WHITE);
        btnReporteVentas.setFocusPainted(false);
        filterPanel.add(btnReporteVentas);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Tabla de ventas
        String[] columnNames = {"ID Venta", "Fecha", "Usuario", "Productos", "Total", "Método Pago"};
        tableModelVentas = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVentas = new JTable(tableModelVentas);
        tableVentas.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableVentas);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de selección
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Generar Reportes"));
        
        selectionPanel.add(new JLabel("Tipo de Reporte:"));
        comboReportes = new JComboBox<>(new String[]{
            "Ventas por Período",
            "Ventas por Categoría",
            "Stock Crítico",
            "Productos Más Vendidos",
            "Rentabilidad por Producto"
        });
        comboReportes.setPreferredSize(new Dimension(200, 25));
        selectionPanel.add(comboReportes);
        
        btnGenerarReporte = new JButton("📄 Generar Reporte");
        btnGenerarReporte.setBackground(new Color(0, 102, 204));
        btnGenerarReporte.setForeground(Color.WHITE);
        btnGenerarReporte.setFocusPainted(false);
        selectionPanel.add(btnGenerarReporte);
        
        panel.add(selectionPanel, BorderLayout.NORTH);
        
        // Área de texto para reportes
        txtReportes = new JTextArea();
        txtReportes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReportes.setEditable(false);
        txtReportes.setBackground(new Color(248, 249, 250));
        JScrollPane scrollPane = new JScrollPane(txtReportes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reporte Generado"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Área de texto para dashboard
        txtDashboard = new JTextArea();
        txtDashboard.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtDashboard.setEditable(false);
        txtDashboard.setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(txtDashboard);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📈 Dashboard Gerencial"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Método para configurar permisos en la UI
    private void configurarPermisosUI() {
        if (usuarioActual == null) {
            deshabilitarTodosBotones();
            return;
        }
        
        System.out.println("=== INFORMACIÓN DE USUARIO GERENTE ===");
        System.out.println("Usuario: " + usuarioActual.getUsername());
        System.out.println("Rol: " + usuarioActual.getRol());
        System.out.println("Permisos: " + usuarioActual.getPermisosEspeciales());
        System.out.println("Puede acceder gestión productos: " + usuarioActual.puedeAccederGestionProductos());
        System.out.println("Puede acceder control ventas: " + usuarioActual.puedeAccederControlVentas());
        System.out.println("Puede acceder reportes: " + usuarioActual.puedeAccederReportes());
        System.out.println("Puede ver reportes: " + usuarioActual.puedeVerReportes());
        System.out.println("Puede acceder dashboard: " + usuarioActual.puedeAccederDashboard());
        System.out.println("======================================");
        
        // Habilitar/deshabilitar según permisos
        btnBuscarProductos.setEnabled(usuarioActual.puedeAccederGestionProductos());
        btnReporteStock.setEnabled(usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes());
        btnFiltrarVentas.setEnabled(usuarioActual.puedeAccederControlVentas());
        btnReporteVentas.setEnabled(usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes());
        btnGenerarReporte.setEnabled(usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes());
        
        // Configurar pestañas según permisos
        boolean accesoProductos = usuarioActual.puedeAccederGestionProductos() || usuarioActual.tieneAccesoTotal();
        boolean accesoVentas = usuarioActual.puedeAccederControlVentas() || usuarioActual.tieneAccesoTotal();
        boolean accesoReportes = usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes() || usuarioActual.tieneAccesoTotal();
        boolean accesoDashboard = usuarioActual.puedeAccederDashboard() || usuarioActual.tieneAccesoTotal();
        
        // Habilitar/deshabilitar pestañas
        tabbedPane.setEnabledAt(0, accesoProductos);
        tabbedPane.setEnabledAt(1, accesoVentas);
        tabbedPane.setEnabledAt(2, accesoReportes);
        tabbedPane.setEnabledAt(3, accesoDashboard);
        
        // Si no tiene acceso a ninguna pestaña, mostrar error
        if (!accesoProductos && !accesoVentas && !accesoReportes && !accesoDashboard) {
            JOptionPane.showMessageDialog(this,
                "⚠ Este usuario no tiene permisos para acceder a ninguna sección.\n" +
                "Contacte al administrador para asignar permisos.\n\n" +
                "Permisos actuales: " + usuarioActual.getPermisosEspeciales(),
                "Sin Permisos",
                JOptionPane.WARNING_MESSAGE);
        }
        
        // Agregar tooltips
        actualizarTooltips();
        
        // Mostrar advertencia si tiene pocos permisos
        if (usuarioActual.esGerente() && 
            !usuarioActual.puedeAccederGestionProductos() && 
            !usuarioActual.puedeAccederControlVentas() && 
            !usuarioActual.puedeAccederReportes()) {
            
            JOptionPane.showMessageDialog(this,
                "⚠ Este gerente tiene permisos limitados.\n" +
                "Solo podrá acceder a las funciones para las que tiene permisos específicos.",
                "Permisos Limitados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deshabilitarTodosBotones() {
        btnBuscarProductos.setEnabled(false);
        btnReporteStock.setEnabled(false);
        btnFiltrarVentas.setEnabled(false);
        btnReporteVentas.setEnabled(false);
        btnGenerarReporte.setEnabled(false);
        tabbedPane.setEnabledAt(0, false);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);
    }
    
    private void actualizarTooltips() {
        btnBuscarProductos.setToolTipText(usuarioActual.puedeAccederGestionProductos() ? 
            "Buscar productos por categoría" : 
            "No tiene permiso para acceder a gestión de productos");
        
        btnReporteStock.setToolTipText((usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes()) ? 
            "Generar reporte de stock crítico" : 
            "No tiene permiso para generar reportes");
        
        btnFiltrarVentas.setToolTipText(usuarioActual.puedeAccederControlVentas() ? 
            "Filtrar ventas por fecha" : 
            "No tiene permiso para acceder a control de ventas");
        
        btnReporteVentas.setToolTipText((usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes()) ? 
            "Generar reporte de ventas" : 
            "No tiene permiso para generar reportes");
        
        btnGenerarReporte.setToolTipText((usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes()) ? 
            "Generar reporte seleccionado" : 
            "No tiene permiso para generar reportes");
        
        // Tooltips para pestañas
        tabbedPane.setToolTipTextAt(0, accesoProductosTooltip());
        tabbedPane.setToolTipTextAt(1, accesoVentasTooltip());
        tabbedPane.setToolTipTextAt(2, accesoReportesTooltip());
        tabbedPane.setToolTipTextAt(3, accesoDashboardTooltip());
    }
    
    private String accesoProductosTooltip() {
        return usuarioActual.puedeAccederGestionProductos() ? 
            "Gestión de productos e inventario" : 
            "No tiene permiso para acceder a gestión de productos";
    }
    
    private String accesoVentasTooltip() {
        return usuarioActual.puedeAccederControlVentas() ? 
            "Control y seguimiento de ventas" : 
            "No tiene permiso para acceder a control de ventas";
    }
    
    private String accesoReportesTooltip() {
        return (usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes()) ? 
            "Generación de reportes gerenciales" : 
            "No tiene permiso para acceder a reportes";
    }
    
    private String accesoDashboardTooltip() {
        return usuarioActual.puedeAccederDashboard() ? 
            "Dashboard con métricas gerenciales" : 
            "No tiene permiso para acceder al dashboard";
    }
    
    // ========== MÉTODOS PARA ACTUALIZAR DATOS ==========
    
    public void actualizarTablaProductos(List<Producto> productos) {
        tableModelProductos.setRowCount(0);
        for (Producto producto : productos) {
            Object[] row = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCategoria(),
                String.format("S/. %.2f", producto.getPrecioCompra()),
                String.format("S/. %.2f", producto.getPrecioVenta()),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getEstado()
            };
            tableModelProductos.addRow(row);
        }
    }
    
    public void actualizarComboCategorias(List<String> categorias) {
        comboCategorias.removeAllItems();
        comboCategorias.addItem("TODAS");
        for (String categoria : categorias) {
            comboCategorias.addItem(categoria);
        }
    }
    
    public void actualizarTablaVentas(List<Venta> ventas) {
        tableModelVentas.setRowCount(0);
        for (Venta venta : ventas) {
            Object[] row = {
                venta.getId(),
                venta.getFecha(),
                venta.getUsuario(),
                venta.getProductos().size() + " productos",
                String.format("S/. %.2f", venta.getTotal()),
                venta.getMetodoPago()
            };
            tableModelVentas.addRow(row);
        }
    }
    
    public void mostrarReporte(String reporte) {
        txtReportes.setText(reporte);
        // Cambiar a la pestaña de reportes
        tabbedPane.setSelectedIndex(2);
    }
    
    public void actualizarDashboard(double totalVentas, String productoMasVendido, 
                                   String categoriaMasVendida, int stockCritico) {
        String dashboardContent = String.format(
            "═══════════════════════════════════════════════════════════════\n" +
            "                       📊 DASHBOARD GERENCIAL                       \n" +
            "═══════════════════════════════════════════════════════════════\n\n" +
            "💰 INGRESOS TOTALES:\n" +
            "   • Total Ventas: S/. %.2f\n\n" +
            "🏆 PRODUCTOS DESTACADOS:\n" +
            "   • Producto Más Vendido: %s\n" +
            "   • Categoría Más Vendida: %s\n\n" +
            "⚠ ALERTAS DE INVENTARIO:\n" +
            "   • Productos con Stock Crítico: %d\n\n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "          Última actualización: %s\n" +
            "═══════════════════════════════════════════════════════════════",
            totalVentas, productoMasVendido, categoriaMasVendida, stockCritico,
            new java.util.Date().toString()
        );
        
        txtDashboard.setText(dashboardContent);
        // Cambiar a la pestaña de dashboard
        tabbedPane.setSelectedIndex(3);
    }
    
    // ========== GETTERS PARA DATOS ==========
    
    public String getCategoriaSeleccionada() {
        return (String) comboCategorias.getSelectedItem();
    }
    
    public String getTipoReporte() {
        return (String) comboReportes.getSelectedItem();
    }
    
    public String getFechaInicio() {
        return txtFechaInicio.getText().trim();
    }
    
    public String getFechaFin() {
        return txtFechaFin.getText().trim();
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    // ========== LISTENERS ==========
    
    public void addBuscarProductosListener(ActionListener listener) {
        btnBuscarProductos.addActionListener(listener);
    }
    
    public void addReporteStockListener(ActionListener listener) {
        btnReporteStock.addActionListener(listener);
    }
    
    public void addFiltrarVentasListener(ActionListener listener) {
        btnFiltrarVentas.addActionListener(listener);
    }
    
    public void addReporteVentasListener(ActionListener listener) {
        btnReporteVentas.addActionListener(listener);
    }
    
    public void addGenerarReporteListener(ActionListener listener) {
        btnGenerarReporte.addActionListener(listener);
    }
    
    public void addCerrarSesionListener(ActionListener listener) {
        btnCerrarSesion.addActionListener(listener);
    }
    
    // ========== MÉTODOS PARA MOSTRAR MENSAJES ==========
    
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    // ========== MÉTODOS ADICIONALES UTILES ==========
    
    public void limpiarReportes() {
        txtReportes.setText("");
    }
    
    public void limpiarDashboard() {
        txtDashboard.setText("");
    }
    
    public void mostrarMensajeStatus(String mensaje) {
        System.out.println("📢 GerenteView: " + mensaje);
    }
    
    // Método para verificar si se puede realizar una acción
    public boolean verificarPermisoAccion(String accion, boolean mostrarMensaje) {
        boolean tienePermiso = false;
        String mensajeError = "";
        
        switch(accion) {
            case "BUSCAR_PRODUCTOS":
                tienePermiso = usuarioActual.puedeAccederGestionProductos();
                mensajeError = "No tiene permiso para buscar productos";
                break;
            case "REPORTE_STOCK":
                tienePermiso = usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes();
                mensajeError = "No tiene permiso para generar reportes de stock";
                break;
            case "FILTRAR_VENTAS":
                tienePermiso = usuarioActual.puedeAccederControlVentas();
                mensajeError = "No tiene permiso para filtrar ventas";
                break;
            case "REPORTE_VENTAS":
                tienePermiso = usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes();
                mensajeError = "No tiene permiso para generar reportes de ventas";
                break;
            case "GENERAR_REPORTE":
                tienePermiso = usuarioActual.puedeAccederReportes() || usuarioActual.puedeVerReportes();
                mensajeError = "No tiene permiso para generar reportes";
                break;
            default:
                mensajeError = "Acción no reconocida: " + accion;
        }
        
        if (!tienePermiso && mostrarMensaje) {
            mostrarError(mensajeError + "\n\nContacte al administrador para solicitar este permiso.");
        }
        
        return tienePermiso;
    }
}