package com.bank.api;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * All REST resources will be available under /api path.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No need to override methods
    // JAX-RS will auto-discover all @Path annotated classes
}

// Made with Bob
