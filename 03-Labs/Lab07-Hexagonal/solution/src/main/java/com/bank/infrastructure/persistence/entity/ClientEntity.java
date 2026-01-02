/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.entity;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for Client persistence.
 * 
 * Hexagonal Architecture: Infrastructure Layer
 * - Part of the secondary adapter (JPA adapter)
 * - Contains JPA annotations and persistence concerns
 * - Separated from domain model
 * - Mapped to/from domain Client via ClientMapper
 */
@Entity
@Table(name = "clients")
public class ClientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @NotNull
    @Email
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "is_premium")
    private boolean premium = false;
    
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountEntity> accounts = new ArrayList<>();
    
    /**
     * Default constructor for JPA.
     */
    public ClientEntity() {
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    
    public List<AccountEntity> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }
}

// Made with Bob
