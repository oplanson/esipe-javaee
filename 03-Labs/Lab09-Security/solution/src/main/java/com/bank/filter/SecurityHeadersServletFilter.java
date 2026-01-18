// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet filter to add security headers to ALL responses (including static content)
 */
@WebFilter(urlPatterns = "/*")
public class SecurityHeadersServletFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent clickjacking attacks
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'");
        
        // Referrer Policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy (formerly Feature Policy)
        httpResponse.setHeader("Permissions-Policy", 
                "geolocation=(), microphone=(), camera=()");
        
        // Strict Transport Security (HSTS) - only for HTTPS
        if (httpRequest.getScheme().equals("https")) {
            httpResponse.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains");
        }
        
        // Continue the filter chain
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // No cleanup needed
    }
}

// Made with Bob