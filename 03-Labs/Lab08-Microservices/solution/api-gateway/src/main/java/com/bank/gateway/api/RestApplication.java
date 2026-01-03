// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration for API Gateway
 * Exposes REST API at /api/*
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No additional configuration needed
    // All @Path annotated classes will be automatically discovered
}

// Made with Bob