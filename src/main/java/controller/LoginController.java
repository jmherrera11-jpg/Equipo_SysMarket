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

					// Autenticar usuario
					var usuario = autenticar(username, password);

					view.mostrarExito("Bienvenido " + usuario.getUsername() + " (" + usuario.getRol() + ")");
					view.dispose();

					// Redirigir según el rol
					redirigirSegunRol(usuario);

				} catch (Exception ex) {
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

		Usuario usuario = usuarioRepository.autenticar(username, password);

		if (usuario == null) {
			throw new SecurityException("Usuario o contraseña incorrectos");
		}

		return usuario;
	}

	private void redirigirSegunRol(Usuario usuario) {
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
			JOptionPane.showMessageDialog(null, "Rol no reconocido: " + usuario.getRol());
		}
	}

	private void mostrarVistaBodeguero(ProductoController productoController, Usuario usuario) {
		BodegueroView bodegueroView = new BodegueroView(usuario);
		// MODIFICADO: Pasar usuario al controlador
		BodegueroController bodegueroController = new BodegueroController(bodegueroView, productoController, usuario);
		bodegueroView.setVisible(true);
	}

	private void mostrarVistaCajero(ProductoController productoController, VentaController ventaController,
			Usuario usuario) {
		CajeroView cajeroView = new CajeroView(usuario);
		// MODIFICADO: Quitar el parámetro extra
		CajeroController cajeroController = new CajeroController(cajeroView, productoController, ventaController, usuario);
		cajeroView.setVisible(true);
	}

	private void mostrarVistaGerente(ProductoController productoController, VentaController ventaController,
			GerenteController gerenteController, Usuario usuario) {
		GerenteView gerenteView = new GerenteView(usuario);
		GerenteViewController gerenteViewController = new GerenteViewController(gerenteView, productoController,
	            ventaController, gerenteController, usuario);
		gerenteView.setVisible(true);
	}

	private void mostrarVistaSuperusuarioCompleto(UsuarioManagementController usuarioManagementController,
			ProductoController productoController, VentaController ventaController, GerenteController gerenteController,
			Usuario usuario) {
		SuperusuarioCompletoView superusuarioView = new SuperusuarioCompletoView(usuario);
		SuperusuarioCompletoController superusuarioController = new SuperusuarioCompletoController(superusuarioView,
				usuarioManagementController, productoController, ventaController, gerenteController, usuario);
		superusuarioView.setVisible(true);
	}
}