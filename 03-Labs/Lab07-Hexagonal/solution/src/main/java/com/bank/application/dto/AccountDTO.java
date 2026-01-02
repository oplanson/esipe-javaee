/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.math.BigDecimal;

/**
 * AccountDTO for transferring account data between layers.
 * 
 * Hexagonal Architecture: DTO in Application Layer
 * - Part of the application layer (use cases)
 * - Used by both primary and secondary adapters
 * - Separates domain model from external representations
 * - Provides a stable interface for adapters
 */
public class AccountDTO {
    
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String accountType;
    private Long clientId;
    private String clientName;
    
    /**
     * Default constructor.
     */
    public AccountDTO() {
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    /**
     * Get balance as double for backward compatibility.
     * 
     * @return Balance as double
     */
    public double getBalanceAsDouble() {
        return balance != null ? balance.doubleValue() : 0.0;
    }
    
    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", accountType='" + accountType + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}

// Made with Bob
