// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.dto;

/**
 * Error response DTO
 */
public class ErrorResponse {
    
    private String error;
    private String message;
    private int status;
    
    // Constructors
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }
    
    public ErrorResponse(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}

// Made with Bob
