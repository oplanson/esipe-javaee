/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.out;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.event.DomainEvent;

/**
 * Secondary port (driven) - Event publisher interface.
 * Defined by the application layer, implemented by infrastructure layer.
 * Allows the application to publish domain events without knowing the implementation details.
 */
public interface EventPublisher {
    
    /**
     * Publish a domain event
     */
    void publish(DomainEvent event);
}

// Made with Bob
