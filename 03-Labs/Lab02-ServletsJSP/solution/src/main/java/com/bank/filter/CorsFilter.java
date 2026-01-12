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
 * This filter handles Cross-Origin Resource Sharing (CORS) for REST API endpoints.
 * It demonstrates:
 * - CORS header configuration
 * - Preflight request handling (OPTIONS)
 * - Security best practices for cross-origin requests
 * - Allowed origins, methods, and headers configuration
 * 
 * CORS Configuration:
 * - Allowed Origins: http://localhost:3000, http://localhost:4200
 * - Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
 * - Allowed Headers: Content-Type, Authorization, X-Requested-With
 * - Max Age: 3600 seconds (1 hour)
 * 
 * @author Olivier Planson
 * @version 1.0
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = "/api/*")
public class CorsFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    
    // CORS configuration
    private static final String ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:4200,http://localhost:8080";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final String ALLOWED_HEADERS = "Content-Type, Authorization, X-Requested-With, Accept, Origin";
    private static final String EXPOSED_HEADERS = "Location, Content-Disposition";
    private static final String MAX_AGE = "3600"; // 1 hour
    private static final String ALLOW_CREDENTIALS = "true";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CorsFilter initialized");
        LOGGER.info("Allowed Origins: " + ALLOWED_ORIGINS);
        LOGGER.info("Allowed Methods: " + ALLOWED_METHODS);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        String method = httpRequest.getMethod();
        
        LOGGER.fine("Processing CORS request - Origin: " + origin + ", Method: " + method);
        
        // Check if origin is allowed
        if (origin != null && isOriginAllowed(origin)) {
            // Set CORS headers
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
            httpResponse.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
            httpResponse.setHeader("Access-Control-Expose-Headers", EXPOSED_HEADERS);
            httpResponse.setHeader("Access-Control-Max-Age", MAX_AGE);
            httpResponse.setHeader("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
            
            LOGGER.fine("CORS headers set for origin: " + origin);
        } else if (origin != null) {
            LOGGER.warning("Origin not allowed: " + origin);
        }
        
        // Handle preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            LOGGER.info("Handling preflight request from origin: " + origin);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return; // Don't continue the filter chain for OPTIONS requests
        }
        
        // Continue with the filter chain for actual requests
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.info("CorsFilter destroyed");
    }
    
    /**
     * Check if the origin is in the allowed list
     */
    private boolean isOriginAllowed(String origin) {
        if (origin == null || origin.trim().isEmpty()) {
            return false;
        }
        
        String[] allowedOrigins = ALLOWED_ORIGINS.split(",");
        for (String allowedOrigin : allowedOrigins) {
            if (allowedOrigin.trim().equals(origin)) {
                return true;
            }
        }
        
        return false;
    }
}

// Made with Bob
