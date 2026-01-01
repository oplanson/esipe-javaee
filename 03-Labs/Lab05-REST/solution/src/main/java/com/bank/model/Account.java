package com.bank.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Account entity representing a bank account.
 * JPA entity with database persistence.
 */
@Entity
@Table(name = "accounts")
@NamedQueries({
    @NamedQuery(
        name = "Account.findAll",
        query = "SELECT a FROM Account a ORDER BY a.number"
    ),
    @NamedQuery(
        name = "Account.findByClient",
        query = "SELECT a FROM Account a WHERE a.client.id = :clientId ORDER BY a.number"
    ),
    @NamedQuery(
        name = "Account.findByType",
        query = "SELECT a FROM Account a WHERE a.type = :type ORDER BY a.number"
    ),
    @NamedQuery(
        name = "Account.count",
        query = "SELECT COUNT(a) FROM Account a"
    )
})
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Account number is required")
    @Size(min = 5, max = 34, message = "Account number must be between 5 and 34 characters")
    @Column(nullable = false, unique = true, length = 34)
    private String number;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    @Column(nullable = false)
    private double balance;
    
    @NotNull(message = "Account type is required")
    @Pattern(regexp = "CHECKING|SAVINGS", message = "Type must be CHECKING or SAVINGS")
    @Column(nullable = false, length = 20)
    private String type; // CHECKING or SAVINGS
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
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
    public Account() {
    }
    
    /**
     * Constructor with all parameters except ID.
     * 
     * @param number The account number
     * @param balance The account balance
     * @param type The account type (CHECKING or SAVINGS)
     */
    public Account(String number, double balance, String type) {
        this.number = number;
        this.balance = balance;
        this.type = type;
    }
    
    /**
     * Full constructor with all parameters.
     * 
     * @param id The account ID
     * @param number The account number
     * @param balance The account balance
     * @param type The account type
     */
    public Account(Long id, String number, double balance, String type) {
        this(number, balance, type);
        this.id = id;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String number) {
        this.number = number;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    /**
     * Helper method to get client ID.
     * Useful for JSP views.
     * 
     * @return The client ID or null
     */
    public Long getClientId() {
        return client != null ? client.getId() : null;
    }
    
    /**
     * Deposit money into the account.
     * 
     * @param amount The amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
    
    /**
     * Withdraw money from the account.
     * 
     * @param amount The amount to withdraw
     * @return true if successful, false if insufficient funds
     */
    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", balance=" + balance +
                ", type='" + type + '\'' +
                ", clientId=" + getClientId() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

// Made with Bob