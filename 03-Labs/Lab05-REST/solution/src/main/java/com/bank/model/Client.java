package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Client entity representing a bank customer.
 * JPA entity with database persistence.
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
    
    @NotNull(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Account> accounts;
    
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
        this.premium = false;
    }
    
    /**
     * Constructor with name, email, and premium status.
     *
     * @param name The client's name
     * @param email The client's email
     * @param premium Whether the client has premium status
     */
    public Client(String name, String email, boolean premium) {
        this();
        this.name = name;
        this.email = email;
        this.premium = premium;
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
    
    public boolean isPremium() {
        return premium;
    }
    
    public void setPremium(boolean premium) {
        this.premium = premium;
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
        if (this.accounts == null) {
            this.accounts = new ArrayList<>();
        }
        this.accounts.add(account);
        account.setClient(this);
    }
    
    /**
     * Remove an account from this client.
     * Maintains bidirectional relationship.
     * 
     * @param account The account to remove
     */
    public void removeAccount(Account account) {
        if (this.accounts != null) {
            this.accounts.remove(account);
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
        return getClass().hashCode();
    }
}

// Made with Bob