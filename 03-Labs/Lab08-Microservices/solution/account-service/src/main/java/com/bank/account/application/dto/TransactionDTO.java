// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Transaction Data Transfer Object
 * Used for deposit, withdrawal, and transfer operations
 */
public class TransactionDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    private Long targetAccountId;
    private String description;
    
    // Constructors
    
    public TransactionDTO() {
    }
    
    public TransactionDTO(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionDTO(BigDecimal amount, Long targetAccountId) {
        this.amount = amount;
        this.targetAccountId = targetAccountId;
    }
    
    public TransactionDTO(BigDecimal amount, Long targetAccountId, String description) {
        this.amount = amount;
        this.targetAccountId = targetAccountId;
        this.description = description;
    }
    
    // Getters and Setters
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Long getTargetAccountId() {
        return targetAccountId;
    }
    
    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "TransactionDTO{" +
                "amount=" + amount +
                ", targetAccountId=" + targetAccountId +
                ", description='" + description + '\'' +
                '}';
    }
}

// Made with Bob
