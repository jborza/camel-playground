package com.github.jborza.camel.component.smbj;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.share.File;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.component.file.GenericFileComponent;
import org.apache.camel.component.file.GenericFileEndpoint;

import java.net.URI;
import java.util.Map;

public class SmbComponent extends GenericFileComponent<File> {

    public SmbComponent(CamelContext context) {
        super(context);
    }

    @Override
    protected SmbEndpoint buildFileEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        uri = fixSpaces(uri);
        SmbConfiguration config = new SmbConfiguration(new URI(uri));
        SmbEndpoint endpoint = new SmbEndpoint(uri, this, config);
        return endpoint;
    }

    @Override
    protected void afterPropertiesSet(GenericFileEndpoint<File> genericFileEndpoint) throws Exception {
        //TODO noop
    }

    private String fixSpaces(String input) {
        return input.replace(" ", "%20");
    }
}
