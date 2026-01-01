package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account Aggregate Root representing a bank account.
 *
 * DDD Pattern: Aggregate Root
 * - Entity with identity (ID)
 * - Encapsulates business logic
 * - Uses Value Objects for attributes
 * - Enforces invariants
 * - Controls access to internal entities
 *
 * Refactored from anemic domain model to rich domain model:
 * - Replaced primitive types with Value Objects (Money, AccountNumber, AccountType)
 * - Added business logic methods
 * - Enforced business rules and invariants
 * - Made setters private to control state changes
 */
@Entity
@Table(name = "accounts")
@NamedQueries({
    @NamedQuery(
        name = "Account.findAll",
        query = "SELECT a FROM Account a ORDER BY a.accountNumber.value"
    ),
    @NamedQuery(
        name = "Account.findByClient",
        query = "SELECT a FROM Account a WHERE a.client.id = :clientId ORDER BY a.accountNumber.value"
    ),
    @NamedQuery(
        name = "Account.findByType",
        query = "SELECT a FROM Account a WHERE a.accountType = :type ORDER BY a.accountNumber.value"
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
    
    @Valid
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "account_number", nullable = false, unique = true, length = 34))
    private AccountNumber accountNumber;
    
    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", nullable = false, precision = 19, scale = 2)),
        @AttributeOverride(name = "currency", column = @Column(name = "balance_currency", nullable = false, length = 3))
    })
    private Money balance;
    
    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;
    
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
    protected Account() {
    }
    
    /**
     * Factory method to create a new Account.
     * Enforces business rules and generates account number.
     *
     * @param client The account owner
     * @param accountType The account type
     * @param initialDeposit The initial deposit amount
     * @return A new Account instance
     */
    public static Account create(Client client, AccountType accountType, Money initialDeposit) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (initialDeposit == null) {
            throw new IllegalArgumentException("Initial deposit cannot be null");
        }
        
        // Business rule: Minimum initial deposit
        Money minimumDeposit = Money.euros(10.0);
        if (initialDeposit.isLessThan(minimumDeposit)) {
            throw new IllegalArgumentException(
                "Initial deposit must be at least " + minimumDeposit + ", got: " + initialDeposit
            );
        }
        
        Account account = new Account();
        account.accountNumber = AccountNumber.generate();
        account.balance = initialDeposit;
        account.accountType = accountType;
        account.client = client;
        
        return account;
    }
    
    /**
     * Factory method to create Account with specific account number.
     * Used for testing or migration scenarios.
     *
     * @param client The account owner
     * @param accountNumber The account number
     * @param accountType The account type
     * @param initialBalance The initial balance
     * @return A new Account instance
     */
    public static Account createWithNumber(Client client, AccountNumber accountNumber,
                                          AccountType accountType, Money initialBalance) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        
        Account account = new Account();
        account.accountNumber = accountNumber;
        account.balance = initialBalance;
        account.accountType = accountType;
        account.client = client;
        
        return account;
    }
    
    /**
     * Deposit money into the account.
     * Business logic: Validates amount and updates balance.
     *
     * @param amount The amount to deposit
     * @throws IllegalArgumentException if amount is invalid
     */
    public void deposit(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Deposit amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Deposit amount must be positive: " + amount);
        }
        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch: account uses " + balance.getCurrency() +
                ", deposit is " + amount.getCurrency()
            );
        }
        
        this.balance = this.balance.add(amount);
    }
    
    /**
     * Withdraw money from the account.
     * Business logic: Validates amount, checks balance, respects overdraft limits.
     *
     * @param amount The amount to withdraw
     * @throws IllegalArgumentException if amount is invalid
     * @throws IllegalStateException if insufficient funds
     */
    public void withdraw(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Withdrawal amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Withdrawal amount must be positive: " + amount);
        }
        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch: account uses " + balance.getCurrency() +
                ", withdrawal is " + amount.getCurrency()
            );
        }
        
        // Check if withdrawal is allowed based on account type and balance
        if (!accountType.canWithdraw(balance, amount)) {
            Money minimumBalance = accountType.getMinimumBalance(balance.getCurrency());
            throw new IllegalStateException(
                "Insufficient funds. Current balance: " + balance +
                ", withdrawal: " + amount +
                ", minimum allowed: " + minimumBalance
            );
        }
        
        this.balance = this.balance.subtract(amount);
    }
    
    /**
     * Transfer money to another account.
     * Business logic: Validates accounts, checks balance, performs atomic transfer.
     *
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if insufficient funds
     */
    public void transferTo(Account toAccount, Money amount) {
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account cannot be null");
        }
        if (this.equals(toAccount)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Withdraw from this account (will validate and check balance)
        this.withdraw(amount);
        
        // Deposit to destination account
        toAccount.deposit(amount);
    }
    
    /**
     * Calculate interest for savings accounts.
     *
     * @param annualRate The annual interest rate (e.g., 0.02 for 2%)
     * @param days The number of days
     * @return The interest amount
     */
    public Money calculateInterest(double annualRate, int days) {
        return accountType.calculateInterest(balance, annualRate, days);
    }
    
    /**
     * Apply interest to the account balance.
     * Only applicable for savings accounts.
     *
     * @param annualRate The annual interest rate
     * @param days The number of days
     */
    public void applyInterest(double annualRate, int days) {
        Money interest = calculateInterest(annualRate, days);
        if (interest.isPositive()) {
            this.balance = this.balance.add(interest);
        }
    }
    
    /**
     * Check if the account is overdrawn.
     *
     * @return true if balance is negative
     */
    public boolean isOverdrawn() {
        return balance.getAmount().compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Check if the account can accept deposits.
     * Business rule: All accounts can accept deposits.
     *
     * @return true if deposits are allowed
     */
    public boolean canAcceptDeposits() {
        return true;
    }
    
    /**
     * Check if the account can make withdrawals.
     * Business rule: Depends on balance and account type.
     *
     * @param amount The amount to withdraw
     * @return true if withdrawal is allowed
     */
    public boolean canWithdraw(Money amount) {
        if (amount == null || !amount.isPositive()) {
            return false;
        }
        return accountType.canWithdraw(balance, amount);
    }
    
    /**
     * Validate business invariants.
     * Called before persist and update.
     */
    private void validateInvariants() {
        if (accountNumber == null) {
            throw new IllegalStateException("Account number cannot be null");
        }
        if (balance == null) {
            throw new IllegalStateException("Balance cannot be null");
        }
        if (accountType == null) {
            throw new IllegalStateException("Account type cannot be null");
        }
        if (client == null) {
            throw new IllegalStateException("Client cannot be null");
        }
        
        // Validate balance against account type limits
        Money minimumBalance = accountType.getMinimumBalance(balance.getCurrency());
        if (balance.isLessThan(minimumBalance)) {
            throw new IllegalStateException(
                "Balance " + balance + " is below minimum allowed " + minimumBalance +
                " for " + accountType + " account"
            );
        }
    }
    
    // Getters (no setters to enforce encapsulation)
    
    public Long getId() {
        return id;
    }
    
    public AccountNumber getAccountNumber() {
        return accountNumber;
    }
    
    public Money getBalance() {
        return balance;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Client getClient() {
        return client;
    }
    
    /**
     * Helper method to get client ID.
     * Useful for JSP views and DTOs.
     *
     * @return The client ID or null
     */
    public Long getClientId() {
        return client != null ? client.getId() : null;
    }
    
    /**
     * Get account number as string.
     * Convenience method for views.
     *
     * @return The account number string
     */
    public String getNumber() {
        return accountNumber != null ? accountNumber.getValue() : null;
    }
    
    /**
     * Get account type as string.
     * Convenience method for views.
     *
     * @return The account type string
     */
    public String getType() {
        return accountType != null ? accountType.name() : null;
    }
    
    /**
     * Get balance as double.
     * Convenience method for views (use with caution).
     *
     * @return The balance as double
     */
    public double getBalanceAsDouble() {
        return balance != null ? balance.getAmountAsDouble() : 0.0;
    }
    
    // Package-private setters for JPA and testing
    
    void setId(Long id) {
        this.id = id;
    }
    
    void setClient(Client client) {
        this.client = client;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", accountType=" + accountType +
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
