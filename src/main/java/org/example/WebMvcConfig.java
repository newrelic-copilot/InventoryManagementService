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
                     * Verifies that {@code resource} is strictly contained within {@code location}
                     * so that a crafted path cannot escape the intended base directory.
                     * Canonical file paths are used where available to neutralise any remaining
                     * encoding or normalisation differences.  The separator is always appended to
                     * the location path before the prefix check to prevent partial-name matches
                     * (e.g. {@code /app/static-evil} being accepted when location is
                     * {@code /app/static}).
                     */
                    private boolean isWithinLocation(
                            org.springframework.core.io.Resource location,
                            org.springframework.core.io.Resource resource) {
                        try {
                            // Prefer canonical File paths (resolves symlinks, normalises ..)
                            java.io.File locationFile = location.getFile().getCanonicalFile();
                            java.io.File resourceFile = resource.getFile().getCanonicalFile();
                            // Require the resource to be strictly inside the location directory
                            String locationPrefix = locationFile.getPath() + java.io.File.separator;
                            return resourceFile.getPath().startsWith(locationPrefix);
                        } catch (java.io.IOException fileException) {
                            // Fall back to URL comparison for non-file resources (e.g. classpath jars)
                            try {
                                String locationUrl = location.getURL().toExternalForm();
                                if (!locationUrl.endsWith("/")) {
                                    locationUrl = locationUrl + "/";
                                }
                                String resourceUrl = resource.getURL().toExternalForm();
                                // Normalise both URLs to lower-case for case-insensitive file systems
                                return resourceUrl.toLowerCase(java.util.Locale.ROOT)
                                        .startsWith(locationUrl.toLowerCase(java.util.Locale.ROOT));
                            } catch (java.io.IOException urlException) {
                                return false;
                            }
                        }
                    }
                });
    }
}
