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
 * This filter logs all HTTP requests and responses with timing information.
 * It demonstrates:
 * - Request/response logging
 * - Performance monitoring
 * - Parameter extraction
 * - Execution time measurement
 * 
 * Logs include:
 * - HTTP method and URI
 * - Request parameters
 * - Response status code
 * - Execution time in milliseconds
 * 
 * @author Olivier Planson
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
        
        // Record start time
        long startTime = System.currentTimeMillis();
        
        // Log request details
        logRequest(httpRequest);
        
        // Wrap response to capture status code
        ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);
        
        try {
            // Continue with the filter chain
            chain.doFilter(request, responseWrapper);
        } finally {
            // Record end time and calculate duration
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Log response details
            logResponse(httpRequest, responseWrapper, duration);
        }
    }
    
    @Override
    public void destroy() {
        LOGGER.info("LoggingFilter destroyed");
    }
    
    /**
     * Log incoming request details
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Incoming Request ==========\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Query String: ").append(request.getQueryString()).append("\n");
        logMessage.append("Remote Address: ").append(request.getRemoteAddr()).append("\n");
        
        // Log request parameters
        logMessage.append("Parameters:\n");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            logMessage.append("  ").append(paramName).append(" = ");
            if (paramValues.length == 1) {
                logMessage.append(paramValues[0]);
            } else {
                logMessage.append(java.util.Arrays.toString(paramValues));
            }
            logMessage.append("\n");
        }
        
        // Log session info
        HttpSession session = request.getSession(false);
        if (session != null) {
            logMessage.append("Session ID: ").append(session.getId()).append("\n");
            logMessage.append("Session Created: ").append(new java.util.Date(session.getCreationTime())).append("\n");
        }
        
        logMessage.append("======================================");
        LOGGER.info(logMessage.toString());
    }
    
    /**
     * Log response details with timing information
     */
    private void logResponse(HttpServletRequest request, ResponseWrapper response, long duration) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Outgoing Response ==========\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Status Code: ").append(response.getStatus()).append("\n");
        logMessage.append("Content Type: ").append(response.getContentType()).append("\n");
        logMessage.append("Execution Time: ").append(duration).append(" ms\n");
        
        // Performance warning for slow requests
        if (duration > 1000) {
            logMessage.append("⚠️  WARNING: Slow request detected (> 1 second)\n");
        }
        
        logMessage.append("=======================================");
        LOGGER.info(logMessage.toString());
    }
    
    /**
     * Response wrapper to capture status code
     */
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        private int status = HttpServletResponse.SC_OK;
        
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void setStatus(int sc) {
            this.status = sc;
            super.setStatus(sc);
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            this.status = sc;
            super.sendError(sc);
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.status = sc;
            super.sendError(sc, msg);
        }
        
        @Override
        public void sendRedirect(String location) throws IOException {
            this.status = HttpServletResponse.SC_FOUND;
            super.sendRedirect(location);
        }
        
        public int getStatus() {
            return status;
        }
    }
}

// Made with Bob
