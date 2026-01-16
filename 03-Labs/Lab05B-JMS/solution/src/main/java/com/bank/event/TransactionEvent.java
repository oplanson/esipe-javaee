// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event representing a banking transaction.
 * This event is sent via JMS when transactions occur.
 */
public class TransactionEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long transactionId;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
    private String description;
    private String customerEmail;
    
    // Default constructor
    public TransactionEvent() {
        this.timestamp = LocalDateTime.now();
        this.status = "SUCCESS";
    }
    
    // Constructor with essential fields
    public TransactionEvent(Long transactionId, Long accountId, String accountNumber, 
                           BigDecimal amount, String type) {
        this();
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
    }
    
    // Full constructor
    public TransactionEvent(Long transactionId, Long accountId, String accountNumber, 
                           BigDecimal amount, String type, String status, 
                           String description, String customerEmail) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.description = description;
        this.customerEmail = customerEmail;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

// Made with Bob