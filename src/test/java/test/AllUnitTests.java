package test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite Completa de Tests Unitarios")
@SelectClasses({
    // Tests de Modelos
    test.model.UsuarioModelTest.class,
    test.model.ProductoModelTest.class,
    test.model.VentaModelTest.class,
    
    // Tests de Controladores Unitarios
    test.controller.ProductoControllerUnitTest.class,
    test.controller.VentaControllerUnitTest.class,
    test.controller.UsuarioManagementControllerUnitTest.class,
    test.controller.UsuarioControllerUnitTest.class,
    test.controller.GerenteControllerUnitTest.class
})
public class AllUnitTests {
    /*
     * Esta suite ejecuta todos los tests unitarios que NO requieren MongoDB.
     * Estos tests usan clases fake/mock para aislar las pruebas.
     * 
     * Para ejecutar: mvn test -Dtest=AllUnitTests
     */
}