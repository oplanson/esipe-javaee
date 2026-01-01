package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.AccountType;
import com.bank.model.Account;

import java.math.BigDecimal;

/**
 * AccountDTO for transferring account data between layers.
 * 
 * DDD Pattern: Data Transfer Object (DTO)
 * - Separates domain model from presentation/API layer
 * - Provides a stable interface for external consumers
 * - Prevents exposing domain model internals
 * - Simplifies serialization (JSON, XML)
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
    
    /**
     * Create DTO from Account entity.
     * 
     * @param account The account entity
     * @return AccountDTO
     */
    public static AccountDTO fromEntity(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountDTO dto = new AccountDTO();
        dto.id = account.getId();
        dto.accountNumber = account.getAccountNumber() != null ? 
            account.getAccountNumber().getValue() : null;
        dto.balance = account.getBalance() != null ? 
            account.getBalance().getAmount() : BigDecimal.ZERO;
        dto.currency = account.getBalance() != null ? 
            account.getBalance().getCurrency() : "EUR";
        dto.accountType = account.getAccountType() != null ? 
            account.getAccountType().name() : null;
        dto.clientId = account.getClientId();
        dto.clientName = account.getClient() != null ? 
            account.getClient().getName() : null;
        
        return dto;
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
    
    /**
     * Get account type display name.
     * 
     * @return Display name
     */
    public String getAccountTypeDisplayName() {
        if (accountType == null) {
            return "";
        }
        try {
            return AccountType.valueOf(accountType).getDisplayName();
        } catch (IllegalArgumentException e) {
            return accountType;
        }
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