// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bank account entity
 */
@Entity
@Table(name = "accounts")
@NamedQueries({
    @NamedQuery(
        name = "Account.findAll",
        query = "SELECT a FROM Account a ORDER BY a.accountNumber"
    ),
    @NamedQuery(
        name = "Account.findByOwner",
        query = "SELECT a FROM Account a WHERE a.ownerUsername = :username ORDER BY a.accountNumber"
    ),
    @NamedQuery(
        name = "Account.findByAccountNumber",
        query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber"
    )
})
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;
    
    @NotBlank
    @Column(name = "owner_username", nullable = false, length = 50)
    private String ownerUsername;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "account_type", length = 20)
    private String accountType = "CHECKING";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (accountNumber == null) {
            accountNumber = generateAccountNumber();
        }
    }
    
    private String generateAccountNumber() {
        return "ACC-" + System.currentTimeMillis();
    }
    
    // Constructors
    public Account() {
    }
    
    public Account(String ownerUsername, BigDecimal balance) {
        this.ownerUsername = ownerUsername;
        this.balance = balance;
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
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}

// Made with Bob
