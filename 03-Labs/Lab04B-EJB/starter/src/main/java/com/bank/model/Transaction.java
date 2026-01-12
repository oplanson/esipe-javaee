package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity representing a banking transaction.
 * Records all account operations (deposits, withdrawals, transfers).
 */
@Entity
@Table(name = "transactions")
@NamedQueries({
    @NamedQuery(
        name = "Transaction.findAll",
        query = "SELECT t FROM Transaction t ORDER BY t.timestamp DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByAccount",
        query = "SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.timestamp DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByType",
        query = "SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.timestamp DESC"
    ),
    @NamedQuery(
        name = "Transaction.findByDateRange",
        query = "SELECT t FROM Transaction t WHERE t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC"
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "reference_number", unique = true, length = 50)
    private String referenceNumber;
    
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "processed_by", length = 100)
    private String processedBy; // User or system that processed the transaction
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (referenceNumber == null) {
            referenceNumber = generateReferenceNumber();
        }
    }
    
    /**
     * Default constructor required by JPA.
     */
    public Transaction() {
    }
    
    /**
     * Constructor with essential parameters.
     * 
     * @param account The account
     * @param type The transaction type
     * @param amount The transaction amount
     * @param balanceAfter The balance after transaction
     */
    public Transaction(Account account, TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }
    
    /**
     * Constructor with description.
     * 
     * @param account The account
     * @param type The transaction type
     * @param amount The transaction amount
     * @param balanceAfter The balance after transaction
     * @param description The transaction description
     */
    public Transaction(Account account, TransactionType type, BigDecimal amount, 
                      BigDecimal balanceAfter, String description) {
        this(account, type, amount, balanceAfter);
        this.description = description;
    }
    
    /**
     * Generate a unique reference number for the transaction.
     * 
     * @return The reference number
     */
    private String generateReferenceNumber() {
        return "TXN-" + System.currentTimeMillis() + "-" + 
               (int)(Math.random() * 10000);
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", timestamp=" + timestamp +
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

/**
 * Transaction type enumeration.
 */
enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT,
    FEE,
    INTEREST
}

// Made with Bob