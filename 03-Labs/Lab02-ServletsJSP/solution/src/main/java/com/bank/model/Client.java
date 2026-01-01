package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.ArrayList;
import java.util.List;

/**
 * Client entity representing a bank customer.
 * Complete implementation with all required methods.
 */
public class Client {
    
    private Long id;
    private String name;
    private String email;
    private List<Account> accounts;
    
    /**
     * Default constructor.
     * Initializes the accounts list to avoid NullPointerException.
     */
    public Client() {
        this.accounts = new ArrayList<>();
    }
    
    /**
     * Constructor with name and email parameters.
     * 
     * @param name The client's name
     * @param email The client's email
     */
    public Client(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }
    
    /**
     * Full constructor with all parameters.
     * 
     * @param id The client ID
     * @param name The client's name
     * @param email The client's email
     */
    public Client(Long id, String name, String email) {
        this(name, email);
        this.id = id;
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
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    /**
     * Add an account to this client.
     * 
     * @param account The account to add
     */
    public void addAccount(Account account) {
        if (this.accounts == null) {
            this.accounts = new ArrayList<>();
        }
        this.accounts.add(account);
        account.setClientId(this.id);
    }
    
    /**
     * Remove an account from this client.
     * 
     * @param account The account to remove
     */
    public void removeAccount(Account account) {
        if (this.accounts != null) {
            this.accounts.remove(account);
        }
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + (accounts != null ? accounts.size() : 0) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id != null && id.equals(client.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

// Made with Bob
