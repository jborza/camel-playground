package com.foo;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();
        //main.addRouteBuilder(new MyRouteBuilder());
        //main.addRouteBuilder(new SmbRouteBuilder());
        CamelContext context = main.getOrCreateCamelContext();
//        SmbComponent component = new SmbComponent(context);
//        context.addComponent("smb2",component);
//        context.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                from("smb3://smb@192.168.0.108/share_smb/data?password=smb")
//                        .to("log://block")
//                        .to("file://out");
//
//            }
//        });
        boolean readSmb = false;
        if (readSmb)
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("smb2://localhost/share/subdir/?username=smb&password=smb")
                            .to("log://block");
//                from("file://in?noop=true")
//                        .to("log://block")
//                .to("smb://localhost/share?username=smb&password=smb")
//                .to("smb3://smb@192.168.0.105/share_smb/output?password=smb");
                    ;
                }
            });

        boolean writeSmb = true;
        if (writeSmb) {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("file://in_simple?noop=true&recursive=true")
                            .to("log://block")
                            .to("file://out")
//                            .to("smb://localhost/share/out_jcifs/?username=smb&password=smb")
                            .to("smb2://localhost/share/out_smbj/?username=smb&password=smb")
                    ;
                }
            });
        }
        context.start();
        Thread.sleep(5000);
        context.stop();
        //main.run(args);
    }

}

