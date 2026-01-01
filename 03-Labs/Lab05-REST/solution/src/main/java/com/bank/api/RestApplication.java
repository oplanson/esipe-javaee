package com.bank.api;

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
