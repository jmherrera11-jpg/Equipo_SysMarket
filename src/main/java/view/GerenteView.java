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
    
    // ========== MÉTODOS ADICIONALES UTILES ==========
    
    public void limpiarReportes() {
        txtReportes.setText("");
    }
    
    public void limpiarDashboard() {
        txtDashboard.setText("");
    }
    
    public void mostrarMensajeStatus(String mensaje) {
        // Puedes agregar una barra de estado si quieres
        System.out.println("📢 " + mensaje);
    }
}