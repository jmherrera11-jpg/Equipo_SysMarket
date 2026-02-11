package controller;

import model.Usuario;
import view.LoginView;
import view.SuperusuarioCompletoView;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class SuperusuarioCompletoController {
    private SuperusuarioCompletoView view;
    private UsuarioManagementController usuarioManagementController;
    private ProductoController productoController;
    private VentaController ventaController;
    private GerenteController gerenteController;
    private Usuario usuarioActual;
    
    public SuperusuarioCompletoController(SuperusuarioCompletoView view,
                                        UsuarioManagementController usuarioManagementController,
                                        ProductoController productoController,
                                        VentaController ventaController,
                                        GerenteController gerenteController,
                                        Usuario usuarioActual) {
        this.view = view;
        this.usuarioManagementController = usuarioManagementController;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.gerenteController = gerenteController;
        this.usuarioActual = usuarioActual;
        initializeController();
    }
    
    private void initializeController() {
        cargarDatosCompletos();
        configurarListeners();
    }
    
    private void cargarDatosCompletos() {
        try {
            // Cargar usuarios
            var usuarios = usuarioManagementController.obtenerTodosUsuarios();
            view.actualizarTablaUsuarios(usuarios);

            // Cargar productos
            var productos = productoController.obtenerTodosProductos();
            view.actualizarTablaProductos(productos);

            // Cargar ventas
            var ventas = ventaController.obtenerTodasVentas();
            view.actualizarTablaVentas(ventas);

            // Cargar dashboard
            var estadisticas = gerenteController.obtenerEstadisticasDashboard();
            int totalUsuarios = usuarios.size();
            int totalProductos = productos.size();
            double totalVentas = (Double) estadisticas.get("totalVentas");

            view.actualizarDashboard(totalUsuarios, totalProductos, totalVentas);

        } catch (Exception e) {
            view.mostrarError("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configurarListeners() {
        // Listener para AGREGAR usuario
        view.addAgregarUsuarioListener(e -> agregarUsuario());
        
        // Listener para EDITAR usuario
        view.addEditarUsuarioListener(e -> editarUsuario());
        
        // Listener para ELIMINAR usuario
        view.addEliminarUsuarioListener(e -> eliminarUsuario());
        
        // Listener para ACTUALIZAR lista
        view.addActualizarUsuariosListener(e -> actualizarListaUsuarios());
        
        // Listener para cerrar sesión
        view.addCerrarSesionListener(e -> cerrarSesion());
        
        // Listener para cambio de rol en combo
        view.getComboRol().addActionListener(e -> actualizarPermisosSegunRol());
    }
    
    private void agregarUsuario() {
        try {
            String username = view.getUsername();
            String password = view.getPassword();
            String rol = view.getRol();
            List<String> permisosSeleccionados = view.getPermisosSeleccionados();

            // Validaciones
            if (username.isEmpty() || password.isEmpty()) {
                view.mostrarError("Usuario y contraseña son obligatorios");
                return;
            }
            
            if (password.length() < 6) {
                view.mostrarError("La contraseña debe tener al menos 6 caracteres");
                return;
            }
            
            // Filtrar permisos válidos para el rol
            List<String> permisosValidos = new ArrayList<>();
            for (String permiso : permisosSeleccionados) {
                if (Usuario.esPermisoValidoParaRol(permiso, rol)) {
                    permisosValidos.add(permiso);
                } else {
                    System.out.println("Permiso " + permiso + " no válido para rol " + rol);
                }
            }

            // Crear nuevo usuario
            var usuario = new Usuario(username, password, rol);
            
            // Si se seleccionaron permisos específicos, sobreescribir los por defecto
            if (!permisosValidos.isEmpty()) {
                usuario.setPermisosEspeciales(permisosValidos);
            }

            // Agregar usuario
            usuarioManagementController.agregarUsuarioConPermisos(usuario, usuario.getPermisosEspeciales(), usuarioActual);
            
            view.mostrarExito("Usuario '" + username + "' creado exitosamente con " + 
                             usuario.getPermisosEspeciales().size() + " permiso(s)");
            view.limpiarFormularioUsuario();

            // Recargar datos
            cargarDatosCompletos();

        } catch (Exception ex) {
            view.mostrarError("Error al agregar usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void editarUsuario() {
        try {
            String username = view.getUsuarioSeleccionado();
            if (username == null) {
                view.mostrarError("Por favor seleccione un usuario para editar");
                return;
            }

            // Buscar usuario en la base de datos
            var usuarios = usuarioManagementController.obtenerTodosUsuarios();
            var usuario = usuarios.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

            if (usuario != null) {
                // Cargar datos del usuario en el formulario
                view.setUsuarioParaEditar(usuario);
                
                // Preguntar si quiere actualizar
                int opcion = JOptionPane.showConfirmDialog(view,
                    "¿Desea actualizar los permisos del usuario '" + username + "'?",
                    "Actualizar Permisos",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    String nuevoRol = view.getRol();
                    List<String> nuevosPermisos = view.getPermisosSeleccionados();
                    
                    // Filtrar permisos válidos
                    List<String> permisosValidos = new ArrayList<>();
                    for (String permiso : nuevosPermisos) {
                        if (Usuario.esPermisoValidoParaRol(permiso, nuevoRol)) {
                            permisosValidos.add(permiso);
                        }
                    }
                    
                    // Actualizar usuario
                    usuario.setRol(nuevoRol);
                    usuario.setPermisosEspeciales(permisosValidos);
                    usuarioManagementController.actualizarPermisosUsuario(username, permisosValidos, usuarioActual);
                    
                    view.mostrarExito("Usuario actualizado exitosamente");
                    cargarDatosCompletos();
                }
            } else {
                view.mostrarError("Usuario no encontrado");
            }

        } catch (Exception ex) {
            view.mostrarError("Error al cargar usuario: " + ex.getMessage());
        }
    }
    
    private void eliminarUsuario() {
        try {
            String username = view.getUsuarioSeleccionado();
            if (username == null) {
                view.mostrarError("Por favor seleccione un usuario para eliminar");
                return;
            }

            // Confirmar eliminación
            if (view.confirmarEliminacion(username)) {
                usuarioManagementController.eliminarUsuario(username, usuarioActual);
                view.mostrarExito("Usuario '" + username + "' eliminado exitosamente");
                view.limpiarFormularioUsuario();
                cargarDatosCompletos();
            }

        } catch (Exception ex) {
            view.mostrarError("Error al eliminar usuario: " + ex.getMessage());
        }
    }
    
    private void actualizarListaUsuarios() {
        try {
            cargarDatosCompletos();
            view.mostrarExito("Lista de usuarios actualizada");
        } catch (Exception ex) {
            view.mostrarError("Error al actualizar lista: " + ex.getMessage());
        }
    }
    
    private void actualizarPermisosSegunRol() {
        String rol = view.getRol();
        
        // Deshabilitar checkboxes según el rol
        var checkBoxes = view.getCheckBoxesPermisos();
        if (checkBoxes != null) {
            for (var entry : checkBoxes.entrySet()) {
                String permiso = entry.getKey();
                JCheckBox checkBox = entry.getValue();
                
                // Habilitar todos los checkboxes para superusuario
                if (Usuario.ROL_SUPERUSUARIO.equals(rol)) {
                    checkBox.setEnabled(true);
                } else {
                    // Validar permisos según rol
                    boolean esValido = Usuario.esPermisoValidoParaRol(permiso, rol);
                    checkBox.setEnabled(esValido);
                    if (!esValido) {
                        checkBox.setSelected(false); // Desmarcar permisos no válidos
                    }
                }
            }
        }
    }
    
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "¿Está seguro de que desea cerrar sesión?",
            "Confirmar Cierre de Sesión", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            volverAlLogin();
        }
    }
    
    private void volverAlLogin() {
        new LoginView().setVisible(true);
    }
    
    // Método para obtener el combo de roles desde la vista (debes agregar este getter en la vista)
    public JComboBox<String> getComboRol() {
        return view.getComboRol();
    }
}