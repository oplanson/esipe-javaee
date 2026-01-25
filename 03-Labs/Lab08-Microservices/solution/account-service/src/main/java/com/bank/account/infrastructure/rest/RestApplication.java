// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * JAX-RS Application configuration for Account Service REST API
 * Note: The /api path is configured in server.xml as contextRoot
 */
@ApplicationPath("/")
@OpenAPIDefinition(
    info = @Info(
        title = "Account Service API",
        version = "1.0.0",
        description = "REST API for managing bank accounts in a microservices architecture",
        contact = @Contact(
            name = "Banking Application Support",
            email = "support@banking-app.com"
        ),
        license = @License(
            name = "© Copyright 2025-2026 Olivier Planson. All rights reserved.",
            url = "https://banking-app.com/license"
        )
    ),
    servers = {
        @Server(url = "http://localhost:9082/api", description = "Development server"),
        @Server(url = "http://account-service:9082/api", description = "Docker container")
    }
)
public class RestApplication extends Application {
    // JAX-RS will automatically discover and register all @Path annotated classes
}

// Made with Bob
