// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No additional configuration needed
    // All resources will be automatically discovered
}

// Made with Bob
