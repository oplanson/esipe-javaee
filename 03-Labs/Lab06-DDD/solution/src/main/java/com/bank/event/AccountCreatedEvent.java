package com.bank.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;

/**
 * CDI Event fired when a new account is created.
 * Demonstrates CDI event-driven architecture.
 *
 * Lab 06 - DDD: Events (from Lab 04)
 *
 * Refactored to Java Record (JDK 17+):
 * - Immutable by design (perfect for events)
 * - Thread-safe
 * - Concise syntax
 * - Events should never be modified after creation
 */
public record AccountCreatedEvent(
    Account account,
    Long clientId,
    String createdBy,
    long timestamp
) {
    
    /**
     * Convenience constructor with default createdBy.
     *
     * @param account The created account
     */
    public AccountCreatedEvent(Account account) {
        this(account, "system");
    }
    
    /**
     * Constructor with custom createdBy.
     *
     * @param account The created account
     * @param createdBy Who created the account
     */
    public AccountCreatedEvent(Account account, String createdBy) {
        this(
            account,
            account != null && account.getClient() != null ? account.getClient().getId() : null,
            createdBy,
            System.currentTimeMillis()
        );
    }
}

// Made with Bob
