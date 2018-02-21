import org.apache.camel.test.junit4.CamelTestSupport;

public class SmbTestBase extends CamelTestSupport {
    protected String getLocalSharePath() {
        return "/Users/juraj/share/";
    }

    protected String getUserName() {
        return "smb";
    }

    protected String getPassword() {
        return "smb";
    }

    protected String getHost() {
        return "127.0.0.1";
    }

    protected String getShareUrl(String share) {
        return String.format("smb2://localhost/share/%s?username=%s&password=%s", share, getUserName(), getPassword());
    }
}
