// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * JAX-RS filter to handle CORS (Cross-Origin Resource Sharing)
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) throws IOException {
        
        // Allow requests from specific origins (configure as needed)
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        
        // Allow specific HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", 
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        
        // Allow specific headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", 
                "Content-Type, Authorization, X-Requested-With, Accept, Origin");
        
        // Cache preflight response for 1 hour
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
        
        // Expose specific headers to the client
        responseContext.getHeaders().add("Access-Control-Expose-Headers", 
                "Authorization, Content-Type");
    }
}

// Made with Bob
