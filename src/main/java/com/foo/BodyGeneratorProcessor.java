package com.foo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

class BodyGeneratorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String timestamp = new java.util.Date().toString();
        exchange.getIn().setBody("Body generated @ "+timestamp);
    }
}
