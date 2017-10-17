package com.foo;

import com.github.jborza.camel.component.smbj.SmbComponent;
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
        SmbComponent component = new SmbComponent(context);
        context.addComponent("smb3",component);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("smb3://smb@192.168.0.108/share_smb/data?password=smb")
//                from("timer://foo?period=1s")
                        .to("log://block");

            }
        });
        context.start();
        Thread.sleep(2000);
        context.stop();;
        //main.run(args);
    }

}

