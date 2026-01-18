// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * JAX-RS filter to add security headers to all responses
 */
@Provider
public class SecurityHeadersFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) throws IOException {
        
        // Prevent clickjacking attacks
        responseContext.getHeaders().add("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        responseContext.getHeaders().add("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        responseContext.getHeaders().add("X-XSS-Protection", "1; mode=block");
        
        // Content Security Policy
        responseContext.getHeaders().add("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'");
        
        // Referrer Policy
        responseContext.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy (formerly Feature Policy)
        responseContext.getHeaders().add("Permissions-Policy", 
                "geolocation=(), microphone=(), camera=()");
        
        // Strict Transport Security (HSTS) - only for HTTPS
        if (requestContext.getUriInfo().getRequestUri().getScheme().equals("https")) {
            responseContext.getHeaders().add("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains");
        }
    }
}

// Made with Bob
