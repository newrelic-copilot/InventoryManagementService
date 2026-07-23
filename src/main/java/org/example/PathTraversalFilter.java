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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
     * The URI is decoded iteratively until it stabilises so that double- and
     * multi-encoded variants (e.g. {@code %252e%252e}, {@code %2e%2e}) are also
     * detected.
     */
    static boolean containsPathTraversal(String uri) {
        if (uri == null) {
            return false;
        }

        String current = uri;
        // Iteratively decode until the value no longer changes (handles double/multi encoding)
        while (true) {
            if (hasTraversalSegment(current)) {
                return true;
            }
            String decoded;
            try {
                decoded = URLDecoder.decode(current, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                // Malformed encoding – treat as suspicious
                return true;
            }
            if (decoded.equals(current)) {
                break; // fully decoded, no traversal found
            }
            current = decoded;
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
