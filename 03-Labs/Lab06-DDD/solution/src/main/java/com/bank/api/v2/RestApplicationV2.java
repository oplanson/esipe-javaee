package com.bank.api.v2;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application configuration for API v2.
 * All REST resources will be available under /api/v2 path.
 *
 * This demonstrates API versioning strategy using URL versioning.
 *
 * BEST PRACTICE: Explicit resource registration
 * - Prevents accidental exposure of V1 resources in V2 API
 * - Clear separation between API versions
 * - Easier to maintain and understand
 * - Recommended by JAX-RS specification for versioned APIs
 *
 * Version 2 Changes:
 * - Money Value Object in responses (amount + currency)
 * - Improved error handling
 * - Better DTOs separation
 * - No deprecation headers (current version)
 *
 * @author Banking Application Team
 * @version 2.0
 * @since Lab 06
 */
@ApplicationPath("/api/v2")
public class RestApplicationV2 extends Application {
    
    /**
     * Explicitly register V2 resources only.
     * This prevents JAX-RS from auto-discovering V1 resources.
     *
     * BEST PRACTICE: Explicit is better than implicit for API versioning.
     * - Clear contract of what's exposed
     * - No surprises from auto-discovery
     * - Easy to audit and maintain
     * - Prevents version mixing
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // V2 Resources (CURRENT)
        classes.add(AccountResourceV2.class);
        
        // Note: V1 resources are NOT registered here
        // They are registered in RestApplication
        
        // Future: Add more V2 resources here as they are created
        // classes.add(ClientResourceV2.class);
        // classes.add(TransactionResourceV2.class);
        
        return classes;
    }
}

// Made with Bob