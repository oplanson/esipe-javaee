package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Email;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client Aggregate Root representing a bank customer.
 *
 * DDD Pattern: Aggregate Root
 * - Entity with identity (ID)
 * - Encapsulates business logic
 * - Uses Value Objects for attributes
 * - Controls access to Account entities (aggregate members)
 * - Enforces invariants
 *
 * Refactored from anemic domain model to rich domain model:
 * - Replaced String email with Email Value Object
 * - Added business logic methods
 * - Enforced business rules and invariants
 * - Made setters private to control state changes
 * - Controls account collection access
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
        query = "SELECT c FROM Client c WHERE LOWER(c.email.value) LIKE LOWER(:email) ORDER BY c.name"
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
    
    @Valid
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true, length = 100))
    private Email email;
    
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
        
        // Enforce invariants
        validateInvariants();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Enforce invariants
        validateInvariants();
    }
    
    /**
     * Default constructor required by JPA.
     */
    protected Client() {
    }
    
    /**
     * Factory method to create a new Client.
     * Enforces business rules.
     *
     * @param name The client's name
     * @param email The client's email
     * @return A new Client instance
     */
    public static Client create(String name, Email email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        
        Client client = new Client();
        client.name = name.trim();
        client.email = email;
        client.premium = false;
        
        return client;
    }
    
    /**
     * Factory method to create a premium Client.
     *
     * @param name The client's name
     * @param email The client's email
     * @return A new premium Client instance
     */
    public static Client createPremium(String name, Email email) {
        Client client = create(name, email);
        client.premium = true;
        return client;
    }
    
    /**
     * Upgrade client to premium status.
     * Business rule: Can only upgrade, not downgrade.
     */
    public void upgradeToPremium() {
        if (!this.premium) {
            this.premium = true;
        }
    }
    
    /**
     * Downgrade client from premium status.
     * Business rule: Must not have any overdrawn accounts.
     */
    public void downgradeFromPremium() {
        if (this.premium) {
            // Check if any accounts are overdrawn
            boolean hasOverdrawnAccounts = accounts.stream()
                .anyMatch(Account::isOverdrawn);
            
            if (hasOverdrawnAccounts) {
                throw new IllegalStateException(
                    "Cannot downgrade client with overdrawn accounts"
                );
            }
            
            this.premium = false;
        }
    }
    
    /**
     * Update client name.
     * Business rule: Name cannot be empty.
     *
     * @param newName The new name
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = newName.trim();
    }
    
    /**
     * Update client email.
     * Business rule: Email must be valid.
     *
     * @param newEmail The new email
     */
    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }
    
    /**
     * Add an account to this client.
     * Maintains bidirectional relationship and enforces business rules.
     *
     * @param account The account to add
     */
    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        // Business rule: Maximum number of accounts
        if (accounts.size() >= getMaxAccountsAllowed()) {
            throw new IllegalStateException(
                "Client has reached maximum number of accounts: " + getMaxAccountsAllowed()
            );
        }
        
        if (!accounts.contains(account)) {
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
    
    /**
     * Get maximum number of accounts allowed.
     * Business rule: Premium clients can have more accounts.
     *
     * @return Maximum number of accounts
     */
    public int getMaxAccountsAllowed() {
        return premium ? 10 : 5;
    }
    
    /**
     * Get number of accounts.
     *
     * @return Number of accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }
    
    /**
     * Check if client can open a new account.
     *
     * @return true if can open new account
     */
    public boolean canOpenNewAccount() {
        return accounts.size() < getMaxAccountsAllowed();
    }
    
    /**
     * Get all accounts (unmodifiable).
     * Protects aggregate boundary.
     *
     * @return Unmodifiable list of accounts
     */
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }
    
    /**
     * Check if client has any accounts.
     *
     * @return true if has accounts
     */
    public boolean hasAccounts() {
        return !accounts.isEmpty();
    }
    
    /**
     * Validate business invariants.
     * Called before persist and update.
     */
    private void validateInvariants() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Name cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalStateException("Email cannot be null");
        }
        if (accounts.size() > getMaxAccountsAllowed()) {
            throw new IllegalStateException(
                "Client has too many accounts: " + accounts.size() +
                ", maximum allowed: " + getMaxAccountsAllowed()
            );
        }
    }
    
    // Getters (no setters to enforce encapsulation)
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Email getEmail() {
        return email;
    }
    
    /**
     * Get email as string.
     * Convenience method for views.
     *
     * @return The email string
     */
    public String getEmailAsString() {
        return email != null ? email.getValue() : null;
    }
    
    public boolean isPremium() {
        return premium;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Package-private setters for JPA and testing
    
    void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
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
