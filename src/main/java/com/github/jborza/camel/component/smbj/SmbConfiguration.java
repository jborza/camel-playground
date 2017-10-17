package com.github.jborza.camel.component.smbj;

import org.apache.camel.component.file.GenericFileConfiguration;
import org.apache.camel.util.ObjectHelper;

import java.net.URI;

public class SmbConfiguration extends GenericFileConfiguration {

    private static final String DOMAIN_SEPARATOR = ";";
    private static final String USER_PASS_SEPARATOR = ":";

    private String domain;
    private String username;
    private String password;
    private String host;
    private String path;



    private String share;
    private int port;

    public SmbConfiguration(URI uri) {
        configure(uri);
    }

    @Override
    public void configure(URI uri) {
        super.configure(uri);
        String userInfo = uri.getUserInfo();

        if (userInfo != null) {
            if (userInfo.contains(DOMAIN_SEPARATOR)) {
                setDomain(ObjectHelper.before(userInfo, DOMAIN_SEPARATOR));
                userInfo = ObjectHelper.after(userInfo, DOMAIN_SEPARATOR);
            }
            if (userInfo.contains(USER_PASS_SEPARATOR)) {
                setUsername(ObjectHelper.before(userInfo, USER_PASS_SEPARATOR));
                setPassword(ObjectHelper.after(userInfo, USER_PASS_SEPARATOR));
            } else {
                setUsername(userInfo);
            }
        }

        setHost(uri.getHost());
        setPort(uri.getPort());
        setPath(uri.getPath());
        String[] segments = uri.getPath().split("/");
        if(segments.length > 1) //first one is "/"
            setShare(segments[1]);
        setPath(uri.getPath().replace("/"+getShare()+"/",""));
    }

    public String getSmbPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("smb://");
        buffer.append(getHost());
        if (getPort() > 0) {
            buffer.append(":").append(getPort());
        }
        buffer.append(getPath());
        return buffer.toString();
    }

    public String getSmbHostPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("smb://");
        buffer.append(getHost());
        if (getPort() > 0) {
            buffer.append(":").append(getPort());
        }
        buffer.append("/");
        return buffer.toString();
    }


    public String getShare() { return share; }

    public void setShare(String share) { this.share = share; }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    // TODO: give this dirty handling some thinking.
    @Override
    public String getDirectory() {
        String s = super.getDirectory();
        s = s.replace('\\', '/');
        // we always need /
        // this is a bit dirty

        return s;
    }

}

