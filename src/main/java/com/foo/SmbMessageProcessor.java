package com.foo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apacheextras.camel.component.jcifs.SmbGenericFile;

import java.io.ByteArrayOutputStream;

public class SmbMessageProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        SmbGenericFile file = (SmbGenericFile) exchange.getIn().getBody();
        ByteArrayOutputStream baos = (ByteArrayOutputStream) file.getBody();

        String s = new String(baos.toByteArray());
        String timestamp = new java.util.Date().toString();
        exchange.getOut().setBody(s+"\r\n"+"Processed @ "+timestamp);
    }
}
