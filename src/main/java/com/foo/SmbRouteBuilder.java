package com.foo;

import org.apache.camel.builder.RouteBuilder;

public class SmbRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("smb://smb@192.168.0.108/share_smb/generated?password=smb")
                .process(new SmbMessageProcessor())
                .to("log://block")
                .to("file://processed_local?autoCreate=true");
    }
}
