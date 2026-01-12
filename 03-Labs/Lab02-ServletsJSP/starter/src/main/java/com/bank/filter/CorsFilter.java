package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * CORS Filter - Exercise 4
 * 
 * TODO: Implement CORS filter that:
 * 1. Handles Cross-Origin Resource Sharing for REST APIs
 * 2. Configures CORS headers (Access-Control-Allow-*)
 * 3. Handles preflight requests (OPTIONS method)
 * 4. Validates allowed origins
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = "/api/*")
public class CorsFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    
    // TODO: Define CORS configuration constants
    private static final String ALLOWED_ORIGINS = ""; // TODO: Add allowed origins
    private static final String ALLOWED_METHODS = ""; // TODO: Add allowed methods
    private static final String ALLOWED_HEADERS = ""; // TODO: Add allowed headers
    private static final String EXPOSED_HEADERS = ""; // TODO: Add exposed headers
    private static final String MAX_AGE = ""; // TODO: Set max age
    private static final String ALLOW_CREDENTIALS = ""; // TODO: Set allow credentials
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CorsFilter initialized");
        // TODO: Log CORS configuration
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // TODO: Get origin header from request
        String origin = null; // TODO: Get "Origin" header
        String method = null; // TODO: Get HTTP method
        
        LOGGER.fine("Processing CORS request - Origin: " + origin + ", Method: " + method);
        
        // TODO: Check if origin is allowed
        if (false) { // TODO: Check if origin is not null and is allowed
            // TODO: Set CORS headers
            // - Access-Control-Allow-Origin
            // - Access-Control-Allow-Methods
            // - Access-Control-Allow-Headers
            // - Access-Control-Expose-Headers
            // - Access-Control-Max-Age
            // - Access-Control-Allow-Credentials
            
            LOGGER.fine("CORS headers set for origin: " + origin);
        } else if (origin != null) {
            LOGGER.warning("Origin not allowed: " + origin);
        }
        
        // TODO: Handle preflight requests (OPTIONS method)
        if (false) { // TODO: Check if method is OPTIONS
            LOGGER.info("Handling preflight request from origin: " + origin);
            // TODO: Set response status to OK
            // TODO: Return without continuing filter chain
        }
        
        // TODO: Continue with the filter chain for actual requests
    }
    
    @Override
    public void destroy() {
        LOGGER.info("CorsFilter destroyed");
    }
    
    /**
     * TODO: Implement method to check if origin is allowed
     */
    private boolean isOriginAllowed(String origin) {
        // TODO: Check if origin is in the allowed list
        return false;
    }
}

// Made with Bob
