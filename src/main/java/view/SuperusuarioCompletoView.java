package view;

import model.Usuario;
import model.Producto;
import model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperusuarioCompletoView extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Pestaña de Usuarios
    private JTable tableUsuarios;
    private DefaultTableModel tableModelUsuarios;
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> comboRol;
    private JPanel permisosPanel;
    private Map<String, JCheckBox> checkBoxesPermisos;
    
    // Pestaña de Productos
    private JTable tableProductos;
    private DefaultTableModel tableModelProductos;
    
    // Pestaña de Ventas
    private JTable tableVentas;
    private DefaultTableModel tableModelVentas;
    
    // Pestaña de Dashboard - CORREGIDO: Referencias directas
    private JLabel lblTotalUsuarios, lblTotalProductos, lblTotalVentas;
    
    // Botones
    private JButton btnAgregarUsuario, btnEditarUsuario, btnEliminarUsuario, btnActualizarUsuarios;
    private JButton btnCerrarSesion;
    
    private Usuario usuarioActual;
    
    public SuperusuarioCompletoView(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.checkBoxesPermisos = new HashMap<>();
        initializeUI();
        configurarPermisos();
    }
    
    private void initializeUI() {
        setTitle("MINIMARKET - Panel de Superusuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        
        // Panel principal con pestañas
        tabbedPane = new JTabbedPane();
        
        // Crear pestañas
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());
        tabbedPane.addTab("Gestión de Productos", crearPanelProductos());
        tabbedPane.addTab("Control de Ventas", crearPanelVentas());
        tabbedPane.addTab("Dashboard", crearPanelDashboard());
        
        // Panel de header
        JPanel headerPanel = crearHeaderPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));
        
        JLabel lblTitle = new JLabel("⚡ PANEL DE SUPERUSUARIO - CONTROL TOTAL ⚡", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        JLabel lblUsuario = new JLabel("Superusuario: " + usuarioActual.getUsername());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(220, 53, 69));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.add(lblUsuario);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.add(btnCerrarSesion);
        
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo: Formulario y permisos
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        
        // Formulario de usuario
        JPanel formPanel = crearFormularioUsuario();
        
        // Panel de permisos con checkboxes
        JPanel permisosContainer = crearPanelPermisos();
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        leftPanel.add(permisosContainer, BorderLayout.CENTER);
        
        // Panel derecho: Tabla de usuarios
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(crearTablaUsuarios(), BorderLayout.CENTER);
        rightPanel.add(crearPanelBotonesUsuarios(), BorderLayout.SOUTH);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearFormularioUsuario() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        
        formPanel.add(new JLabel("Usuario:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);
        
        formPanel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);
        
        formPanel.add(new JLabel("Rol:"));
        comboRol = new JComboBox<>(new String[]{
            Usuario.ROL_SUPERUSUARIO,
            Usuario.ROL_GERENTE,
            Usuario.ROL_BODEGUERO,
            Usuario.ROL_CAJERO
        });
        comboRol.addActionListener(e -> actualizarPermisosSegunRol());
        formPanel.add(comboRol);
        
        return formPanel;
    }
    
    private JPanel crearPanelPermisos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Permisos Especiales"));
        
        permisosPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane scrollPane = new JScrollPane(permisosPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        // Inicializar checkboxes para todos los permisos
        inicializarCheckboxesPermisos();
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void inicializarCheckboxesPermisos() {
        permisosPanel.removeAll();
        checkBoxesPermisos.clear();
        
        // Crear checkboxes para cada permiso
        if (Usuario.TODOS_LOS_PERMISOS != null) {
            for (String permiso : Usuario.TODOS_LOS_PERMISOS) {
                JCheckBox checkBox = new JCheckBox(permiso);
                checkBox.setFont(new Font("Arial", Font.PLAIN, 11));
                checkBoxesPermisos.put(permiso, checkBox);
                permisosPanel.add(checkBox);
            }
        }
        
        permisosPanel.revalidate();
        permisosPanel.repaint();
    }
    
    private JPanel crearTablaUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Usuarios Registrados"));
        
        String[] columnNames = {"Usuario", "Rol", "Permisos", "Estado"};
        tableModelUsuarios = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableUsuarios = new JTable(tableModelUsuarios);
        tableUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarUsuarioSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesUsuarios() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnAgregarUsuario = crearBoton("Agregar Usuario", new Color(40, 167, 69));
        btnEditarUsuario = crearBoton("Editar Usuario", new Color(255, 193, 7));
        btnEliminarUsuario = crearBoton("Eliminar Usuario", new Color(220, 53, 69));
        btnActualizarUsuarios = crearBoton("Actualizar Lista", new Color(0, 123, 255));
        
        panel.add(btnAgregarUsuario);
        panel.add(btnEditarUsuario);
        panel.add(btnEliminarUsuario);
        panel.add(btnActualizarUsuarios);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(150, 35));
        return boton;
    }
    
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Código", "Nombre", "Categoría", "P. Compra", "P. Venta", "Stock", "Estado"};
        tableModelProductos = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProductos = new JTable(tableModelProductos);
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"ID Venta", "Fecha", "Usuario", "Productos", "Total", "Método Pago"};
        tableModelVentas = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVentas = new JTable(tableModelVentas);
        JScrollPane scrollPane = new JScrollPane(tableVentas);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 240));
        
        // Tarjetas de estadísticas - CORREGIDO: Sin cast peligroso
        JPanel cardUsuarios = crearTarjetaDashboard("👥 TOTAL USUARIOS", "0", new Color(40, 167, 69));
        lblTotalUsuarios = obtenerLabelDeTarjeta(cardUsuarios);
        
        JPanel cardProductos = crearTarjetaDashboard("📦 TOTAL PRODUCTOS", "0", new Color(0, 123, 255));
        lblTotalProductos = obtenerLabelDeTarjeta(cardProductos);
        
        JPanel cardVentas = crearTarjetaDashboard("💰 TOTAL VENTAS", "S/. 0.00", new Color(255, 193, 7));
        lblTotalVentas = obtenerLabelDeTarjeta(cardVentas);
        
        JPanel cardActividad = crearTarjetaDashboard("📊 ACTIVIDAD RECIENTE", "Ver detalles", new Color(108, 117, 125));
        
        panel.add(cardUsuarios);
        panel.add(cardProductos);
        panel.add(cardVentas);
        panel.add(cardActividad);
        
        return panel;
    }
    
    // NUEVO MÉTODO: Obtener label sin cast peligroso
    private JLabel obtenerLabelDeTarjeta(JPanel tarjeta) {
        Component centerComponent = ((BorderLayout)tarjeta.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent instanceof JLabel) {
            return (JLabel) centerComponent;
        }
        return null;
    }
    
    private JPanel crearTarjetaDashboard(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(color);
        
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(Color.BLACK);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        
        return card;
    }
    
    // Métodos públicos para el controlador
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(((JPasswordField)txtPassword).getPassword()).trim(); }
    public String getRol() { return (String) comboRol.getSelectedItem(); }
    
    public java.util.List<String> getPermisosSeleccionados() {
        java.util.List<String> permisos = new java.util.ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : checkBoxesPermisos.entrySet()) {
            if (entry.getValue().isSelected()) {
                permisos.add(entry.getKey());
            }
        }
        return permisos;
    }
    
    public void setPermisosSeleccionados(java.util.List<String> permisos) {
        // Limpiar todas las selecciones
        for (JCheckBox checkBox : checkBoxesPermisos.values()) {
            checkBox.setSelected(false);
        }
        
        // Seleccionar los permisos especificados
        if (permisos != null) {
            for (String permiso : permisos) {
                JCheckBox checkBox = checkBoxesPermisos.get(permiso);
                if (checkBox != null) {
                    checkBox.setSelected(true);
                }
            }
        }
    }
    
    public void limpiarFormularioUsuario() {
        txtUsername.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
        setPermisosSeleccionados(null);
    }
    
    public void cargarUsuarioSeleccionado() {
        String username = getUsuarioSeleccionado();
        if (username != null) {
            txtUsername.setText(username);
            txtPassword.setText(""); // No mostrar contraseña
        }
    }
    
    public String getUsuarioSeleccionado() {
        int row = tableUsuarios.getSelectedRow();
        return row != -1 ? (String) tableModelUsuarios.getValueAt(row, 0) : null;
    }
    
    public void actualizarTablaUsuarios(List<Usuario> usuarios) {
        tableModelUsuarios.setRowCount(0);
        for (Usuario usuario : usuarios) {
            String permisos = String.join(", ", usuario.getPermisosEspeciales());
            if (permisos.length() > 50) {
                permisos = permisos.substring(0, 47) + "...";
            }
            
            Object[] row = {
                usuario.getUsername(),
                usuario.getRol(),
                permisos,
                "ACTIVO"
            };
            tableModelUsuarios.addRow(row);
        }
    }
    
    public void actualizarTablaProductos(List<Producto> productos) {
        tableModelProductos.setRowCount(0);
        for (Producto producto : productos) {
            Object[] row = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.getEstado()
            };
            tableModelProductos.addRow(row);
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
                venta.getTotal(),
                venta.getMetodoPago()
            };
            tableModelVentas.addRow(row);
        }
    }
    
    public void actualizarDashboard(int totalUsuarios, int totalProductos, double totalVentas) {
        if (lblTotalUsuarios != null) lblTotalUsuarios.setText(String.valueOf(totalUsuarios));
        if (lblTotalProductos != null) lblTotalProductos.setText(String.valueOf(totalProductos));
        if (lblTotalVentas != null) lblTotalVentas.setText(String.format("S/. %.2f", totalVentas));
    }
    
    private void actualizarPermisosSegunRol() {
        String rol = getRol();
        
        // Deshabilitar checkboxes según el rol
        for (Map.Entry<String, JCheckBox> entry : checkBoxesPermisos.entrySet()) {
            String permiso = entry.getKey();
            JCheckBox checkBox = entry.getValue();
            
            // Habilitar todos los checkboxes para superusuario
            if (Usuario.ROL_SUPERUSUARIO.equals(rol)) {
                checkBox.setEnabled(true);
            } else {
                // Validar permisos según rol
                checkBox.setEnabled(esPermisoValidoParaRol(permiso, rol));
            }
        }
    }
    
    private boolean esPermisoValidoParaRol(String permiso, String rol) {
        // Definir qué permisos son válidos para cada rol
        switch (rol) {
            case Usuario.ROL_GERENTE:
                return permiso.startsWith("ACCEDER_") || 
                       permiso.startsWith("VER_") ||
                       permiso.equals("EXPORTAR_DATOS") ||
                       permiso.equals("GESTIONAR_PRODUCTOS");
            case Usuario.ROL_BODEGUERO:
                return permiso.contains("PRODUCTOS") || 
                       permiso.equals("AJUSTAR_STOCK") ||
                       permiso.equals("GESTIONAR_CATEGORIAS");
            case Usuario.ROL_CAJERO:
                return permiso.contains("CARRITO") || 
                       permiso.contains("VENTA") ||
                       permiso.equals("PROCESAR_VENTAS") ||
                       permiso.equals("GENERAR_COMPROBANTES");
            default:
                return true; // Superusuario puede todo
        }
    }
    
    private void configurarPermisos() {
        // El superusuario siempre tiene acceso total
        setTitle("MINIMARKET - Panel de Superusuario (" + usuarioActual.getUsername() + ")");
    }
    
    // Listeners
    public void addAgregarUsuarioListener(ActionListener listener) {
        btnAgregarUsuario.addActionListener(listener);
    }
    
    public void addEditarUsuarioListener(ActionListener listener) {
        btnEditarUsuario.addActionListener(listener);
    }
    
    public void addEliminarUsuarioListener(ActionListener listener) {
        btnEliminarUsuario.addActionListener(listener);
    }
    
    public void addActualizarUsuariosListener(ActionListener listener) {
        btnActualizarUsuarios.addActionListener(listener);
    }
    
    public void addCerrarSesionListener(ActionListener listener) {
        btnCerrarSesion.addActionListener(listener);
    }
    
    // Métodos de utilidad
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public boolean confirmarEliminacion(String username) {
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el usuario: " + username + "?\n\n" +
            "Esta acción no se puede deshacer.", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    public void setUsuarioParaEditar(Usuario usuario) {
        if (usuario != null) {
            txtUsername.setText(usuario.getUsername());
            txtPassword.setText(""); // No mostrar contraseña real
            comboRol.setSelectedItem(usuario.getRol());
            setPermisosSeleccionados(usuario.getPermisosEspeciales());
        }
    }
}