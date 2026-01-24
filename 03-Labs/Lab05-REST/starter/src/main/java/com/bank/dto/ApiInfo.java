package com.bank.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API Information DTO for the root endpoint.
 * Provides metadata about the API, available endpoints, and useful links.
 * 
 * This follows REST best practices for API discovery and HATEOAS principles.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class ApiInfo {
    
    private String name;
    private String version;
    private String description;
    private Map<String, String> endpoints;
    private Map<String, String> links;
    private LocalDateTime timestamp;
    
    public ApiInfo() {
        this.timestamp = LocalDateTime.now();
        this.endpoints = new LinkedHashMap<>();
        this.links = new LinkedHashMap<>();
    }
    
    public ApiInfo(String name, String version, String description) {
        this();
        this.name = name;
        this.version = version;
        this.description = description;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, String> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(Map<String, String> endpoints) {
        this.endpoints = endpoints;
    }
    
    public Map<String, String> getLinks() {
        return links;
    }
    
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Add an endpoint to the API info.
     * 
     * @param name The endpoint name (e.g., "clients")
     * @param url The full URL to the endpoint
     * @return This ApiInfo instance for method chaining
     */
    public ApiInfo addEndpoint(String name, String url) {
        this.endpoints.put(name, url);
        return this;
    }
    
    /**
     * Add a link to the API info.
     * 
     * @param name The link name (e.g., "openapi")
     * @param url The full URL to the resource
     * @return This ApiInfo instance for method chaining
     */
    public ApiInfo addLink(String name, String url) {
        this.links.put(name, url);
        return this;
    }
}

// Made with Bob