package controller;

import model.Usuario;
import repository.UsuarioRepository;
import java.util.List;

public class UsuarioManagementController {
	private UsuarioRepository usuarioRepository;

	public UsuarioManagementController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> obtenerTodosUsuarios() {
		return usuarioRepository.obtenerTodosUsuarios();
	}

	public void agregarUsuario(Usuario usuario, Usuario usuarioActual) {
		// Validar que solo superusuarios pueden agregar usuarios
		if (!usuarioActual.esSuperusuario()) {
			throw new SecurityException("Solo los superusuarios pueden agregar usuarios");
		}

		// Validaciones del usuario
		if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de usuario es obligatorio");
		}

		if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("La contraseña es obligatoria");
		}

		if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
			throw new IllegalArgumentException("El rol es obligatorio");
		}

		// Validar rol válido
		if (!esRolValido(usuario.getRol())) {
			throw new IllegalArgumentException("Rol no válido: " + usuario.getRol());
		}

		usuarioRepository.agregarUsuario(usuario);
	}

	public void actualizarUsuario(String username, Usuario usuario, Usuario usuarioActual) {
		// Validar que solo superusuarios pueden actualizar usuarios
		if (!usuarioActual.esSuperusuario()) {
			throw new SecurityException("Solo los superusuarios pueden actualizar usuarios");
		}

		// No permitir cambiar el rol del superusuario principal
		if ("admin".equals(username) && !Usuario.ROL_SUPERUSUARIO.equals(usuario.getRol())) {
			throw new IllegalArgumentException("No se puede cambiar el rol del superusuario principal");
		}

		usuarioRepository.actualizarUsuario(username, usuario);
	}

	public void eliminarUsuario(String username, Usuario usuarioActual) {
		// Validar que solo superusuarios pueden eliminar usuarios
		if (!usuarioActual.esSuperusuario()) {
			throw new SecurityException("Solo los superusuarios pueden eliminar usuarios");
		}

		// No permitir eliminarse a sí mismo
		if (usuarioActual.getUsername().equals(username)) {
			throw new IllegalArgumentException("No puede eliminarse a sí mismo");
		}

		usuarioRepository.eliminarUsuario(username);
	}

	private boolean esRolValido(String rol) {
		return Usuario.ROL_SUPERUSUARIO.equals(rol) || Usuario.ROL_GERENTE.equals(rol)
				|| Usuario.ROL_BODEGUERO.equals(rol) || Usuario.ROL_CAJERO.equals(rol);
	}

	public boolean puedeGestionarUsuarios(Usuario usuario) {
		return usuario != null && usuario.esSuperusuario();
	}

	public void agregarUsuarioConPermisos(Usuario usuario, List<String> permisosEspeciales, Usuario usuarioActual) {
		if (!usuarioActual.esSuperusuario()) {
			throw new SecurityException("Solo los superusuarios pueden agregar usuarios");
		}

		// Validaciones del usuario
		if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de usuario es obligatorio");
		}

		if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("La contraseña es obligatoria");
		}

		if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
			throw new IllegalArgumentException("El rol es obligatorio");
		}

		// Validar rol válido
		if (!esRolValido(usuario.getRol())) {
			throw new IllegalArgumentException("Rol no válido: " + usuario.getRol());
		}

		usuario.setPermisosEspeciales(permisosEspeciales);
		usuarioRepository.agregarUsuario(usuario);
	}

	public void actualizarPermisosUsuario(String username, List<String> permisosEspeciales, Usuario usuarioActual) {
		if (!usuarioActual.esSuperusuario()) {
			throw new SecurityException("Solo los superusuarios pueden actualizar permisos");
		}

		Usuario usuario = usuarioRepository.buscarPorUsername(username);
		if (usuario != null) {
			usuario.setPermisosEspeciales(permisosEspeciales);
			usuarioRepository.actualizarUsuario(username, usuario);
		}
	}

}
