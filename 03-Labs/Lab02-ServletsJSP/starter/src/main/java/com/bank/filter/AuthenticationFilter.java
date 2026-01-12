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
 * TODO: Implement authentication filter that:
 * 1. Checks if user is authenticated before accessing protected resources
 * 2. Redirects to login page if not authenticated
 * 3. Manages user sessions
 * 4. Protects specific URL patterns
 * 
 * Protected URLs: /clients/*, /admin/*
 * Public URLs: /login, /logout, /public/*, /css/*, /js/*, /images/*
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    // TODO: Define lists of public and protected URLs
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        // TODO: Add public URLs that don't require authentication
    );
    
    private static final List<String> PROTECTED_URLS = Arrays.asList(
        // TODO: Add protected URLs that require authentication
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO: Initialize the filter
        LOGGER.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // TODO: Get the request URI and extract the path
        String requestURI = null; // TODO: Get request URI
        String contextPath = null; // TODO: Get context path
        String path = null; // TODO: Extract path from URI
        
        LOGGER.fine("Processing authentication for path: " + path);
        
        // TODO: Check if the URL is public (no authentication required)
        if (false) { // TODO: Replace with isPublicUrl(path)
            LOGGER.fine("Public URL, allowing access: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        // TODO: Check if the URL requires authentication
        if (false) { // TODO: Replace with isProtectedUrl(path)
            // TODO: Get the session (don't create if doesn't exist)
            HttpSession session = null; // TODO: Get session
            
            // TODO: Check if user is authenticated (check session and "user" attribute)
            if (false) { // TODO: Check authentication
                LOGGER.warning("Unauthorized access attempt to: " + path);
                
                // TODO: Store the original URL to redirect after login
                // TODO: Create new session and store originalUrl
                
                // TODO: Redirect to login page
                return;
            }
            
            // TODO: User is authenticated, log the access
            String username = null; // TODO: Get username from session
            LOGGER.info("Authenticated user '" + username + "' accessing: " + path);
        }
        
        // TODO: Continue with the filter chain
    }
    
    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed");
    }
    
    /**
     * TODO: Implement method to check if the URL is public
     */
    private boolean isPublicUrl(String path) {
        // TODO: Check if path starts with any public URL
        return false;
    }
    
    /**
     * TODO: Implement method to check if the URL is protected
     */
    private boolean isProtectedUrl(String path) {
        // TODO: Check if path starts with any protected URL
        return false;
    }
}

// Made with Bob
