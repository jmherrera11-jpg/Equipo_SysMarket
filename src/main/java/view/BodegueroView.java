package view;

import model.Producto;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class BodegueroView extends JFrame {
    private JTable tableProductos;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboCategorias;
    private JTextField txtCodigo, txtNombre, txtPrecioCompra, txtPrecioVenta, txtStock, txtStockMinimo;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar, btnBuscar, btnLimpiar, btnCerrarSesion;
    private JComboBox<String> comboCatForm;
    private Usuario usuarioActual;
    
    
    public BodegueroView(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initializeUI();
        configurarPermisos();
    }
    
    private void initializeUI() {
        setTitle("MINIMARKET - Gestión de Bodega");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel lblTitle = new JLabel("GESTIÓN DE INVENTARIO - KARDEX", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar"));
        
        searchPanel.add(new JLabel("Categoría:"));
        comboCategorias = new JComboBox<>();
        comboCategorias.addItem("TODAS");
        searchPanel.add(comboCategorias);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(0, 102, 204));
        btnBuscar.setForeground(Color.BLACK);
        searchPanel.add(btnBuscar);
        
        btnLimpiar = new JButton("Limpiar Filtro");
        btnLimpiar.setBackground(new Color(102, 102, 102));
        btnLimpiar.setForeground(Color.BLACK);
        searchPanel.add(btnLimpiar);
        
        // Panel de formulario para productos
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Productos"));
        
        formPanel.add(new JLabel("Código:"));
        txtCodigo = new JTextField();
        formPanel.add(txtCodigo);
        
        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);
        
        formPanel.add(new JLabel("Precio Compra:"));
        txtPrecioCompra = new JTextField();
        formPanel.add(txtPrecioCompra);
        
        formPanel.add(new JLabel("Precio Venta:"));
        txtPrecioVenta = new JTextField();
        formPanel.add(txtPrecioVenta);
        
        formPanel.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        formPanel.add(txtStock);
        
        formPanel.add(new JLabel("Stock Mínimo:"));
        txtStockMinimo = new JTextField();
        formPanel.add(txtStockMinimo);
        
        formPanel.add(new JLabel("Categoría:"));
        comboCatForm = new JComboBox<>();
        formPanel.add(comboCatForm);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        
        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 193, 7));
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(0, 123, 255));
        btnActualizar.setForeground(Color.BLACK);
        
        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.setBackground(new Color(108, 117, 125));
        btnReporte.setForeground(Color.BLACK);
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(108, 117, 125));
        btnCerrarSesion.setForeground(Color.BLACK);
        
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnReporte);
        buttonPanel.add(btnCerrarSesion);
        
        // Tabla de productos
        String[] columnNames = {"Código", "Nombre", "Categoría", "Precio Compra", 
                               "Precio Venta", "Stock", "Stock Mínimo", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProductos = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        
        // Agregar componentes al panel principal
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(scrollPane, BorderLayout.SOUTH);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // Getters para los datos del formulario - CORREGIDO
    public String getCodigo() { return txtCodigo.getText().trim(); }
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getCategoria() { return (String) comboCatForm.getSelectedItem(); }
    public Usuario getUsuarioActual() {return usuarioActual;}
    
    public double getPrecioCompra() { 
        try {
            String texto = txtPrecioCompra.getText().trim();
            return texto.isEmpty() ? 0.0 : Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public double getPrecioVenta() { 
        try {
            String texto = txtPrecioVenta.getText().trim();
            return texto.isEmpty() ? 0.0 : Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public int getStock() { 
        try {
            String texto = txtStock.getText().trim();
            return texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int getStockMinimo() { 
        try {
            String texto = txtStockMinimo.getText().trim();
            return texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    // Setters para el formulario
    public void setCodigo(String codigo) { txtCodigo.setText(codigo); }
    public void setNombre(String nombre) { txtNombre.setText(nombre); }
    public void setPrecioCompra(double precio) { txtPrecioCompra.setText(String.valueOf(precio)); }
    public void setPrecioVenta(double precio) { txtPrecioVenta.setText(String.valueOf(precio)); }
    public void setStock(int stock) { txtStock.setText(String.valueOf(stock)); }
    public void setStockMinimo(int stockMinimo) { txtStockMinimo.setText(String.valueOf(stockMinimo)); }
    public void setCategoria(String categoria) { comboCatForm.setSelectedItem(categoria); }
    
    // Métodos para actualizar combos
    public void actualizarComboCategorias(List<String> categorias) {
        comboCategorias.removeAllItems();
        comboCategorias.addItem("TODAS");
        
        comboCatForm.removeAllItems(); 
        
        for (String categoria : categorias) {
            comboCategorias.addItem(categoria);
            comboCatForm.addItem(categoria);
        }
    }
    
    
    // Métodos para la tabla
    public void actualizarTablaProductos(List<Producto> productos) {
        tableModel.setRowCount(0);
        for (Producto producto : productos) {
            Object[] row = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getEstado()
            };
            tableModel.addRow(row);
        }
    }
    
    public int getFilaSeleccionada() {
        return tableProductos.getSelectedRow();
    }
    
    public String getCodigoFilaSeleccionada() {
        int row = getFilaSeleccionada();
        return row != -1 ? (String) tableModel.getValueAt(row, 0) : null;
    }
    
    // Métodos para limpiar formulario
    public void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecioCompra.setText("");
        txtPrecioVenta.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
        if (comboCatForm.getItemCount() > 0) {
            comboCatForm.setSelectedIndex(0);
        }
    }
    
 // En el método configurarPermisos(), agrega tooltips y mejoras:

    private void configurarPermisos() {
        if (usuarioActual == null) {
            deshabilitarTodosBotones();
            return;
        }
        
        System.out.println("Usuario: " + usuarioActual.getUsername());
        System.out.println("Rol: " + usuarioActual.getRol());
        System.out.println("Permisos asignados: " + usuarioActual.getPermisosEspeciales());
        
        // Configurar botones según permisos
        btnAgregar.setEnabled(usuarioActual.puedeAgregarProductos());
        btnEditar.setEnabled(usuarioActual.puedeEditarProductos());
        btnEliminar.setEnabled(usuarioActual.puedeEliminarProductos());
        
        // Configurar campos editables
        txtPrecioCompra.setEditable(usuarioActual.puedeModificarPrecios());
        txtPrecioVenta.setEditable(usuarioActual.puedeModificarPrecios());
        txtStock.setEditable(usuarioActual.puedeAjustarStock());
        txtStockMinimo.setEditable(usuarioActual.puedeAjustarStock());
        
        // Agregar tooltips informativos
        actualizarTooltips();
        
        // Verificar si solo tiene permisos limitados
        verificarPermisosLimitados();
    }

    private void actualizarTooltips() {
        btnAgregar.setToolTipText(usuarioActual.puedeAgregarProductos() ? 
            "Agregar nuevo producto al inventario" : 
            "No tiene permiso para agregar productos");
        
        btnEditar.setToolTipText(usuarioActual.puedeEditarProductos() ? 
            "Editar producto seleccionado" : 
            "No tiene permiso para editar productos");
        
        btnEliminar.setToolTipText(usuarioActual.puedeEliminarProductos() ? 
            "Eliminar producto seleccionado" : 
            "No tiene permiso para eliminar productos");
        
        txtPrecioCompra.setToolTipText(usuarioActual.puedeModificarPrecios() ? 
            "Precio de compra del producto" : 
            "No tiene permiso para modificar precios");
        
        txtPrecioVenta.setToolTipText(usuarioActual.puedeModificarPrecios() ? 
            "Precio de venta del producto" : 
            "No tiene permiso para modificar precios");
        
        txtStock.setToolTipText(usuarioActual.puedeAjustarStock() ? 
            "Cantidad disponible en inventario" : 
            "No tiene permiso para ajustar stock");
        
        txtStockMinimo.setToolTipText(usuarioActual.puedeAjustarStock() ? 
            "Stock mínimo de alerta" : 
            "No tiene permiso para ajustar stock");
    }

    private void verificarPermisosLimitados() {
        int permisosActivos = 0;
        if (usuarioActual.puedeAgregarProductos()) permisosActivos++;
        if (usuarioActual.puedeEditarProductos()) permisosActivos++;
        if (usuarioActual.puedeEliminarProductos()) permisosActivos++;
        if (usuarioActual.puedeModificarPrecios()) permisosActivos++;
        if (usuarioActual.puedeAjustarStock()) permisosActivos++;
        
        if (permisosActivos <= 1 && permisosActivos > 0) {
            JOptionPane.showMessageDialog(this,
                "⚠ Permisos especiales activados\n" +
                "Solo tiene acceso a funciones específicas según sus permisos.\n" +
                "Los botones/campos deshabilitados requieren permisos adicionales.",
                "Permisos Limitados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deshabilitarTodosBotones() {
        btnAgregar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnActualizar.setEnabled(false);
        btnBuscar.setEnabled(false);
        btnLimpiar.setEnabled(false);
    }

    // Listeners
    public void addAgregarListener(ActionListener listener) {
        btnAgregar.addActionListener(listener);
    }
    
    public void addEditarListener(ActionListener listener) {
        btnEditar.addActionListener(listener);
    }
    
    public void addEliminarListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }
    
    public void addActualizarListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }
    
    public void addBuscarListener(ActionListener listener) {
        btnBuscar.addActionListener(listener);
    }
    
    public void addLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
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
    
    public boolean confirmarEliminacion(String nombre) {
        if (!usuarioActual.puedeEliminarProductos()) {
            mostrarError("No tiene permiso para eliminar productos. Contacte al administrador.");
            return false;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el producto: " + nombre + "?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}