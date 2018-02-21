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

public class FileToSmbTest extends SmbTestBase {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    String folder = "src/it/data";
    String fileName = "LICENSE.txt";
    String fileName2 = "logo-camel.png";
    String shareDst = "integration-test-dst";

    @Before
    public void setupFileSystem() throws IOException {
        //delete contents of the target share
        Path targetSharePath = Paths.get(getLocalSharePath(), shareDst);
        File targetShareDir = new File(targetSharePath.toString());
        FileUtils.cleanDirectory(targetShareDir);
        //contents of the source share are in src/it/data
    }

    @Test
    public void testMessageDeliveredToSmb() throws Exception {
        String expectedFileName = Paths.get(folder, fileName).toString();
        resultEndpoint.expectedMessageCount(3); //this gets ignored anyway :-/
        assertMockEndpointsSatisfied();

        Thread.sleep(500);

        //assert the files exists in the SMB folder
        String[] names = new String[]{"LICENSE.TXT", "logo-camel.png", "subdir/file2.txt"};
        for (String name : names) {
            String pathInShare = Paths.get(getLocalSharePath(), shareDst, name).toString();
            java.io.File f = new File(pathInShare);
            assertTrue(name + " expected, but doesn't exist!", f.exists());
        }
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("file://" + folder + "?noop=true&recursive=true")
                        .to(getShareUrl(shareDst))
                        .to("mock:result");
            }
        };
    }
}
