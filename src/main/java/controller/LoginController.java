package controller;

import model.Usuario;
import database.MongoDBConnection;
import repository.UsuarioRepository;
import repository.ProductoRepository;
import repository.CategoriaRepository;
import repository.VentaRepository;
import view.LoginView;
import view.BodegueroView;
import view.CajeroView;
import view.GerenteView;
import view.SuperusuarioCompletoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView view;
    private UsuarioRepository usuarioRepository;

    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioRepository = new UsuarioRepository(MongoDBConnection.getDatabase());
        initializeController();
    }

    private void initializeController() {
        // Configurar el listener del login
        view.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = view.getUsername();
                    String password = view.getPassword();

                    System.out.println("\n=== INTENTO DE LOGIN ===");
                    System.out.println("Usuario: " + username);
                    System.out.println("Contraseña ingresada: " + (password != null ? "***" + password.length() + "***" : "null"));

                    // Autenticar usuario
                    var usuario = autenticar(username, password);

                    System.out.println("✅ Login exitoso para: " + usuario.getUsername());
                    System.out.println("Rol: " + usuario.getRol());
                    System.out.println("Permisos: " + usuario.getPermisosEspeciales());
                    System.out.println("Tiene acceso total: " + usuario.tieneAccesoTotal());
                    System.out.println("===========================\n");

                    view.mostrarExito("Bienvenido " + usuario.getUsername() + " (" + usuario.getRol() + ")");
                    view.dispose();

                    // Redirigir según el rol
                    redirigirSegunRol(usuario);

                } catch (Exception ex) {
                    System.err.println("❌ Error en login: " + ex.getMessage());
                    ex.printStackTrace();
                    view.mostrarError(ex.getMessage());
                    view.limpiarFormulario();
                }
            }
        });
    }

    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        System.out.println("Buscando usuario: " + username);
        
        // Primero buscar el usuario por username
        Usuario usuario = usuarioRepository.buscarPorUsername(username);
        
        if (usuario == null) {
            throw new SecurityException("Usuario no encontrado: " + username);
        }
        
        System.out.println("Usuario encontrado: " + usuario.getUsername());
        System.out.println("Rol en BD: " + usuario.getRol());
        System.out.println("Permisos en BD: " + usuario.getPermisosEspeciales());
        
        // Verificar contraseña
        if (!usuario.getPassword().equals(password)) {
            throw new SecurityException("Contraseña incorrecta para el usuario: " + username);
        }
        
        // Verificar que el usuario tenga permisos básicos según su rol
        if (usuario.getPermisosEspeciales() == null || usuario.getPermisosEspeciales().isEmpty()) {
            System.out.println("⚠ Usuario sin permisos asignados, asignando permisos por defecto...");
            // Asignar permisos por defecto según rol
            switch (usuario.getRol()) {
                case "SUPERUSUARIO":
                    usuario.setPermisosEspeciales(java.util.Arrays.asList(
                        "ACCESO_TOTAL", "GESTIONAR_USUARIOS", "GESTIONAR_PRODUCTOS",
                        "VER_REPORTES", "EXPORTAR_DATOS", "CONFIGURAR_SISTEMA"
                    ));
                    break;
                case "GERENTE":
                    usuario.setPermisosEspeciales(java.util.Arrays.asList(
                        "VER_REPORTES", "EXPORTAR_DATOS", "GESTIONAR_PRODUCTOS",
                        "ACCEDER_GESTION_PRODUCTOS", "ACCEDER_CONTROL_VENTAS",
                        "ACCEDER_REPORTES", "ACCEDER_DASHBOARD"
                    ));
                    break;
                case "BODEGUERO":
                    usuario.setPermisosEspeciales(java.util.Arrays.asList(
                        "AGREGAR_PRODUCTOS", "EDITAR_PRODUCTOS", "ELIMINAR_PRODUCTOS",
                        "MODIFICAR_PRECIOS", "AJUSTAR_STOCK", "GESTIONAR_CATEGORIAS"
                    ));
                    break;
                case "CAJERO":
                    usuario.setPermisosEspeciales(java.util.Arrays.asList(
                        "PROCESAR_VENTAS", "GESTIONAR_CARRITO", "VER_REPORTES_BASICOS",
                        "AGREGAR_CARRITO", "QUITAR_CARRITO", "LIMPIAR_CARRITO", "REALIZAR_VENTA"
                    ));
                    break;
            }
            // Actualizar usuario en la base de datos
            usuarioRepository.actualizarUsuario(username, usuario);
        }

        return usuario;
    }

    private void redirigirSegunRol(Usuario usuario) {
        // Log de permisos del usuario
        System.out.println("\n=== REDIRIGIENDO SEGÚN ROL ===");
        System.out.println("Usuario: " + usuario.getUsername());
        System.out.println("Rol: " + usuario.getRol());
        System.out.println("Permisos: " + usuario.getPermisosEspeciales());
        System.out.println("Tiene acceso total: " + usuario.tieneAccesoTotal());
        System.out.println("==============================\n");
        
        // Inicializar repositorios y controladores necesarios
        ProductoRepository productoRepository = new ProductoRepository(MongoDBConnection.getDatabase());
        CategoriaRepository categoriaRepository = new CategoriaRepository(MongoDBConnection.getDatabase());
        VentaRepository ventaRepository = new VentaRepository(MongoDBConnection.getDatabase());

        ProductoController productoController = new ProductoController(productoRepository, categoriaRepository);
        VentaController ventaController = new VentaController(ventaRepository, productoRepository);
        GerenteController gerenteController = new GerenteController(productoRepository, ventaRepository,
                categoriaRepository);
        UsuarioManagementController usuarioManagementController = new UsuarioManagementController(usuarioRepository);

        switch (usuario.getRol()) {
            case "BODEGUERO":
                mostrarVistaBodeguero(productoController, usuario);
                break;
            case "GERENTE":
                mostrarVistaGerente(productoController, ventaController, gerenteController, usuario);
                break;
            case "CAJERO":
                mostrarVistaCajero(productoController, ventaController, usuario);
                break;
            case "SUPERUSUARIO":
                mostrarVistaSuperusuarioCompleto(usuarioManagementController, productoController, ventaController,
                        gerenteController, usuario);
                break;
            default:
                JOptionPane.showMessageDialog(null, 
                    "Rol no reconocido: " + usuario.getRol() + "\n" +
                    "Contacte al administrador del sistema.",
                    "Error de Rol",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarVistaBodeguero(ProductoController productoController, Usuario usuario) {
        System.out.println("🔄 Abriendo vista de bodeguero para: " + usuario.getUsername());
        SwingUtilities.invokeLater(() -> {
            BodegueroView bodegueroView = new BodegueroView(usuario);
            BodegueroController bodegueroController = new BodegueroController(bodegueroView, productoController, usuario);
            bodegueroView.setVisible(true);
        });
    }

    private void mostrarVistaCajero(ProductoController productoController, VentaController ventaController,
            Usuario usuario) {
        System.out.println("🔄 Abriendo vista de cajero para: " + usuario.getUsername());
        SwingUtilities.invokeLater(() -> {
            CajeroView cajeroView = new CajeroView(usuario);
            CajeroController cajeroController = new CajeroController(cajeroView, productoController, ventaController, usuario);
            cajeroView.setVisible(true);
        });
    }

    private void mostrarVistaGerente(ProductoController productoController, VentaController ventaController,
            GerenteController gerenteController, Usuario usuario) {
        System.out.println("🔄 Abriendo vista de gerente para: " + usuario.getUsername());
        SwingUtilities.invokeLater(() -> {
            GerenteView gerenteView = new GerenteView(usuario);
            GerenteViewController gerenteViewController = new GerenteViewController(gerenteView, productoController,
                    ventaController, gerenteController, usuario);
            gerenteView.setVisible(true);
        });
    }

    private void mostrarVistaSuperusuarioCompleto(UsuarioManagementController usuarioManagementController,
            ProductoController productoController, VentaController ventaController, GerenteController gerenteController,
            Usuario usuario) {
        System.out.println("🔄 Abriendo vista de superusuario para: " + usuario.getUsername());
        SwingUtilities.invokeLater(() -> {
            SuperusuarioCompletoView superusuarioView = new SuperusuarioCompletoView(usuario);
            SuperusuarioCompletoController superusuarioController = new SuperusuarioCompletoController(superusuarioView,
                    usuarioManagementController, productoController, ventaController, gerenteController, usuario);
            superusuarioView.setVisible(true);
        });
    }
    
    // Método para limpiar la consola (opcional)
    private void limpiarConsola() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // Ignorar errores de limpieza de consola
        }
    }
}