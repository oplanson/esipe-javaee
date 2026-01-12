package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Logging Filter - Exercise 2
 * 
 * TODO: Implement logging filter that:
 * 1. Logs all HTTP requests with details (method, URI, parameters)
 * 2. Measures execution time for each request
 * 3. Logs response status code
 * 4. Provides performance monitoring
 * 
 * @author Your Name
 * @version 1.0
 */
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("LoggingFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // TODO: Record start time
        long startTime = 0; // TODO: Get current time in milliseconds
        
        // TODO: Log request details (call logRequest method)
        
        // TODO: Wrap response to capture status code
        ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);
        
        try {
            // TODO: Continue with the filter chain
        } finally {
            // TODO: Record end time and calculate duration
            long endTime = 0; // TODO: Get current time
            long duration = 0; // TODO: Calculate duration
            
            // TODO: Log response details (call logResponse method)
        }
    }
    
    @Override
    public void destroy() {
        LOGGER.info("LoggingFilter destroyed");
    }
    
    /**
     * TODO: Implement method to log incoming request details
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Incoming Request ==========\n");
        
        // TODO: Log HTTP method
        // TODO: Log request URI
        // TODO: Log query string
        // TODO: Log remote address
        
        // TODO: Log request parameters
        logMessage.append("Parameters:\n");
        // TODO: Iterate through parameter names and values
        
        // TODO: Log session info if exists
        
        logMessage.append("======================================");
        LOGGER.info(logMessage.toString());
    }
    
    /**
     * TODO: Implement method to log response details with timing
     */
    private void logResponse(HttpServletRequest request, ResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Outgoing Response ==========\n");
        
        // TODO: Log HTTP method
        // TODO: Log request URI
        // TODO: Log status code
        // TODO: Log content type
        // TODO: Log execution time
        
        // TODO: Add warning for slow requests (> 1 second)
        
        logMessage.append("=======================================");
        LOGGER.info(logMessage.toString());
    }
    
    /**
     * Response wrapper to capture status code
     * TODO: Complete the implementation
     */
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        private int status = HttpServletResponse.SC_OK;
        
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void setStatus(int sc) {
            // TODO: Store status code and call super
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            // TODO: Store status code and call super
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            // TODO: Store status code and call super
        }
        
        @Override
        public void sendRedirect(String location) throws IOException {
            // TODO: Store status code (SC_FOUND) and call super
        }
        
        public int getStatus() {
            return status;
        }
    }
}

// Made with Bob
