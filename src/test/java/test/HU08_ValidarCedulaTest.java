package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import config.CedulaValidator;

@DisplayName("HU-08: Validar cédula del cliente antes de facturar")
public class HU08_ValidarCedulaTest {
    
    @BeforeEach
    public void setUp() {
        // Preparar datos para cada test
    }
    
    @Test
    @DisplayName("CA-1: La cédula debe tener formato válido (10 dígitos)")
    public void testFormatoCedulaValido() {
        // Given - Cédulas válidas (ejemplos de cédulas válidas ecuatorianas)
        String[] cedulasValidas = {
            "1715045179",  // Cédula ecuatoriana válida
            "1722334411",  // Otra cédula válida
            "1719876543"   // Otra cédula válida
        };
        
        // Then - Las cédulas válidas deben pasar validación
        for (String cedula : cedulasValidas) {
            assertTrue(CedulaValidator.esValida(cedula), 
                      "La cédula " + cedula + " debe ser válida");
        }
    }
    
    @Test
    @DisplayName("CA-1: La cédula NO puede tener menos de 10 dígitos")
    public void testCedulaMuyCorta() {
        String cedulaCorta = "171504517";  // 9 dígitos
        
        assertFalse(CedulaValidator.esValida(cedulaCorta), 
                   "Cédula con menos de 10 dígitos debe ser inválida");
        
        String error = CedulaValidator.obtenerMensajeError(cedulaCorta);
        assertNotNull(error, "Debe retornar mensaje de error");
        assertTrue(error.contains("10"), "Error debe mencionar los 10 dígitos requeridos");
    }
    
    @Test
    @DisplayName("CA-1: La cédula NO puede tener más de 10 dígitos")
    public void testCedulaMuyLarga() {
        String cedulaLarga = "17150451799";  // 11 dígitos
        
        assertFalse(CedulaValidator.esValida(cedulaLarga), 
                   "Cédula con más de 10 dígitos debe ser inválida");
        
        String error = CedulaValidator.obtenerMensajeError(cedulaLarga);
        assertNotNull(error, "Debe retornar mensaje de error");
    }
    
    @Test
    @DisplayName("CA-1: La cédula NO puede contener letras")
    public void testCedulaConLetras() {
        String cedulaConLetras = "171AB45179";
        
        assertFalse(CedulaValidator.esValida(cedulaConLetras), 
                   "Cédula con letras debe ser inválida");
        
        String error = CedulaValidator.obtenerMensajeError(cedulaConLetras);
        assertNotNull(error, "Debe retornar mensaje de error");
        assertTrue(error.contains("números"), "Error debe mencionar solo números");
    }
    
    @Test
    @DisplayName("CA-1: La cédula NO puede contener caracteres especiales")
    public void testCedulaConCaracteresEspeciales() {
        String[] cedulasInvalidas = {
            "171-504-517-9",  // Con guiones
            "171 504 517 9",  // Con espacios
            "171.504.517.9"   // Con puntos
        };
        
        for (String cedula : cedulasInvalidas) {
            assertFalse(CedulaValidator.esValida(cedula), 
                       "Cédula con caracteres especiales debe ser inválida");
        }
    }
    
    @Test
    @DisplayName("CA-1: La cédula NO puede ser toda ceros")
    public void testCedulaTodaCeros() {
        String cedulaCeros = "0000000000";
        
        assertFalse(CedulaValidator.esValida(cedulaCeros), 
                   "Cédula toda ceros debe ser inválida");
        
        String error = CedulaValidator.obtenerMensajeError(cedulaCeros);
        assertNotNull(error, "Debe retornar mensaje de error");
        assertTrue(error.contains("ceros"), "Error debe mencionar que no puede ser toda ceros");
    }
    
    @Test
    @DisplayName("CA-1: Validación del dígito verificador")
    public void testValidacionDigitoVerificador() {
        // Cédula con dígito verificador incorrecto
        String cedulaInvalida = "1715045170";  // Último dígito es 0 pero debería ser 9
        
        assertFalse(CedulaValidator.esValida(cedulaInvalida), 
                   "Cédula con dígito verificador incorrecto debe ser inválida");
        
        String error = CedulaValidator.obtenerMensajeError(cedulaInvalida);
        assertNotNull(error, "Debe retornar mensaje de error");
        assertTrue(error.contains("verificador"), "Error debe mencionar el dígito verificador");
    }
    
