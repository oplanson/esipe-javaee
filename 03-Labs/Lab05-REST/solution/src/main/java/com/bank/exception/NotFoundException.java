package com.bank.exception;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

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
