package com.bank.event;

import com.bank.model.Account;

/**
 * CDI Event fired when a financial transaction occurs.
 * Demonstrates CDI event-driven architecture with qualifiers.
 *
 * Lab 05 - JAX-RS: Events (from Lab 04)
 */
public class TransactionEvent {
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
    
    private final Account account;
    private final TransactionType type;
    private final double amount;
    private final Long targetAccountId; // For transfers
    private final String performedBy;
    private final long timestamp;
    
    public TransactionEvent(Account account, TransactionType type, double amount) {
        this(account, type, amount, null, "system");
    }
    
    public TransactionEvent(Account account, TransactionType type, double amount, Long targetAccountId) {
        this(account, type, amount, targetAccountId, "system");
    }
    
    public TransactionEvent(Account account, TransactionType type, double amount, Long targetAccountId, String performedBy) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.targetAccountId = targetAccountId;
        this.performedBy = performedBy;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Account getAccount() {
        return account;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public Long getTargetAccountId() {
        return targetAccountId;
    }
    
    public String getPerformedBy() {
        return performedBy;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "TransactionEvent{" +
                "accountId=" + (account != null ? account.getId() : "null") +
                ", type=" + type +
                ", amount=" + amount +
                ", targetAccountId=" + targetAccountId +
                ", performedBy='" + performedBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

// Made with Bob