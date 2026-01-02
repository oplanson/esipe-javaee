/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.domain.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.time.LocalDateTime;

/**
 * Base interface for all domain events.
 * Domain events represent something that happened in the domain.
 */
public interface DomainEvent {
    
    /**
     * Get the timestamp when the event occurred
     */
    LocalDateTime occurredOn();
}

// Made with Bob
