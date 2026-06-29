package org.example;

import org.apache.catalina.Wrapper;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class TomcatHardeningConfig {

    private static final Logger LOGGER = Logger.getLogger(TomcatHardeningConfig.class.getName());

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatSecurityCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Wrapper defaultServlet = (Wrapper) context.findChild("default");
            if (defaultServlet != null) {
                hardenDefaultServlet(defaultServlet);
            } else {
                LOGGER.warning("Tomcat default servlet not found; unable to enforce partial PUT hardening.");
            }
        });
    }

    private void hardenDefaultServlet(Wrapper defaultServlet) {
        defaultServlet.addInitParameter("readonly", "true");
        defaultServlet.addInitParameter("allowPartialPut", "false");
    }
}
