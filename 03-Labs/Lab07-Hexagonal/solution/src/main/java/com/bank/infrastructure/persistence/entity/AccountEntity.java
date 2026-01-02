/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.entity;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * JPA Entity for Account persistence.
 * 
 * Hexagonal Architecture: Infrastructure Layer
 * - Part of the secondary adapter (JPA adapter)
 * - Contains JPA annotations and persistence concerns
 * - Separated from domain model
 * - Mapped to/from domain Account via AccountMapper
 */
@Entity
@Table(name = "accounts")
public class AccountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "account_number", unique = true, nullable = false, length = 50)
    private String accountNumber;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @NotNull
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;
    
    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private ClientEntity client;
    
    /**
     * Default constructor for JPA.
     */
    public AccountEntity() {
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public ClientEntity getClient() {
        return client;
    }
    
    public void setClient(ClientEntity client) {
        this.client = client;
    }
}

// Made with Bob
