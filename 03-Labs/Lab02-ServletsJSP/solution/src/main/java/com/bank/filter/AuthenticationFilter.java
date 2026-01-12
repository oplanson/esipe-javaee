package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication Filter - Exercise 1
 * 
 * This filter checks if a user is authenticated before allowing access to protected resources.
 * It demonstrates:
 * - Session management
 * - URL pattern matching
 * - Request/response manipulation
 * - Filter chain processing
 * 
 * Protected URLs: /clients/*, /admin/*
 * Public URLs: /login, /logout, /public/*, /css/*, /js/*, /images/*
 * 
 * @author Olivier Planson
 * @version 1.0
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    // URLs that don't require authentication
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login",
        "/logout",
        "/public/",
        "/css/",
        "/js/",
        "/images/",
        "/index.html",
        "/"
    );
    
    // URLs that require authentication
    private static final List<String> PROTECTED_URLS = Arrays.asList(
        "/clients/",
        "/admin/"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        LOGGER.fine("Processing authentication for path: " + path);
        
        // Check if the URL is public (no authentication required)
        if (isPublicUrl(path)) {
            LOGGER.fine("Public URL, allowing access: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        // Check if the URL requires authentication
        if (isProtectedUrl(path)) {
            HttpSession session = httpRequest.getSession(false);
            
            // Check if user is authenticated
            if (session == null || session.getAttribute("user") == null) {
                LOGGER.warning("Unauthorized access attempt to: " + path);
                
                // Store the original URL to redirect after login
                httpRequest.getSession(true).setAttribute("originalUrl", requestURI);
                
                // Redirect to login page
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }
            
            // User is authenticated, log the access
            String username = (String) session.getAttribute("user");
            LOGGER.info("Authenticated user '" + username + "' accessing: " + path);
        }
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed");
    }
    
    /**
     * Check if the URL is public (doesn't require authentication)
     */
    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }
    
    /**
     * Check if the URL is protected (requires authentication)
     */
    private boolean isProtectedUrl(String path) {
        return PROTECTED_URLS.stream().anyMatch(path::startsWith);
    }
}

// Made with Bob
