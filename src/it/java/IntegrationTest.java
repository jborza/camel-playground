import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.main.Main;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest extends TestCase {

    @Before
    public void cleanup() {
//        FileUtils.cleanDirectory("in");
    }

    @Test
    public void testfileToSmbSimpleTest() {
        //prepare
        Main main = new Main();
        CamelContext context = main.getOrCreateCamelContext();
//        context.addRoutes();
    }

    @Test
    public void testSomethingSimple() {
        assertEquals(2, 3);
    }
}
