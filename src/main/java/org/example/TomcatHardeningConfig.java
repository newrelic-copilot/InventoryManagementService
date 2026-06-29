package org.example;

import org.apache.catalina.Wrapper;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatHardeningConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatSecurityCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Wrapper defaultServlet = (Wrapper) context.findChild("default");
            if (defaultServlet != null) {
                hardenDefaultServlet(defaultServlet);
            }
        });
    }

    void hardenDefaultServlet(Wrapper defaultServlet) {
        defaultServlet.addInitParameter("readonly", "true");
        defaultServlet.addInitParameter("allowPartialPut", "false");
    }
}
