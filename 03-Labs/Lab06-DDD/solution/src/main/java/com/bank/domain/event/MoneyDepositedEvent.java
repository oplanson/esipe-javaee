package com.bank.domain.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Money;
import com.bank.model.Account;

import java.time.LocalDateTime;

/**
 * MoneyDepositedEvent Domain Event.
 * 
 * DDD Pattern: Domain Event
 * - Represents something that happened in the domain
 * - Immutable (all fields final)
 * - Contains all relevant information about the event
 * - Used for event-driven architecture and audit trail
 */
public class MoneyDepositedEvent {
    
    private final Account account;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime occurredAt;
    
    /**
     * Constructor for MoneyDepositedEvent.
     * 
     * @param account The account where money was deposited
     * @param amount The amount deposited
     * @param balanceAfter The balance after deposit
     */
    public MoneyDepositedEvent(Account account, Money amount, Money balanceAfter) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (balanceAfter == null) {
            throw new IllegalArgumentException("Balance after cannot be null");
        }
        
        this.account = account;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.occurredAt = LocalDateTime.now();
    }
    
    public Account getAccount() {
        return account;
    }
    
    public Money getAmount() {
        return amount;
    }
    
    public Money getBalanceAfter() {
        return balanceAfter;
    }
    
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    public Long getAccountId() {
        return account != null ? account.getId() : null;
    }
    
    public String getAccountNumber() {
        return account != null ? account.getNumber() : null;
    }
    
    @Override
    public String toString() {
        return "MoneyDepositedEvent{" +
                "accountId=" + getAccountId() +
                ", accountNumber='" + getAccountNumber() + '\'' +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", occurredAt=" + occurredAt +
                '}';
    }
}

// Made with Bob