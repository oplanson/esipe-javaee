package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;

/**
 * ClientDTO for transferring client data between layers.
 * 
 * DDD Pattern: Data Transfer Object (DTO)
 * - Separates domain model from presentation/API layer
 * - Provides a stable interface for external consumers
 * - Prevents exposing domain model internals
 * - Simplifies serialization (JSON, XML)
 */
public class ClientDTO {
    
    private Long id;
    private String name;
    private String email;
    private boolean premium;
    private int accountCount;
    
    /**
     * Default constructor.
     */
    public ClientDTO() {
    }
    
    /**
     * Create DTO from Client entity.
     * 
     * @param client The client entity
     * @return ClientDTO
     */
    public static ClientDTO fromEntity(Client client) {
        if (client == null) {
            return null;
        }
        
        ClientDTO dto = new ClientDTO();
        dto.id = client.getId();
        dto.name = client.getName();
        dto.email = client.getEmail() != null ? 
            client.getEmail().getValue() : null;
        dto.premium = client.isPremium();
        dto.accountCount = client.getAccountCount();
        
        return dto;
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
    
    public int getAccountCount() {
        return accountCount;
    }
    
    public void setAccountCount(int accountCount) {
        this.accountCount = accountCount;
    }
    
    /**
     * Get masked email for display.
     * 
     * @return Masked email
     */
    public String getMaskedEmail() {
        if (email == null || email.isEmpty()) {
            return "***@***.***";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***@***.***";
        }
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domain;
        }
        
        return localPart.substring(0, 2) + "***" + domain;
    }
    
    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", premium=" + premium +
                ", accountCount=" + accountCount +
                '}';
    }
}

// Made with Bob