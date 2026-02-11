package test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite Maestra - Todos los Tests")
@SelectClasses({
    AllUnitTests.class,
    AllIntegrationTests.class
})
public class AllTests {
    /*
     * Esta suite ejecuta TODOS los tests (unitarios + integración).
     * 
     * IMPORTANTE: Los tests de integración requieren MongoDB funcionando.
     * 
     * Para ejecutar: mvn test -Dtest=AllTests
     * 
     * Estructura:
     * 1. Tests Unitarios (sin MongoDB)
     * 2. Tests de Integración (con MongoDB real)
     */
}