    @Test
    @DisplayName("CA-2: Si cédula no es válida, se bloquea la facturación")
    public void testBloqueoFacturacionCedulaInvalida() {
        String cedulaInvalida = "123456789";  // Inválida - solo 9 dígitos
        
        String errorMessage = CedulaValidator.obtenerMensajeError(cedulaInvalida);
        
        // Si hay un mensaje de error, la facturación debe bloquearse
        assertNotNull(errorMessage, "Debe haber un mensaje de error");
        assertFalse(CedulaValidator.esValida(cedulaInvalida),
                   "La facturación debe estar bloqueada para cédula inválida");
    }
    
    @Test
    @DisplayName("CA-2: Si cédula es válida, permite facturación")
    public void testPermisoFacturacionCedulaValida() {
        String cedulaValida = "1715045179";  // Válida
        
        String errorMessage = CedulaValidator.obtenerMensajeError(cedulaValida);
        
        // No debe haber mensaje de error
        assertNull(errorMessage, "Cédula válida no debe tener mensaje de error");
        assertTrue(CedulaValidator.esValida(cedulaValida),
                  "La facturación debe permitirse para cédula válida");
    }
    
    @Test
    @DisplayName("CA-3: Mensaje de error es visible y claro")
    public void testMensajeErrorClaro() {
        // Test de cédula vacía
        String errorVacia = CedulaValidator.obtenerMensajeError("");
        assertNotNull(errorVacia);
        assertTrue(errorVacia.toLowerCase().contains("vacía") || 
                  errorVacia.toLowerCase().contains("vacío"),
                  "Mensaje debe ser claro sobre cédula vacía");
        
        // Test de cédula corta
        String errorCorta = CedulaValidator.obtenerMensajeError("123");
        assertNotNull(errorCorta);
        assertTrue(errorCorta.contains("10"), "Mensaje debe indicar 10 dígitos");
        
        // Test de cédula con letras
        String errorLetras = CedulaValidator.obtenerMensajeError("ABCD123456");
        assertNotNull(errorLetras);
        assertTrue(errorLetras.toLowerCase().contains("números"),
                  "Mensaje debe indicar que solo acepta números");
    }
    
    @Test
    @DisplayName("CA-3: Formateo de cédula para presentación")
    public void testFormatearCedula() {
        String cedulaSinFormato = "1715045179";
        String cedulaFormateada = CedulaValidator.formatear(cedulaSinFormato);
        
        assertEquals("171-504-517-9", cedulaFormateada,
                    "Cédula debe formatearse correctamente");
    }
    
    @Test
    @DisplayName("CA-3: Obtener cédula sin formato")
    public void testObtenerCedulaSinFormato() {
        String cedulaFormateada = "171-504-517-9";
        String cedulaSinFormato = CedulaValidator.sinFormato(cedulaFormateada);
        
        assertEquals("1715045179", cedulaSinFormato,
                    "Debe remover formato de la cédula");
    }
    
    @Test
    @DisplayName("HU-08: Flujo completo de validación antes de facturar")
    public void testFlujoCompletoValidacion() {
        // PASO 1: Cajero ingresa cédula del cliente
        String cedulaIngresada = "1715045179";
        
        // PASO 2: Sistema valida la cédula
        String mensajeError = CedulaValidator.obtenerMensajeError(cedulaIngresada);
        
        // PASO 3: Si hay error, mostrar mensaje y bloquear facturación
        if (mensajeError != null) {
            fail("Cédula válida no debe tener error: " + mensajeError);
        }
        
        // PASO 4: Si es válida, permitir facturación
        assertTrue(CedulaValidator.esValida(cedulaIngresada),
                  "Cédula debe permitir facturación");
        
        // PASO 5: Registrar venta con cédula
        assertNotNull(cedulaIngresada);
        assertEquals(10, cedulaIngresada.length());
    }
    
    @Test
    @DisplayName("HU-08: Rechazo de facturación sin cédula válida")
    public void testRechazoFacturacionSinCedulaValida() {
        String[] cedulasInvalidas = {
            "123",              // Muy corta
            "12345678901234",   // Muy larga
            "ABCDEFGHIJ",       // Letras
            "1234-567-890",     // Con formato
            "0000000000"        // Toda ceros
        };
        
        for (String cedula : cedulasInvalidas) {
            assertFalse(CedulaValidator.esValida(cedula),
                       "Cédula " + cedula + " debe ser rechazada");
        }
    }
}
