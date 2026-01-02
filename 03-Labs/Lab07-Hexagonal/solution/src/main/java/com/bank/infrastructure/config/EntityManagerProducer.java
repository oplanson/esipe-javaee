/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.config;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * CDI Producer for EntityManager.
 * 
 * Hexagonal Architecture: Infrastructure Configuration
 * - Provides EntityManager for JPA adapters
 * - Part of infrastructure layer
 * - Handles technical concerns (persistence context)
 */
@ApplicationScoped
public class EntityManagerProducer {
    
    @Produces
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager entityManager;
}

// Made with Bob
