package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Account entity representing a bank account.
 * Used with EJB for transactional operations.
 */
@Entity
@Table(name = "accounts")
@NamedQueries({
    @NamedQuery(
        name = "Account.findAll",
        query = "SELECT a FROM Account a ORDER BY a.accountNumber"
    ),
    @NamedQuery(
        name = "Account.findByNumber",
        query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber"
    ),
    @NamedQuery(
        name = "Account.findByType",
        query = "SELECT a FROM Account a WHERE a.type = :type ORDER BY a.accountNumber"
    ),
    @NamedQuery(
        name = "Account.count",
        query = "SELECT COUNT(a) FROM Account a"
    )
})
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true, length = 34)
    private String accountNumber;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountType type;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    @Version
    private Long version; // Optimistic locking for concurrent access
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Default constructor required by JPA.
     */
    public Account() {
    }
    
    /**
     * Constructor with essential parameters.
     * 
     * @param accountNumber The account number
     * @param type The account type
     */
    public Account(String accountNumber, AccountType type) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }
    
    /**
     * Constructor with balance.
     * 
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param type The account type
     */
    public Account(String accountNumber, BigDecimal balance, AccountType type) {
        this(accountNumber, type);
        this.balance = balance;
    }
    
    // Business methods
    
    /**
     * Deposit money into the account.
     * Thread-safe operation for EJB.
     * 
     * @param amount The amount to deposit
     * @throws IllegalArgumentException if amount is negative or zero
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deposit to inactive account");
        }
        this.balance = this.balance.add(amount);
    }
    
    /**
     * Withdraw money from the account.
     * Thread-safe operation for EJB.
     * 
     * @param amount The amount to withdraw
     * @throws IllegalArgumentException if amount is negative or zero
     * @throws IllegalStateException if insufficient funds or account inactive
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot withdraw from inactive account");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * Check if account has sufficient funds.
     * 
     * @param amount The amount to check
     * @return true if sufficient funds available
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
    
    /**
     * Close the account.
     */
    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
    }
    
    /**
     * Suspend the account.
     */
    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }
    
    /**
     * Activate the account.
     */
    public void activate() {
        this.status = AccountStatus.ACTIVE;
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
    
    public AccountType getType() {
        return type;
    }
    
    public void setType(AccountType type) {
        this.type = type;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public Long getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

/**
 * Account type enumeration.
 */
enum AccountType {
    CHECKING,
    SAVINGS,
    BUSINESS
}

/**
 * Account status enumeration.
 */
enum AccountStatus {
    ACTIVE,
    SUSPENDED,
    CLOSED
}

// Made with Bob