// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * JAX-RS Application Configuration
 * Defines the base path for REST endpoints
 */
@ApplicationPath("/api")
@OpenAPIDefinition(
    info = @Info(
        title = "Client Microservice API",
        version = "1.0.0",
        description = "REST API for managing banking clients",
        contact = @Contact(
            name = "Banking Team",
            email = "support@bank.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:9081/api", description = "Development Server"),
        @Server(url = "http://client-service:9081/api", description = "Docker Server")
    }
)
public class RestApplication extends Application {
    // No additional configuration needed
    // JAX-RS will automatically discover and register all @Path annotated classes
}

// Made with Bob
