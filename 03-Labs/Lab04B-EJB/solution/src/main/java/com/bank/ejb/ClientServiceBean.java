package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stateless Session Bean for client operations.
 * 
 * Key Features:
 * - Stateless: No conversational state maintained
 * - Pooled by container for scalability
 * - Thread-safe and highly concurrent
 * - Container-Managed Transactions (CMT)
 * - Declarative security with role-based access
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@DeclareRoles({"admin", "teller", "customer"})
public class ClientServiceBean {
    
    private static final Logger LOGGER = Logger.getLogger(ClientServiceBean.class.getName());
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    /**
     * Create a new client.
     *
     * @param client The client to create
     * @return The persisted client with generated ID
     */
    @RolesAllowed({"admin", "teller"})
    public Client createClient(Client client) {
        LOGGER.info("Creating new client: " + client.getName());
        em.persist(client);
        em.flush();
        return client;
    }
    
    /**
     * Create a new client with name and email.
     * Convenience method for creating clients.
     *
     * @param name The client's name
     * @param email The client's email
     * @return The persisted client with generated ID
     */
    @RolesAllowed({"admin", "teller"})
    public Client createClient(String name, String email) {
        Client client = new Client(name, email);
        return createClient(client);
    }
    
    /**
     * Find a client by ID.
     *
     * @param id The client ID
     * @return The client, or null if not found
     */
    @PermitAll
    public Client findClient(Long id) {
        return em.find(Client.class, id);
    }
    
    /**
     * Get all clients.
     *
     * @return List of all clients
     */
    @PermitAll
    public List<Client> getAllClients() {
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }
    
    /**
     * Find clients by name (case-insensitive partial match).
     *
     * @param name The name to search for
     * @return List of matching clients
     */
    @PermitAll
    public List<Client> findClientsByName(String name) {
        return em.createNamedQuery("Client.findByName", Client.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
    
    /**
     * Find clients by email (case-insensitive partial match).
     *
     * @param email The email to search for
     * @return List of matching clients
     */
    @PermitAll
    public List<Client> findClientsByEmail(String email) {
        return em.createNamedQuery("Client.findByEmail", Client.class)
                .setParameter("email", "%" + email + "%")
                .getResultList();
    }
    
    /**
     * Update a client.
     *
     * @param client The client to update
     * @return The updated client
     */
    @RolesAllowed({"admin", "teller"})
    public Client updateClient(Client client) {
        LOGGER.info("Updating client: " + client.getId());
        return em.merge(client);
    }
    
    /**
     * Delete a client.
     *
     * @param id The client ID
     */
    @RolesAllowed({"admin"})
    public void deleteClient(Long id) {
        Client client = em.find(Client.class, id);
        if (client != null) {
            LOGGER.info("Deleting client: " + id);
            em.remove(client);
        } else {
            throw new EJBException("Client not found: " + id);
        }
    }
    
    /**
     * Get the total number of clients.
     *
     * @return The count of clients
     */
    @PermitAll
    public long getClientCount() {
        return em.createNamedQuery("Client.count", Long.class)
                .getSingleResult();
    }
    
    /**
     * Upgrade a client to premium status.
     *
     * @param id The client ID
     * @return The updated client
     */
    @RolesAllowed({"admin", "teller"})
    public Client upgradeToPremium(Long id) {
        Client client = em.find(Client.class, id);
        if (client == null) {
            throw new EJBException("Client not found: " + id);
        }
        
        LOGGER.info("Upgrading client to premium: " + id);
        client.setPremium(true);
        return em.merge(client);
    }
    
    /**
     * Downgrade a client from premium status.
     *
     * @param id The client ID
     * @return The updated client
     */
    @RolesAllowed({"admin", "teller"})
    public Client downgradeFromPremium(Long id) {
        Client client = em.find(Client.class, id);
        if (client == null) {
            throw new EJBException("Client not found: " + id);
        }
        
        LOGGER.info("Downgrading client from premium: " + id);
        client.setPremium(false);
        return em.merge(client);
    }
}

// Made with Bob