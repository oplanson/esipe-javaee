package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Client entity representing a bank customer.
 * Used with EJB for transactional operations.
 */
@Entity
@Table(name = "clients")
@NamedQueries({
    @NamedQuery(
        name = "Client.findAll",
        query = "SELECT c FROM Client c ORDER BY c.name"
    ),
    @NamedQuery(
        name = "Client.findByName",
        query = "SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(:name) ORDER BY c.name"
    ),
    @NamedQuery(
        name = "Client.findByEmail",
        query = "SELECT c FROM Client c WHERE LOWER(c.email) LIKE LOWER(:email) ORDER BY c.name"
    ),
    @NamedQuery(
        name = "Client.count",
        query = "SELECT COUNT(c) FROM Client c"
    )
})
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Default constructor required by JPA.
     */
    public Client() {
    }
    
    /**
     * Constructor with essential parameters.
     * 
     * @param name The client's name
     * @param email The client's email
     */
    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    /**
     * Constructor with all parameters.
     * 
     * @param name The client's name
     * @param email The client's email
     * @param phone The client's phone
     * @param address The client's address
     */
    public Client(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    /**
     * Add an account to this client.
     * Maintains bidirectional relationship.
     * 
     * @param account The account to add
     */
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
            account.setClient(this);
        }
    }
    
    /**
     * Remove an account from this client.
     * Maintains bidirectional relationship.
     * 
     * @param account The account to remove
     */
    public void removeAccount(Account account) {
        if (account != null && accounts.contains(account)) {
            accounts.remove(account);
            account.setClient(null);
        }
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", premium=" + premium +
                ", accounts=" + accounts.size() +
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
        return getClass().hashCode();
    }
}

// Made with Bob