/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.port.out.EventPublisher;
import com.bank.domain.event.DomainEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * CDI implementation of EventPublisher port.
 * 
 * Hexagonal Architecture: Secondary Adapter (Driven Adapter)
 * - Implements secondary port (EventPublisher)
 * - Uses CDI events for publishing
 * - Isolated from domain and application layers
 * - Can be replaced with other event mechanisms (Kafka, RabbitMQ, etc.)
 */
@ApplicationScoped
public class CDIEventPublisherAdapter implements EventPublisher {
    
    @Inject
    private Event<DomainEvent> eventBus;
    
    @Override
    public void publish(DomainEvent event) {
        if (event != null) {
            eventBus.fire(event);
        }
    }
}

// Made with Bob
