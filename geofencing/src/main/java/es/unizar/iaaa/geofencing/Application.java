package es.unizar.iaaa.geofencing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;


@SpringBootApplication
public class Application implements EmbeddedServletContainerCustomizer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        ((TomcatEmbeddedServletContainerFactory) container).
                addContextCustomizers((TomcatContextCustomizer) context -> context.setUseHttpOnly(false));
    }
}