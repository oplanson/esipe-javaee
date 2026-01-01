package com.bank.event;

/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;

/**
 * CDI Event fired when a new account is created.
 * Demonstrates CDI event-driven architecture.
 * 
 * Lab 04 - Advanced CDI: Events
 */
public class AccountCreatedEvent {
    
    private final Account account;
    private final Long clientId;
    private final String createdBy;
    private final long timestamp;
    
    public AccountCreatedEvent(Account account) {
        this(account, "system");
    }
    
    public AccountCreatedEvent(Account account, String createdBy) {
        this.account = account;
        this.clientId = account != null && account.getClient() != null 
            ? account.getClient().getId() 
            : null;
        this.createdBy = createdBy;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Account getAccount() {
        return account;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "AccountCreatedEvent{" +
                "accountId=" + (account != null ? account.getId() : "null") +
                ", accountNumber='" + (account != null ? account.getNumber() : "null") + '\'' +
                ", clientId=" + clientId +
                ", createdBy='" + createdBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

// Made with Bob
