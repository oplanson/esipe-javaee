package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction entity representing a financial transaction.
 * Stores the history of all account operations (deposits, withdrawals, transfers).
 */
@Entity
@Table(name = "transactions")
@NamedQueries({
    @NamedQuery(
        name = "Transaction.findAll",
        query = "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByAccount",
        query = "SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByType",
        query = "SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.transactionDate DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByDateRange",
        query = "SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC"
    ),
    @NamedQuery(
        name = "Transaction.count",
        query = "SELECT COUNT(t) FROM Transaction t"
    )
})
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Column(nullable = false)
    private double amount;
    
    @Column(name = "balance_before", nullable = false)
    private double balanceBefore;
    
    @Column(name = "balance_after", nullable = false)
    private double balanceAfter;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @JsonbTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    // For transfers: reference to the target account
    @Column(name = "target_account_id")
    private Long targetAccountId;
    
    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
    
    /**
     * Transaction types
     */
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,
        TRANSFER_IN
    }
    
    /**
     * Default constructor required by JPA.
     */
    public Transaction() {
    }
    
    /**
     * Constructor for deposit or withdrawal.
     */
    public Transaction(TransactionType type, double amount, double balanceBefore, double balanceAfter, Account account) {
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.account = account;
        this.transactionDate = LocalDateTime.now();
    }
    
    /**
     * Constructor for transfer.
     */
    public Transaction(TransactionType type, double amount, double balanceBefore, double balanceAfter, 
                      Account account, Long targetAccountId, String description) {
        this(type, amount, balanceBefore, balanceAfter, account);
        this.targetAccountId = targetAccountId;
        this.description = description;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getBalanceBefore() {
        return balanceBefore;
    }
    
    public void setBalanceBefore(double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public Long getTargetAccountId() {
        return targetAccountId;
    }
    
    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }
    
    /**
     * Helper method to get account ID.
     */
    public Long getAccountId() {
        return account != null ? account.getId() : null;
    }
    
    /**
     * Helper method to get formatted transaction date for JSP display.
     * @return Formatted date string in dd/MM/yyyy HH:mm:ss format
     */
    public String getFormattedTransactionDate() {
        if (transactionDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return transactionDate.format(formatter);
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", accountId=" + getAccountId() +
                ", targetAccountId=" + targetAccountId +
                ", transactionDate=" + transactionDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

// Made with Bob