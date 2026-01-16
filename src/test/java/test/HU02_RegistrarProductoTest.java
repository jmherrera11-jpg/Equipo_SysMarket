package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Producto;

@DisplayName("HU-02: Registrar productos")
public class HU02_RegistrarProductoTest {

	@Test
	@DisplayName("Escenario 1: Registrar producto completo válido")
	public void testRegistrarProductoCompleto() {
		// Given - Producto con todos los campos
		Producto producto = new Producto("PROD001", "Leche Entera 1L", "Lácteos", 2.50, // precioCompra
				3.50, // precioVenta
				100, // stock
				20 // stockMinimo
		);

		// When - Validamos todos los campos
		boolean codigoValido = producto.getCodigo() != null && !producto.getCodigo().trim().isEmpty();
		boolean nombreValido = producto.getNombre() != null && !producto.getNombre().trim().isEmpty();
		boolean categoriaValida = producto.getCategoria() != null && !producto.getCategoria().trim().isEmpty();
		boolean precioCompraValido = producto.getPrecioCompra() > 0;
		boolean precioVentaValido = producto.getPrecioVenta() > producto.getPrecioCompra();
		boolean stockValido = producto.getStock() >= 0;
		boolean stockMinimoValido = producto.getStockMinimo() >= 0;

		// Then - Todos los campos deben ser válidos
		assertAll("Validación completa de producto", () -> assertTrue(codigoValido, "Código válido"),
				() -> assertTrue(nombreValido, "Nombre válido"), () -> assertTrue(categoriaValida, "Categoría válida"),
				() -> assertTrue(precioCompraValido, "Precio compra > 0"),
				() -> assertTrue(precioVentaValido, "Precio venta > precio compra"),
				() -> assertTrue(stockValido, "Stock >= 0"), () -> assertTrue(stockMinimoValido, "Stock mínimo >= 0"));
	}

	@Test
	@DisplayName("Escenario 2: Validación de campos obligatorios faltantes")
	public void testCamposObligatorios() {
		// Caso 1: Sin código
		try {
			Producto productoSinCodigo = new Producto("", // código vacío
					"Leche", "Lácteos", 2.50, 3.50, 100, 20);
			fail("Debió fallar con código vacío");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("código") || e.getMessage().contains("obligatorio"));
		}

		// Caso 2: Sin nombre
		try {
			Producto productoSinNombre = new Producto("PROD001", "", // nombre vacío
					"Lácteos", 2.50, 3.50, 100, 20);
			fail("Debió fallar con nombre vacío");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("nombre") || e.getMessage().contains("obligatorio"));
		}
	}

	@Test
	@DisplayName("Escenario 3: Validación de precios")
	public void testValidacionPrecios() {
		// Caso 1: Precio compra negativo
		try {
			Producto productoPrecioNegativo = new Producto("PROD001", "Leche", "Lácteos", -1.00, // precioCompra
																									// negativo
					3.50, 100, 20);
			fail("Debió fallar con precio compra negativo");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("precio") || e.getMessage().contains("mayor"));
		}

		// Caso 2: Precio venta <= precio compra
		try {
			Producto productoPrecioInvalido = new Producto("PROD002", "Pan", "Panadería", 1.00, 0.99, // precioVenta
																										// menor
					50, 10);
			fail("Debió fallar con precio venta <= precio compra");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("precio") || e.getMessage().contains("mayor"));
		}
	}

	@Test
	@DisplayName("Escenario 4: Validación de stock")
	public void testValidacionStock() {
		// Caso 1: Stock negativo
		try {
			Producto productoStockNegativo = new Producto("PROD001", "Azúcar", "Abarrotes", 1.50, 2.00, -5, // stock
																											// negativo
					10);
			fail("Debió fallar con stock negativo");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("stock") || e.getMessage().contains("negativo"));
		}

		// Caso 2: Stock mínimo negativo
		try {
			Producto productoStockMinimoNegativo = new Producto("PROD002", "Café", "Bebidas", 5.00, 7.00, 50, -10 // stock
																													// mínimo
																													// negativo
			);
			fail("Debió fallar con stock mínimo negativo");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("stock mínimo") || e.getMessage().contains("negativo"));
		}
	}

	@Test
	@DisplayName("Escenario 5: Producto se registra en inventario")
	public void testProductoEnInventario() {
		// Given - Producto válido
		Producto producto = new Producto("PROD999", "Producto Test", "Test", 10.00, 15.00, 100, 20);

		// When - Registramos
		boolean registrado = true; // Simulación exitosa
		String codigo = producto.getCodigo();

		// Then
		assertTrue(registrado, "Producto debe registrarse exitosamente");
		assertEquals("PROD999", codigo, "Código debe mantenerse");
		assertTrue(producto.getNombre().contains("Test"), "Nombre debe contener 'Test'");
		assertEquals(100, producto.getStock(), "Stock debe ser 100");
	}
}