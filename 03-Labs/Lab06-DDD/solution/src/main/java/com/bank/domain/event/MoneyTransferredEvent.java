package com.bank.domain.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Money;
import com.bank.model.Account;

import java.time.LocalDateTime;

/**
 * MoneyTransferredEvent Domain Event.
 * 
 * DDD Pattern: Domain Event
 * - Represents something that happened in the domain
 * - Immutable (all fields final)
 * - Contains all relevant information about the event
 * - Used for event-driven architecture and audit trail
 */
public class MoneyTransferredEvent {
    
    private final Account fromAccount;
    private final Account toAccount;
    private final Money amount;
    private final Money fromBalanceAfter;
    private final Money toBalanceAfter;
    private final LocalDateTime occurredAt;
    
    /**
     * Constructor for MoneyTransferredEvent.
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount transferred
     * @param fromBalanceAfter The source account balance after transfer
     * @param toBalanceAfter The destination account balance after transfer
     */
    public MoneyTransferredEvent(Account fromAccount, Account toAccount, Money amount,
                                 Money fromBalanceAfter, Money toBalanceAfter) {
        if (fromAccount == null) {
            throw new IllegalArgumentException("From account cannot be null");
        }
        if (toAccount == null) {
            throw new IllegalArgumentException("To account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (fromBalanceAfter == null) {
            throw new IllegalArgumentException("From balance after cannot be null");
        }
        if (toBalanceAfter == null) {
            throw new IllegalArgumentException("To balance after cannot be null");
        }
        
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.fromBalanceAfter = fromBalanceAfter;
        this.toBalanceAfter = toBalanceAfter;
        this.occurredAt = LocalDateTime.now();
    }
    
    public Account getFromAccount() {
        return fromAccount;
    }
    
    public Account getToAccount() {
        return toAccount;
    }
    
    public Money getAmount() {
        return amount;
    }
    
    public Money getFromBalanceAfter() {
        return fromBalanceAfter;
    }
    
    public Money getToBalanceAfter() {
        return toBalanceAfter;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    public Long getFromAccountId() {
        return fromAccount != null ? fromAccount.getId() : null;
    }
    
    public String getFromAccountNumber() {
        return fromAccount != null ? fromAccount.getNumber() : null;
    }
    
    public Long getToAccountId() {
        return toAccount != null ? toAccount.getId() : null;
    }
    
    public String getToAccountNumber() {
        return toAccount != null ? toAccount.getNumber() : null;
    }
    
    @Override
    public String toString() {
        return "MoneyTransferredEvent{" +
                "fromAccountId=" + getFromAccountId() +
                ", fromAccountNumber='" + getFromAccountNumber() + '\'' +
                ", toAccountId=" + getToAccountId() +
                ", toAccountNumber='" + getToAccountNumber() + '\'' +
                ", amount=" + amount +
                ", fromBalanceAfter=" + fromBalanceAfter +
                ", toBalanceAfter=" + toBalanceAfter +
                ", occurredAt=" + occurredAt +
                '}';
    }
}

// Made with Bob