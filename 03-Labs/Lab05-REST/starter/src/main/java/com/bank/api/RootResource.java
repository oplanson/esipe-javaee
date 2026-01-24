package com.bank.api;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.dto.ApiInfo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.logging.Logger;

/**
 * Root REST resource for API information.
 * 
 * This resource provides metadata about the API, including:
 * - API name, version, and description
 * - Available endpoints (API discovery)
 * - Useful links (OpenAPI, health checks, metrics)
 * 
 * This follows REST best practices:
 * - API Discovery: Clients can explore available endpoints
 * - HATEOAS: Hypermedia As The Engine Of Application State
 * - Self-documentation: The API describes itself
 * 
 * Base URL: /api
 * 
 * Example:
 * GET /api
 * 
 * Response: 200 OK
 * {
 *   "name": "Banking REST API",
 *   "version": "1.0.0",
 *   "description": "RESTful API for banking operations",
 *   "endpoints": {
 *     "clients": "http://localhost:9080/api/clients",
 *     "accounts": "http://localhost:9080/api/accounts"
 *   },
 *   "links": {
 *     "openapi": "http://localhost:9080/openapi",
 *     "health": "http://localhost:9080/health",
 *     "metrics": "http://localhost:9080/metrics"
 *   },
 *   "timestamp": "2026-01-24T17:00:00"
 * }
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {
    
    private static final Logger LOGGER = Logger.getLogger(RootResource.class.getName());
    
    /**
     * Get API information.
     * 
     * This endpoint provides metadata about the API and available resources.
     * It uses UriInfo to dynamically generate absolute URLs based on the
     * current request context.
     * 
     * @param uriInfo JAX-RS context for building URIs
     * @return Response containing ApiInfo with metadata and links
     */
    @GET
    public Response getApiInfo(@Context UriInfo uriInfo) {
        LOGGER.fine("API root endpoint accessed");
        
        // Get base URI (e.g., http://localhost:9080/api)
        String baseUri = uriInfo.getBaseUri().toString();
        
        // Remove trailing slash if present
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }
        
        // Get absolute path (e.g., http://localhost:9080)
        String absolutePath = uriInfo.getAbsolutePath().toString();
        String rootUri = absolutePath.substring(0, absolutePath.indexOf("/api"));
        
        // Build API information
        ApiInfo apiInfo = new ApiInfo(
            "Banking REST API",
            "1.0.0",
            "RESTful API for banking operations with Jakarta EE"
        );
        
        // Add available endpoints
        apiInfo.addEndpoint("clients", baseUri + "/clients")
               .addEndpoint("accounts", baseUri + "/accounts");
        
        // Add useful links
        apiInfo.addLink("openapi", rootUri + "/openapi")
               .addLink("openapi-ui", rootUri + "/openapi/ui")
               .addLink("health", rootUri + "/health")
               .addLink("health-ready", rootUri + "/health/ready")
               .addLink("health-live", rootUri + "/health/live")
               .addLink("metrics", rootUri + "/metrics");
        
        LOGGER.info("API info returned with " + apiInfo.getEndpoints().size() + 
                   " endpoints and " + apiInfo.getLinks().size() + " links");
        
        return Response.ok(apiInfo).build();
    }
}

// Made with Bob