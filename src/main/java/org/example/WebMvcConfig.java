package org.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * MVC configuration that restricts static resource serving to a designated safe
 * directory and applies path normalisation to prevent directory traversal.
 *
 * Mitigates CVE-2024-38819: path traversal in Spring WebMvc static resource handling.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** Classpath location that is the only permitted root for static resources. */
    private static final String STATIC_RESOURCE_LOCATION = "classpath:/static/";

    /** URL pattern that maps to the static resource location above. */
    private static final String STATIC_RESOURCE_PATTERN = "/static/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(STATIC_RESOURCE_PATTERN)
                .addResourceLocations(STATIC_RESOURCE_LOCATION)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected org.springframework.core.io.Resource getResource(
                            String resourcePath,
                            org.springframework.core.io.Resource location) throws java.io.IOException {

                        // Reject any resource path that contains traversal sequences
                        if (PathTraversalFilter.containsPathTraversal(resourcePath)) {
                            return null;
                        }

                        org.springframework.core.io.Resource resource =
                                location.createRelative(resourcePath);

                        // Ensure the resolved resource actually resides inside the
                        // permitted base location and is readable.
                        if (resource.exists() && resource.isReadable()
                                && isWithinLocation(location, resource)) {
                            return resource;
                        }
                        return null;
                    }

                    /**
                     * Verifies that {@code resource} is contained within {@code location} so
                     * that a crafted path cannot escape the intended base directory.
                     */
                    private boolean isWithinLocation(
                            org.springframework.core.io.Resource location,
                            org.springframework.core.io.Resource resource) {
                        try {
                            String locationPath = location.getURL().toExternalForm();
                            String resourcePath = resource.getURL().toExternalForm();
                            return resourcePath.startsWith(locationPath);
                        } catch (java.io.IOException e) {
                            return false;
                        }
                    }
                });
    }
}
