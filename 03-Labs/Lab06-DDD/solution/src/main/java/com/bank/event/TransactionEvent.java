package com.bank.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;

/**
 * CDI Event fired when a financial transaction occurs.
 * Demonstrates CDI event-driven architecture with qualifiers.
 *
 * Lab 06 - DDD: Events (from Lab 04)
 *
 * Refactored to Java Record (JDK 17+):
 * - Immutable by design (perfect for events)
 * - Thread-safe
 * - Concise syntax
 * - Events should never be modified after creation
 */
public record TransactionEvent(
    Account account,
    TransactionType type,
    double amount,
    Long targetAccountId,
    String performedBy,
    long timestamp
) {
    
    /**
     * Transaction type enumeration.
     */
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
    
    /**
     * Convenience constructor for simple transactions.
     *
     * @param account The account
     * @param type The transaction type
     * @param amount The amount
     */
    public TransactionEvent(Account account, TransactionType type, double amount) {
        this(account, type, amount, null, "system");
    }
    
    /**
     * Constructor for transfers.
     *
     * @param account The source account
     * @param type The transaction type
     * @param amount The amount
     * @param targetAccountId The target account ID (for transfers)
     */
    public TransactionEvent(Account account, TransactionType type, double amount, Long targetAccountId) {
        this(account, type, amount, targetAccountId, "system");
    }
    
    /**
     * Full constructor with all parameters.
     *
     * @param account The account
     * @param type The transaction type
     * @param amount The amount
     * @param targetAccountId The target account ID (for transfers)
     * @param performedBy Who performed the transaction
     */
    public TransactionEvent(Account account, TransactionType type, double amount, Long targetAccountId, String performedBy) {
        this(account, type, amount, targetAccountId, performedBy, System.currentTimeMillis());
    }
}

// Made with Bob
