import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SmbToFileTest extends SmbTestBase {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    String folder = "out/integration-test-out";
    String fileName = "hello.txt";
    String shareDst = "integration-test-src";

    @Before
    public void setupFileSystem() throws IOException {
        //clean the destination
        FileUtils.cleanDirectory(new File(folder));
        //clean the source
        Path targetSharePath = Paths.get(getLocalSharePath(), shareDst);
        File targetShareDir = new File(targetSharePath.toString());
        if (!targetShareDir.exists())
            targetShareDir.mkdir();
        FileUtils.cleanDirectory(targetShareDir);
    }

    @Test
    public void testMessageDeliveredToFile() throws Exception {
        String expectedBody = "Hello, world!\n";
        String expectedFileName = Paths.get(folder, fileName).toString();
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(expectedBody);
        resultEndpoint.expectedFileExists(expectedFileName);
        assertMockEndpointsSatisfied();

        Thread.sleep(500);

        //assert the file exists
        java.io.File f = new File("out/integration-test-out/hello.txt");
        assertTrue(f.exists());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
//                from("direct:start").filter(header("foo").isEqualTo("bar")).to("mock:result");
                from("smb2://localhost/share/integration-test-src/1file?noop=true&delay=1000&username=smb&password=smb")
//                        .to("smb2://integration-test")
                        .to("file://" + folder)
                        .to("mock:result");
            }
        };
    }
}
