package com.bank.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;

/**
 * CDI Event fired when a new client is created.
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
public record ClientCreatedEvent(
    Client client,
    String createdBy,
    long timestamp
) {
    
    /**
     * Convenience constructor with default createdBy.
     *
     * @param client The created client
     */
    public ClientCreatedEvent(Client client) {
        this(client, "system");
    }
    
    /**
     * Constructor with custom createdBy.
     *
     * @param client The created client
     * @param createdBy Who created the client
     */
    public ClientCreatedEvent(Client client, String createdBy) {
        this(client, createdBy, System.currentTimeMillis());
    }
}

// Made with Bob
