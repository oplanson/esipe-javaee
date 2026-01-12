// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client entity for Lab 02B - JSF Client Management
 */
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String email;
    private Address address;
    private List<Account> accounts;
    
    // Constructors
    public Client() {
        this.address = new Address();
        this.accounts = new ArrayList<>();
    }
    
    public Client(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = new Address();
        this.accounts = new ArrayList<>();
    }
    
    public Client(Long id, String name, String email, Address address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.accounts = new ArrayList<>();
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
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    public void addAccount(Account account) {
        this.accounts.add(account);
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                ", accounts=" + accounts.size() +
                '}';
    }
}

// Made with Bob
