package org.example;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Servlet filter that rejects HTTP requests containing path traversal patterns.
 * Mitigates CVE-2024-38819 at the request-handling layer by blocking any request
 * whose decoded URI contains directory traversal sequences before it reaches
 * the Spring resource handler.
 */
@Component
@Order(1)
public class PathTraversalFilter implements Filter {

    private static final Logger logger = Logger.getLogger(PathTraversalFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no initialisation needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        if (containsPathTraversal(requestUri)) {
            logger.warning("Rejected request with path traversal pattern: " + requestUri);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid request path");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no cleanup needed
    }

    /**
     * Returns {@code true} if the given URI contains a directory traversal sequence.
     * Both raw and URL-decoded forms are checked so that encoded variants such as
     * {@code %2e%2e} or {@code %2F} are also caught.
     */
    static boolean containsPathTraversal(String uri) {
        if (uri == null) {
            return false;
        }

        // Check raw URI
        if (hasTraversalSegment(uri)) {
            return true;
        }

        // Check decoded URI
        try {
            String decoded = new URI(uri).getPath();
            if (decoded != null && hasTraversalSegment(decoded)) {
                return true;
            }
        } catch (URISyntaxException ignored) {
            // If the URI cannot be parsed, treat it as suspicious
            return true;
        }

        return false;
    }

    private static boolean hasTraversalSegment(String path) {
        // Normalise path separators before checking
        String normalised = path.replace('\\', '/');
        return normalised.contains("/../")
                || normalised.contains("/./")
                || normalised.endsWith("/..")
                || normalised.endsWith("/.")
                || normalised.startsWith("../")
                || normalised.equals("..")
                || normalised.equals(".");
    }
}
