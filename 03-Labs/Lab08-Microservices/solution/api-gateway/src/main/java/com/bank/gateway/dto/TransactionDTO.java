// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Transaction operations
 */
public class TransactionDTO {
    
    private BigDecimal amount;
    private String description;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionDTO(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

// Made with Bob
