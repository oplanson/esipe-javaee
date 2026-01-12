// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Account entity for Lab 02B - JSF Client Management
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Long clientId;
    
    // Constructors
    public Account() {
        this.balance = BigDecimal.ZERO;
    }
    
    public Account(Long id, String accountNumber, String accountType, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }
    
    public Account(Long id, String accountNumber, String accountType, BigDecimal balance, Long clientId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.clientId = clientId;
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
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", clientId=" + clientId +
                '}';
    }
}

// Made with Bob
