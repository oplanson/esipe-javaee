/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.domain.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.event.DomainEvent;
import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Account aggregate root - Pure domain entity with no infrastructure dependencies.
 * Contains business logic for account operations.
 */
public class Account {
    private Long id;
    private AccountNumber accountNumber;
    private Money balance;
    private AccountType accountType;
    private Client client;
    private boolean active;
    private List<DomainEvent> domainEvents = new ArrayList<>();

    // Default constructor for frameworks
    protected Account() {
    }

    // Constructor for creating new accounts
    public Account(AccountNumber accountNumber, Money initialBalance, AccountType accountType, Client client) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        this.balance = Objects.requireNonNull(initialBalance, "Initial balance cannot be null");
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null");
        this.client = Objects.requireNonNull(client, "Client cannot be null");
        this.active = true;
        
        validateInitialBalance(initialBalance);
    }

    // Constructor for reconstituting from persistence (with Client object)
    public Account(Long id, AccountNumber accountNumber, Money balance, AccountType accountType, Client client, boolean active) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.client = client;
        this.active = active;
    }

    // Constructor for reconstituting from persistence (with clientId only - for lazy loading)
    public Account(Long id, AccountNumber accountNumber, Money balance, AccountType accountType, Long clientId, boolean active) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.client = null; // Will be loaded lazily if needed
        this.active = active;
    }

    /**
     * Factory method to open a new account
     */
    public static Account open(AccountNumber accountNumber, Money initialBalance, AccountType accountType, Client client) {
        return new Account(accountNumber, initialBalance, accountType, client);
    }

    /**
     * Deposit money into the account
     */
    public void deposit(Money amount) {
        validateActive();
        validatePositiveAmount(amount);
        validateCurrency(amount);
        
        this.balance = this.balance.add(amount);
    }

    /**
     * Withdraw money from the account
     */
    public void withdraw(Money amount) {
        validateActive();
        validatePositiveAmount(amount);
        validateCurrency(amount);
        validateSufficientFunds(amount);
        
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Close the account
     */
    public void close() {
        validateActive();
        if (!balance.isZero()) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        this.active = false;
    }

    /**
     * Reactivate a closed account
     */
    public void reactivate() {
        if (this.active) {
            throw new IllegalStateException("Account is already active");
        }
        this.active = true;
    }

    /**
     * Transfer money to another account
     */
    public void transferTo(Account toAccount, Money amount) {
        validateActive();
        validatePositiveAmount(amount);
        validateCurrency(amount);
        validateSufficientFunds(amount);
        
        this.withdraw(amount);
        toAccount.deposit(amount);
    }

    /**
     * Check if withdrawal is possible
     */
    public boolean canWithdraw(Money amount) {
        if (!this.active) {
            return false;
        }
        if (amount.isNegativeOrZero()) {
            return false;
        }
        if (!this.balance.hasSameCurrency(amount)) {
            return false;
        }
        return this.balance.isGreaterThanOrEqualTo(amount);
    }

    /**
     * Get the client ID (for infrastructure layer)
     */
    public Long getClientId() {
        return client != null ? client.getId() : null;
    }

    /**
     * Check if account is closed
     */
    public boolean isClosed() {
        return !active;
    }

    /**
     * Get account number as string
     */
    public String getNumber() {
        return accountNumber != null ? accountNumber.getValue() : null;
    }

    // Validation methods
    private void validateActive() {
        if (!this.active) {
            throw new IllegalStateException("Account is not active");
        }
    }

    private void validatePositiveAmount(Money amount) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void validateCurrency(Money amount) {
        if (!this.balance.hasSameCurrency(amount)) {
            throw new IllegalArgumentException(
                String.format("Currency mismatch: account currency is %s, but amount currency is %s",
                    this.balance.getCurrency(), amount.getCurrency())
            );
        }
    }

    private void validateSufficientFunds(Money amount) {
        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException(
                String.format("Insufficient funds: balance is %s, but withdrawal amount is %s",
                    this.balance, amount)
            );
        }
    }

    private void validateInitialBalance(Money initialBalance) {
        if (initialBalance.isNegative()) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Client getClient() {
        return client;
    }

    public boolean isActive() {
        return active;
    }

    // Setters (for persistence layer only)
    public void setId(Long id) {
        this.id = id;
    }

    // Domain Events Management
    /**
     * Get all domain events
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear all domain events
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * Add a domain event
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    // Equality based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", accountType=" + accountType +
                ", active=" + active +
                '}';
    }
}

// Made with Bob
