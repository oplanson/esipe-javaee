// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Address;
import com.bank.model.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Client service for Lab 02B - JSF Client Management
 * Provides CRUD operations for clients with in-memory storage
 */
@ApplicationScoped
public class ClientService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Client> clients;
    private AtomicLong clientIdCounter;
    private AtomicLong accountIdCounter;
    
    @PostConstruct
    public void init() {
        clients = new ArrayList<>();
        clientIdCounter = new AtomicLong(1);
        accountIdCounter = new AtomicLong(1);
        
        // Initialize with sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Client 1: John Doe
        Address address1 = new Address("123 Main St", "Paris", "75001", "France");
        Client client1 = new Client(clientIdCounter.getAndIncrement(), "John Doe", "john.doe@example.com", address1);
        Account account1 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890123", "Checking", new BigDecimal("5000.00"), client1.getId());
        Account account2 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890124", "Savings", new BigDecimal("15000.00"), client1.getId());
        client1.addAccount(account1);
        client1.addAccount(account2);
        clients.add(client1);
        
        // Client 2: Jane Smith
        Address address2 = new Address("456 Oak Ave", "Lyon", "69001", "France");
        Client client2 = new Client(clientIdCounter.getAndIncrement(), "Jane Smith", "jane.smith@example.com", address2);
        Account account3 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890125", "Checking", new BigDecimal("3500.00"), client2.getId());
        client2.addAccount(account3);
        clients.add(client2);
        
        // Client 3: Bob Johnson
        Address address3 = new Address("789 Elm St", "Marseille", "13001", "France");
        Client client3 = new Client(clientIdCounter.getAndIncrement(), "Bob Johnson", "bob.johnson@example.com", address3);
        Account account4 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890126", "Checking", new BigDecimal("7500.00"), client3.getId());
        Account account5 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890127", "Savings", new BigDecimal("25000.00"), client3.getId());
        client3.addAccount(account4);
        client3.addAccount(account5);
        clients.add(client3);
        
        // Client 4: Alice Williams
        Address address4 = new Address("321 Pine Rd", "Toulouse", "31000", "France");
        Client client4 = new Client(clientIdCounter.getAndIncrement(), "Alice Williams", "alice.williams@example.com", address4);
        Account account6 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890128", "Checking", new BigDecimal("4200.00"), client4.getId());
        client4.addAccount(account6);
        clients.add(client4);
        
        // Client 5: Charlie Brown
        Address address5 = new Address("654 Maple Dr", "Nice", "06000", "France");
        Client client5 = new Client(clientIdCounter.getAndIncrement(), "Charlie Brown", "charlie.brown@example.com", address5);
        Account account7 = new Account(accountIdCounter.getAndIncrement(), "FR7612345678901234567890129", "Savings", new BigDecimal("18000.00"), client5.getId());
        client5.addAccount(account7);
        clients.add(client5);
    }
    
    /**
     * Get all clients
     */
    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }
    
    /**
     * Find client by ID
     */
    public Client findById(Long id) {
        return clients.stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Search clients by name or email
     */
    public List<Client> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllClients();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return clients.stream()
                .filter(c -> (c.getName() != null && c.getName().toLowerCase().contains(lowerSearchTerm)) ||
                           (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerSearchTerm)))
                .collect(Collectors.toList());
    }
    
    /**
     * Save or update client
     */
    public Client save(Client client) {
        if (client.getId() == null) {
            // New client
            client.setId(clientIdCounter.getAndIncrement());
            clients.add(client);
        } else {
            // Update existing client
            Client existing = findById(client.getId());
            if (existing != null) {
                existing.setName(client.getName());
                existing.setEmail(client.getEmail());
                existing.setAddress(client.getAddress());
            }
        }
        return client;
    }
    
    /**
     * Delete client by ID
     */
    public boolean delete(Long id) {
        return clients.removeIf(c -> c.getId().equals(id));
    }
    
    /**
     * Get client count
     */
    public int getClientCount() {
        return clients.size();
    }
}

// Made with Bob
