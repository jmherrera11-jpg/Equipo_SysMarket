package view;

import model.Producto;
import model.Usuario;
import model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CajeroView extends JFrame {
    private JTable tableProductos;
    private JTable tableCarrito;
    private DefaultTableModel tableModelProductos;
    private DefaultTableModel tableModelCarrito;
    private JComboBox<String> comboCategorias;
    private JTextField txtBuscar, txtCantidad;
    private JLabel lblTotal;
    private JButton btnAgregarCarrito, btnQuitarCarrito, btnRealizarVenta, btnLimpiarCarrito, btnCerrarSesion;
    private JButton btnBuscar;
    private List<Producto> carrito;
    private double totalVenta;
    private Usuario usuarioActual;
    
    public CajeroView(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.carrito = new ArrayList<>();
        this.totalVenta = 0.0;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("MINIMARKET - Punto de Venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel lblTitle = new JLabel("🛒 PUNTO DE VENTA - CAJERO", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Panel central dividido
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Panel izquierdo: Productos
        centerPanel.add(crearPanelProductos());
        
        // Panel derecho: Carrito
        centerPanel.add(crearPanelCarrito());
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior: Botones
        mainPanel.add(crearPanelBusqueda(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Buscar Productos"));
        
        panel.add(new JLabel("Categoría:"));
        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("TODAS");
        panel.add(comboCategorias);
        
        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        panel.add(txtBuscar);
        
        btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setBackground(new Color(0, 102, 204));
        btnBuscar.setForeground(Color.WHITE);
        panel.add(btnBuscar);
        
        return panel;
    }
    
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📦 Productos Disponibles"));
        
        // Tabla de productos
        String[] columnNames = {"Código", "Nombre", "Categoría", "Precio", "Stock", "Estado"};
        tableModelProductos = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProductos = new JTable(tableModelProductos);
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        
        // Panel para agregar al carrito
        JPanel agregarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        agregarPanel.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
        txtCantidad.setText("1");
        agregarPanel.add(txtCantidad);
        
        btnAgregarCarrito = new JButton("➕ Agregar al Carrito");
        btnAgregarCarrito.setBackground(new Color(40, 167, 69));
        btnAgregarCarrito.setForeground(Color.WHITE);
        agregarPanel.add(btnAgregarCarrito);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(agregarPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🛍️ Carrito de Compras"));
        
        // Tabla del carrito
        String[] columnNames = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        tableModelCarrito = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCarrito = new JTable(tableModelCarrito);
        JScrollPane scrollPane = new JScrollPane(tableCarrito);
        
        // Panel inferior del carrito
        JPanel carritoActions = new JPanel(new BorderLayout(5, 5));
        
        // Panel del total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        totalPanel.add(new JLabel("Total:"));
        lblTotal = new JLabel("S/. 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(220, 53, 69));
        totalPanel.add(lblTotal);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnQuitarCarrito = new JButton("➖ Quitar del Carrito");
        btnQuitarCarrito.setBackground(new Color(255, 193, 7));
        
        btnLimpiarCarrito = new JButton("🗑️ Limpiar Carrito");
        btnLimpiarCarrito.setBackground(new Color(108, 117, 125));
        btnLimpiarCarrito.setForeground(Color.WHITE);
        
        btnRealizarVenta = new JButton("💰 Realizar Venta");
        btnRealizarVenta.setBackground(new Color(40, 167, 69));
        btnRealizarVenta.setForeground(Color.WHITE);
        btnRealizarVenta.setFont(new Font("Arial", Font.BOLD, 12));
        
        btnCerrarSesion = new JButton("🚪 Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(220, 53, 69));
        btnCerrarSesion.setForeground(Color.WHITE);
        
        buttonsPanel.add(btnQuitarCarrito);
        buttonsPanel.add(btnLimpiarCarrito);
        buttonsPanel.add(btnRealizarVenta);
        buttonsPanel.add(btnCerrarSesion);
        
        carritoActions.add(totalPanel, BorderLayout.NORTH);
        carritoActions.add(buttonsPanel, BorderLayout.SOUTH);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(carritoActions, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Getters
    public String getCategoriaSeleccionada() {
        return (String) comboCategorias.getSelectedItem();
    }
    
    public String getTextoBusqueda() {
        return txtBuscar.getText().trim();
    }
    
    public int getCantidad() {
        try {
            return Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    
    public int getFilaProductoSeleccionada() {
        return tableProductos.getSelectedRow();
    }
    
    public int getFilaCarritoSeleccionada() {
        return tableCarrito.getSelectedRow();
    }
    
    public String getCodigoProductoSeleccionado() {
        int row = getFilaProductoSeleccionada();
        return (row != -1) ? (String) tableModelProductos.getValueAt(row, 0) : null;
    }
    
    public List<Producto> getCarrito() {
        return carrito;
    }
    
    public double getTotalVenta() {
        return totalVenta;
    }
    
    // Setters
    public void setCantidad(int cantidad) {
        txtCantidad.setText(String.valueOf(cantidad));
    }
    
    // Métodos para actualizar datos
    public void actualizarTablaProductos(List<Producto> productos) {
        tableModelProductos.setRowCount(0);
        for (Producto producto : productos) {
            Object[] row = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCategoria(),
                String.format("S/. %.2f", producto.getPrecioVenta()),
                producto.getStock(),
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
    
    public void agregarAlCarrito(Producto producto, int cantidad) {
        // Crear una copia del producto para el carrito
        Producto productoCarrito = new Producto(
            producto.getCodigo(),
            producto.getNombre(),
            producto.getCategoria(),
            producto.getPrecioCompra(),
            producto.getPrecioVenta(),
            cantidad, // Usamos stock para representar cantidad en el carrito
            0
        );
        
        carrito.add(productoCarrito);
        actualizarCarrito();
    }
    
    public void quitarDelCarrito(int index) {
        if (index >= 0 && index < carrito.size()) {
            carrito.remove(index);
            actualizarCarrito();
        }
    }
    
    public void limpiarCarrito() {
        carrito.clear();
        actualizarCarrito();
    }
    
    private void actualizarCarrito() {
        tableModelCarrito.setRowCount(0);
        totalVenta = 0.0;
        
        for (Producto producto : carrito) {
            double subtotal = producto.getPrecioVenta() * producto.getStock(); // Stock = cantidad
            totalVenta += subtotal;
            
            Object[] row = {
                producto.getNombre(),
                producto.getStock(),
                String.format("S/. %.2f", producto.getPrecioVenta()),
                String.format("S/. %.2f", subtotal)
            };
            tableModelCarrito.addRow(row);
        }
        
        lblTotal.setText(String.format("S/. %.2f", totalVenta));
    }
    
    // Listeners
    public void addBuscarListener(ActionListener listener) {
        btnBuscar.addActionListener(listener);
        comboCategorias.addActionListener(listener);
    }
    
    public void addAgregarCarritoListener(ActionListener listener) {
        btnAgregarCarrito.addActionListener(listener);
    }
    
    public void addQuitarCarritoListener(ActionListener listener) {
        btnQuitarCarrito.addActionListener(listener);
    }
    
    public void addLimpiarCarritoListener(ActionListener listener) {
        btnLimpiarCarrito.addActionListener(listener);
    }
    
    public void addRealizarVentaListener(ActionListener listener) {
        btnRealizarVenta.addActionListener(listener);
    }
    
    public void addCerrarSesionListener(ActionListener listener) {
        btnCerrarSesion.addActionListener(listener);
    }
    
    // Métodos para mostrar mensajes
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public String mostrarDialogoMetodoPago() {
        String[] opciones = {"EFECTIVO", "TARJETA", "TRANSFERENCIA", "YAPE/PLIN"};
        return (String) JOptionPane.showInputDialog(
            this,
            "Seleccione método de pago:",
            "Método de Pago",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
    }
    
    public void mostrarResumenVenta(Venta venta) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("═══════════════════════════════════\n");
        resumen.append("        COMPROBANTE DE VENTA       \n");
        resumen.append("═══════════════════════════════════\n");
        resumen.append("Número: ").append(venta.getId()).append("\n");
        resumen.append("Fecha: ").append(venta.getFecha()).append("\n");
        resumen.append("Cajero: ").append(venta.getUsuario()).append("\n");
        resumen.append("═══════════════════════════════════\n");
        
        for (Producto producto : venta.getProductos()) {
            resumen.append(String.format("%-20s x%d  S/. %7.2f\n", 
                producto.getNombre(), 
                producto.getStock(), 
                producto.getPrecioVenta()));
        }
        
        resumen.append("═══════════════════════════════════\n");
        resumen.append(String.format("TOTAL:              S/. %10.2f\n", venta.getTotal()));
        resumen.append("Método pago: ").append(venta.getMetodoPago()).append("\n");
        resumen.append("═══════════════════════════════════\n");
        resumen.append("       ¡GRACIAS POR SU COMPRA!      \n");
        resumen.append("═══════════════════════════════════\n");
        
        JTextArea textArea = new JTextArea(resumen.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Venta Realizada", JOptionPane.INFORMATION_MESSAGE);
    }
}