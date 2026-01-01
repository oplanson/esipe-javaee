package com.bank.exception;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.List;

/**
 * Exception thrown when validation fails.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 06
 */
public class ValidationException extends RuntimeException {
    
    private List<String> errors;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}

// Made with Bob
