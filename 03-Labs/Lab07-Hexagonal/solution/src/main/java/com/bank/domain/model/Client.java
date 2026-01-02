/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.domain.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.event.DomainEvent;
import com.bank.domain.valueobject.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Client aggregate root - Pure domain entity with no infrastructure dependencies.
 * Contains business logic for client operations.
 */
public class Client {
    private Long id;
    private String name;
    private Email email;
    private boolean premium;
    private List<Account> accounts;
    private List<DomainEvent> domainEvents;

    // Default constructor for frameworks
    protected Client() {
        this.accounts = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    // Constructor for creating new clients
    public Client(String name, Email email) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.premium = false;
        this.accounts = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
        
        validateName(name);
    }

    // Constructor for reconstituting from persistence
    public Client(Long id, String name, Email email, boolean premium) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.premium = premium;
        this.accounts = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Factory method to create a new client
     */
    public static Client create(String name, Email email) {
        return new Client(name, email);
    }

    /**
     * Update client information
     */
    public void updateInfo(String name, Email email) {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        validateName(name);
        
        this.name = name;
        this.email = email;
    }

    /**
     * Upgrade client to premium status
     */
    public void upgradeToPremium() {
        if (this.premium) {
            throw new IllegalStateException("Client is already premium");
        }
        this.premium = true;
    }

    /**
     * Downgrade client from premium status
     */
    public void downgradeFromPremium() {
        if (!this.premium) {
            throw new IllegalStateException("Client is not premium");
        }
        this.premium = false;
    }

    /**
     * Alias for upgradeToPremium() - for backward compatibility
     */
    public void makePremium() {
        upgradeToPremium();
    }

    /**
     * Alias for downgradeFromPremium() - for backward compatibility
     */
    public void makeStandard() {
        downgradeFromPremium();
    }

    /**
     * Update client name
     */
    public void updateName(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        validateName(name);
        this.name = name;
    }

    /**
     * Add an account to this client
     */
    public void addAccount(Account account) {
        Objects.requireNonNull(account, "Account cannot be null");
        if (!this.accounts.contains(account)) {
            this.accounts.add(account);
        }
    }

    /**
     * Remove an account from this client
     */
    public void removeAccount(Account account) {
        this.accounts.remove(account);
    }

    // Validation methods
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public boolean isPremium() {
        return premium;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    // Setters (for persistence layer only)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts != null ? new ArrayList<>(accounts) : new ArrayList<>();
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
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", premium=" + premium +
                ", accountCount=" + accounts.size() +
                '}';
    }
}

// Made with Bob
