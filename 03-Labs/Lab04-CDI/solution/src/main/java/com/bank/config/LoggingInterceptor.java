package com.bank.config;

/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * CDI Interceptor for automatic method logging.
 * 
 * This interceptor logs:
 * - Method entry with parameters
 * - Method exit with return value
 * - Exceptions with stack trace
 * - Execution time
 * 
 * Activated by the @Logged annotation.
 */
@Logged
@Interceptor
@jakarta.annotation.Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {
    
    @Inject
    private Logger logger;
    
    /**
     * Intercept method invocation to add logging.
     * 
     * @param context The invocation context
     * @return The method result
     * @throws Exception If the intercepted method throws an exception
     */
    @AroundInvoke
    public Object logMethod(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();
        Object[] parameters = context.getParameters();
        
        // Log method entry
        logger.info(String.format("→ Entering: %s.%s(%s)", 
            className, 
            methodName,
            formatParameters(parameters)));
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Proceed with the method invocation
            Object result = context.proceed();
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Log method exit
            logger.info(String.format("← Exiting: %s.%s - Duration: %dms - Result: %s", 
                className, 
                methodName,
                duration,
                formatResult(result)));
            
            return result;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log exception
            logger.severe(String.format("✗ Exception in: %s.%s - Duration: %dms - Error: %s", 
                className, 
                methodName,
                duration,
                e.getMessage()));
            
            throw e;
        }
    }
    
    /**
     * Format method parameters for logging.
     * 
     * @param parameters The method parameters
     * @return Formatted parameter string
     */
    private String formatParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "";
        }
        
        return Arrays.stream(parameters)
            .map(this::formatObject)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
    
    /**
     * Format result for logging.
     * 
     * @param result The method result
     * @return Formatted result string
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        return formatObject(result);
    }
    
    /**
     * Format an object for logging.
     * Handles collections and limits string length.
     * 
     * @param obj The object to format
     * @return Formatted string
     */
    private String formatObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        String str = obj.toString();
        
        // Limit string length to avoid huge logs
        if (str.length() > 100) {
            return str.substring(0, 97) + "...";
        }
        
        return str;
    }
}

// Made with Bob
