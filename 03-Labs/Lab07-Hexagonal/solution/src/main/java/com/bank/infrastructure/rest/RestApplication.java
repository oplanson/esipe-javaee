/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.rest;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * 
 * Hexagonal Architecture: Infrastructure Layer
 * - Configures REST API endpoints
 * - Part of primary adapter (REST adapter)
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No additional configuration needed
    // All REST resources will be automatically discovered
}

// Made with Bob
