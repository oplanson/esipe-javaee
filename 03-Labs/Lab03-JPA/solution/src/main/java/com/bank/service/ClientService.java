package com.bank.service;

import com.bank.model.Client;
import com.bank.model.Account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for managing clients using JPA.
 * Implements database persistence with PostgreSQL.
 * Uses Singleton pattern (no CDI required).
 * 
 * This service manages its own EntityManager lifecycle and transactions.
 */
public class ClientService {
    
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private static ClientService instance;
    private EntityManagerFactory emf;
    
    /**
     * Private constructor for Singleton pattern.
     */
    private ClientService() {
        // Initialize EntityManagerFactory
        this.emf = Persistence.createEntityManagerFactory("bankingPU");
        LOGGER.info("ClientService initialized with EntityManagerFactory");
    }
    
    /**
     * Get the singleton instance of ClientService.
     * 
     * @return The ClientService instance
     */
    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }
    
    /**
     * Create a new EntityManager for a transaction.
     * Caller is responsible for closing it.
     * 
     * @return A new EntityManager
     */
    private EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Retrieve all clients.
     * 
     * @return List of all clients
     */
    public List<Client> findAll() {
        EntityManager em = createEntityManager();
        try {
            return em.createNamedQuery("Client.findAll", Client.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Find a client by ID.
     * 
     * @param id The client ID
     * @return The client if found, null otherwise
     */
    public Client findById(Long id) {
        EntityManager em = createEntityManager();
        try {
            return em.find(Client.class, id);
        } finally {
            em.close();
        }
    }
    
    /**
     * Find a client by ID with accounts eagerly loaded.
     * 
     * @param id The client ID
     * @return The client with accounts if found, null otherwise
     */
    public Client findByIdWithAccounts(Long id) {
        EntityManager em = createEntityManager();
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c LEFT JOIN FETCH c.accounts WHERE c.id = :id", 
                Client.class
            );
            query.setParameter("id", id);
            
            List<Client> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    
    /**
     * Create a new client.
     * 
     * @param client The client to create
     * @return The created client with ID set
     */
    public Client create(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            em.persist(client);
            em.flush(); // Force ID generation
            tx.commit();
            
            LOGGER.info("Created client: " + client.getId());
            return client;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Error creating client: " + e.getMessage());
            throw new RuntimeException("Failed to create client", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Update an existing client.
     * 
     * @param client The client to update
     * @return The updated client, or null if not found
     */
    public Client update(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client and client ID cannot be null");
        }
        
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            // Check if client exists
            Client existing = em.find(Client.class, client.getId());
            if (existing == null) {
                tx.rollback();
                return null;
            }
            
            // Update client
            Client updated = em.merge(client);
            tx.commit();
            
            LOGGER.info("Updated client: " + client.getId());
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Error updating client: " + e.getMessage());
            throw new RuntimeException("Failed to update client", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Delete a client by ID.
     * 
     * @param id The client ID to delete
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Client client = em.find(Client.class, id);
            if (client == null) {
                tx.rollback();
                return false;
            }
            
            em.remove(client);
            tx.commit();
            
            LOGGER.info("Deleted client: " + id);
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Error deleting client: " + e.getMessage());
            throw new RuntimeException("Failed to delete client", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Search clients by name (partial match, case-insensitive).
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    public List<Client> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        EntityManager em = createEntityManager();
        try {
            TypedQuery<Client> query = em.createNamedQuery("Client.findByName", Client.class);
            query.setParameter("name", "%" + name + "%");
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Search clients by email (partial match, case-insensitive).
     * 
     * @param email The email to search for
     * @return List of matching clients
     */
    public List<Client> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return List.of();
        }
        
        EntityManager em = createEntityManager();
        try {
            TypedQuery<Client> query = em.createNamedQuery("Client.findByEmail", Client.class);
            query.setParameter("email", "%" + email + "%");
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Add an account to a client.
     * 
     * @param clientId The client ID
     * @param account The account to add
     * @return true if added, false if client not found
     */
    public boolean addAccount(Long clientId, Account account) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Client client = em.find(Client.class, clientId);
            if (client == null) {
                tx.rollback();
                return false;
            }
            
            client.addAccount(account);
            em.persist(account);
            tx.commit();
            
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Error adding account: " + e.getMessage());
            throw new RuntimeException("Failed to add account", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Remove an account from a client.
     * 
     * @param clientId The client ID
     * @param accountId The account ID to remove
     * @return true if removed, false if client or account not found
     */
    public boolean removeAccount(Long clientId, Long accountId) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Client client = em.find(Client.class, clientId);
            if (client == null) {
                tx.rollback();
                return false;
            }
            
            Account account = em.find(Account.class, accountId);
            if (account == null) {
                tx.rollback();
                return false;
            }
            
            client.removeAccount(account);
            em.remove(account);
            tx.commit();
            
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.severe("Error removing account: " + e.getMessage());
            throw new RuntimeException("Failed to remove account", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Get the total number of clients.
     * 
     * @return The number of clients
     */
    public long count() {
        EntityManager em = createEntityManager();
        try {
            return em.createNamedQuery("Client.count", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Check if a client exists.
     * 
     * @param id The client ID
     * @return true if exists, false otherwise
     */
    public boolean exists(Long id) {
        EntityManager em = createEntityManager();
        try {
            return em.find(Client.class, id) != null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Shutdown the service and close EntityManagerFactory.
     * Should be called when application is shutting down.
     */
    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("ClientService shut down");
        }
    }
}

// Made with Bob