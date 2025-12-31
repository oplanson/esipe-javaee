package com.bank.event;

import com.bank.model.Client;

/**
 * CDI Event fired when a new client is created.
 * Demonstrates CDI event-driven architecture.
 * 
 * Lab 04 - Advanced CDI: Events
 */
public class ClientCreatedEvent {
    
    private final Client client;
    private final String createdBy;
    private final long timestamp;
    
    public ClientCreatedEvent(Client client) {
        this(client, "system");
    }
    
    public ClientCreatedEvent(Client client, String createdBy) {
        this.client = client;
        this.createdBy = createdBy;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Client getClient() {
        return client;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "ClientCreatedEvent{" +
                "clientId=" + (client != null ? client.getId() : "null") +
                ", clientName='" + (client != null ? client.getName() : "null") + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

// Made with Bob