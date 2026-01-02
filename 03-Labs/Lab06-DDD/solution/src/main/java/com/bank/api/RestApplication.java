package com.bank.api;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application configuration for API V1 (DEPRECATED).
 * All REST resources will be available under /api path.
 *
 * BEST PRACTICE: Explicit resource registration
 * - Prevents accidental exposure of V2 resources in V1 API
 * - Clear separation of concerns
 * - Easier to maintain and understand
 * - Recommended by JAX-RS specification for versioned APIs
 *
 * Alternative approaches:
 * 1. Package-based scanning (requires separate packages)
 * 2. Annotation-based filtering (custom annotations)
 * 3. Configuration-based registration (external config)
 *
 * @author Banking Application Team
 * @version 1.0 (DEPRECATED)
 * @since Lab 06
 * @deprecated Use /api/v2 instead
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    
    /**
     * Explicitly register V1 resources only.
     * This prevents JAX-RS from auto-discovering V2 resources.
     *
     * BEST PRACTICE: Explicit is better than implicit for API versioning.
     * - Clear contract of what's exposed
     * - No surprises from auto-discovery
     * - Easy to audit and maintain
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // V1 Resources (DEPRECATED)
        classes.add(AccountResource.class);
        classes.add(ClientResource.class);
        
        // Note: V2 resources are NOT registered here
        // They are registered in RestApplicationV2
        
        return classes;
    }
}

// Made with Bob
