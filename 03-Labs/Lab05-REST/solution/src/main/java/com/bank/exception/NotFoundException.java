package com.bank.exception;

/**
 * Exception thrown when a resource is not found.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob
