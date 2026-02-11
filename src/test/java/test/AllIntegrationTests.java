package test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite Completa de Tests de Integración")
@SelectClasses({
    // Tests de Repositorios (requieren MongoDB real)
    test.repository.UsuarioRepositoryIntegrationTest.class,
    test.repository.ProductoRepositoryIntegrationTest.class,
    test.repository.VentaRepositoryIntegrationTest.class,
    test.repository.CategoriaRepositoryIntegrationTest.class
})
public class AllIntegrationTests {
    /*
     * Esta suite ejecuta todos los tests de integración que requieren MongoDB real.
     * Estos tests realizan operaciones CRUD reales en la base de datos.
     * 
     * IMPORTANTE: Requiere que MongoDB esté funcionando.
     * 
     * Para ejecutar: mvn test -Dtest=AllIntegrationTests
     */
}