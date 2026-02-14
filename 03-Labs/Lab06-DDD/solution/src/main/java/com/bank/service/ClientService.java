package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.model.Account;
import com.bank.event.ClientCreatedEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for managing clients using CDI and JPA.
 * Uses CDI for dependency injection and declarative transaction management.
 * 
 * Key CDI features demonstrated:
 * - @ApplicationScoped: Singleton bean managed by CDI
 * - @Inject: Dependency injection
 * - @Transactional: Declarative transaction management
 */
@ApplicationScoped
public class ClientService {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EntityManager em;
    
    /**
     * CDI Event for firing client creation events.
     * Demonstrates CDI event-driven architecture.
     */
    @Inject
    private Event<ClientCreatedEvent> clientCreatedEvent;
    
    /**
     * Retrieve all clients.
     * Read-only operation, no transaction required.
     * 
     * @return List of all clients
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Client> findAll() {
        logger.info("Finding all clients");
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }
    
    /**
     * Find a client by ID.
     * Read-only operation, no transaction required.
     * 
     * @param id The client ID
     * @return The client if found, null otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Client findById(Long id) {
        logger.info("Finding client by ID: " + id);
        return em.find(Client.class, id);
    }
    
    /**
     * Find a client by ID with accounts eagerly loaded.
     * Uses JOIN FETCH to avoid N+1 query problem.
     * 
     * @param id The client ID
     * @return The client with accounts if found, null otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Client findByIdWithAccounts(Long id) {
        logger.info("Finding client with accounts by ID: " + id);
        
        TypedQuery<Client> query = em.createQuery(
            "SELECT c FROM Client c LEFT JOIN FETCH c.accounts WHERE c.id = :id", 
            Client.class
        );
        query.setParameter("id", id);
        
        List<Client> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Create a new client.
     * Transaction automatically managed by @Transactional.
     * Rollback on any RuntimeException.
     * 
     * @param client The client to create
     * @return The created client with ID set
     */
    @Transactional
    public Client create(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        
        logger.info("Creating client: " + client.getName());
        em.persist(client);
        em.flush(); // Force ID generation
        
        logger.info("Client created with ID: " + client.getId());
        
        // Fire CDI event for client creation
        clientCreatedEvent.fire(new ClientCreatedEvent(client));
        
        return client;
    }
    
    /**
     * Update an existing client.
     * Transaction automatically managed by @Transactional.
     * 
     * @param client The client to update
     * @return The updated client, or null if not found
     */
    @Transactional
    public Client update(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client and client ID cannot be null");
        }
        
        logger.info("Updating client: " + client.getId());
        
        // Check if client exists
        Client existing = em.find(Client.class, client.getId());
        if (existing == null) {
            logger.warning("Client not found: " + client.getId());
            return null;
        }
        
        // Update client
        Client updated = em.merge(client);
        logger.info("Client updated: " + client.getId());
        
        return updated;
    }
    
    /**
     * Delete a client by ID.
     * Transaction automatically managed by @Transactional.
     * Cascade delete will remove associated accounts.
     * 
     * @param id The client ID to delete
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        logger.info("Deleting client: " + id);
        
        Client client = em.find(Client.class, id);
        if (client == null) {
            logger.warning("Client not found: " + id);
            return false;
        }
        
        em.remove(client);
        logger.info("Client deleted: " + id);
        
        return true;
    }
    
    /**
     * Search clients by name (partial match, case-insensitive).
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Client> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        logger.info("Searching clients by name: " + name);
        
        TypedQuery<Client> query = em.createNamedQuery("Client.findByName", Client.class);
        query.setParameter("name", "%" + name + "%");
        
        return query.getResultList();
    }
    
    /**
     * Search clients by email (partial match, case-insensitive).
     * 
     * @param email The email to search for
     * @return List of matching clients
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Client> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return List.of();
        }
        
        logger.info("Searching clients by email: " + email);
        
        TypedQuery<Client> query = em.createNamedQuery("Client.findByEmail", Client.class);
        query.setParameter("email", "%" + email + "%");
        
        return query.getResultList();
    }
    
    /**
     * Add an account to a client.
     * Transaction automatically managed by @Transactional.
     * 
     * @param clientId The client ID
     * @param account The account to add
     * @return true if added, false if client not found
     */
    @Transactional
    public boolean addAccount(Long clientId, Account account) {
        logger.info("Adding account to client: " + clientId);
        
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            logger.warning("Client not found: " + clientId);
            return false;
        }
        
        client.addAccount(account);
        em.persist(account);
        
        logger.info("Account added to client: " + clientId);
        return true;
    }
    
    /**
     * Remove an account from a client.
     * Transaction automatically managed by @Transactional.
     * 
     * @param clientId The client ID
     * @param accountId The account ID to remove
     * @return true if removed, false if client or account not found
     */
    @Transactional
    public boolean removeAccount(Long clientId, Long accountId) {
        logger.info("Removing account " + accountId + " from client: " + clientId);
        
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            logger.warning("Client not found: " + clientId);
            return false;
        }
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            logger.warning("Account not found: " + accountId);
            return false;
        }
        
        // Remove from client's collection first to maintain in-memory consistency
        // Use the method that doesn't update the account's client reference
        // This avoids setting client_id to null before DELETE (which would cause constraint violation)
        client.removeAccountFromCollection(account);
        
        // Now remove the account - JPA will handle the database DELETE
        em.remove(account);
        
        logger.info("Account removed from client: " + clientId);
        return true;
    }
    
    /**
     * Get the total number of clients.
     * 
     * @return The number of clients
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public long count() {
        logger.info("Counting clients");
        return em.createNamedQuery("Client.count", Long.class)
                .getSingleResult();
    }
    
    /**
     * Check if a client exists.
     * 
     * @param id The client ID
     * @return true if exists, false otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean exists(Long id) {
        return em.find(Client.class, id) != null;
    }
}

// Made with Bob
