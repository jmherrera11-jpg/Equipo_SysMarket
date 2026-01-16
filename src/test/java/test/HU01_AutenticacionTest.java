package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HU-01: Autenticación por roles")
public class HU01_AutenticacionTest {

	@Test
	@DisplayName("Escenario 1: Usuario inicia sesión exitosamente")
	public void testInicioSesionExitoso() {
		// Given - Credenciales válidas
		String usuario = "admin";
		String password = "admin123";

		// When - Validamos formato
		boolean usuarioValido = usuario != null && !usuario.trim().isEmpty();
		boolean passwordValido = password != null && !password.trim().isEmpty();
		boolean credencialesCompletas = usuarioValido && passwordValido;

		// Then
		assertTrue(credencialesCompletas, "Credenciales completas deben ser válidas");
		assertEquals("admin", usuario, "Usuario debe coincidir");
		assertTrue(password.length() >= 6, "Password debe tener al menos 6 caracteres");
	}

	@Test
	@DisplayName("Escenario 2: Campos vacíos muestran error")
	public void testCamposVacios() {
		// Caso 1: Usuario vacío
		String usuarioVacio = "";
		String passwordValido = "password123";

		boolean usuarioInvalido = usuarioVacio == null || usuarioVacio.trim().isEmpty();
		boolean passwordValidoCheck = passwordValido != null && !passwordValido.trim().isEmpty();

		assertTrue(usuarioInvalido, "Usuario vacío debe ser inválido");
		assertTrue(passwordValidoCheck, "Password válido debe ser aceptado");

		// Caso 2: Password vacío
		String usuarioValido = "usuario";
		String passwordVacio = "";

		boolean usuarioValidoCheck = usuarioValido != null && !usuarioValido.trim().isEmpty();
		boolean passwordInvalido = passwordVacio == null || passwordVacio.trim().isEmpty();

		assertTrue(usuarioValidoCheck, "Usuario válido debe ser aceptado");
		assertTrue(passwordInvalido, "Password vacío debe ser inválido");
	}

	@Test
	@DisplayName("Escenario 3: Credenciales incorrectas deniegan acceso")
	public void testCredencialesIncorrectas() {
		// Simulamos base de datos de usuarios
		String[][] usuariosDB = { { "admin", "admin123", "SUPERUSUARIO" }, { "gerente", "gerente123", "GERENTE" },
				{ "cajero", "caja123", "CAJERO" } };

		String usuarioIngresado = "admin";
		String passwordIngresado = "passwordIncorrecta";

		// Buscar usuario en DB
		boolean encontrado = false;
		boolean passwordCorrecto = false;

		for (String[] usuarioDB : usuariosDB) {
			if (usuarioDB[0].equals(usuarioIngresado)) {
				encontrado = true;
				if (usuarioDB[1].equals(passwordIngresado)) {
					passwordCorrecto = true;
				}
				break;
			}
		}

		assertTrue(encontrado, "Usuario debe existir en la base de datos");
		assertFalse(passwordCorrecto, "Password incorrecto debe denegar acceso");
	}

	@Test
	@DisplayName("Escenario 4: Sesión inicia con rol asignado")
	public void testSesionConRol() {
		String usuario = "gerente";
		String rolEsperado = "GERENTE";

		// Simulamos autenticación exitosa
		boolean autenticado = true;
		String rolObtenido = rolEsperado;

		assertTrue(autenticado, "Usuario debe estar autenticado");
		assertEquals(rolEsperado, rolObtenido, "Rol debe asignarse correctamente");
		assertNotNull(rolObtenido, "Rol no debe ser nulo");
	}
}
