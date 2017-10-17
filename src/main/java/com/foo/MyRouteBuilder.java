package com.foo;

import org.apache.camel.builder.RouteBuilder;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)
        // then performs content based routing on the message using XPath
//        from("file:src/data?noop=true")
//            .choice()
//                .when(xpath("/person/city = 'London'"))
//                    .log("UK message")
//                    .to("file:target/messages/uk")
//                .otherwise()
//                    .log("Other message")
//                    .to("file:target/messages/others");

        from("timer://foo?period=3s")
                .process(new BodyGeneratorProcessor())
                .to("log:block")
        .to("file://.")
        //.to("ftp://192.168.0.108?username=guest&password=guest")
        .to("smb://smb@192.168.0.108/share_smb?password=smb")
        ;
    }

}
