/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.ArrayList;
import java.util.List;

/**
 * ClientDTO for transferring client data between layers.
 * 
 * Hexagonal Architecture: DTO in Application Layer
 * - Part of the application layer (use cases)
 * - Used by both primary and secondary adapters
 * - Separates domain model from external representations
 * - Provides a stable interface for adapters
 */
public class ClientDTO {
    
    private Long id;
    private String name;
    private String email;
    private boolean premium;
    private List<AccountDTO> accounts = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    public ClientDTO() {
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
    
    public List<AccountDTO> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }
    
    /**
     * Get premium status as string for display.
     * 
     * @return "Premium" or "Standard"
     */
    public String getPremiumStatus() {
        return premium ? "Premium" : "Standard";
    }
    
    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", premium=" + premium +
                ", accountsCount=" + (accounts != null ? accounts.size() : 0) +
                '}';
    }
}

// Made with Bob
