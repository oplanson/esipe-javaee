package com.bank.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.logging.Logger;

/**
 * CDI Producer for EntityManager and Logger.
 * 
 * Producers allow creating beans that cannot be annotated directly:
 * - EntityManager from JPA
 * - Logger from java.util.logging
 * 
 * This demonstrates:
 * - @Produces annotation for producer methods
 * - @PersistenceContext for EntityManager injection
 * - InjectionPoint for context-aware production
 */
@ApplicationScoped
public class EntityManagerProducer {
    
    /**
     * EntityManager injected from persistence context.
     * Container-managed EntityManager with JTA transactions.
     */
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    /**
     * Produce EntityManager for injection.
     * 
     * @RequestScoped ensures each request gets the same EntityManager instance,
     * which is important for transaction consistency.
     * 
     * @return The EntityManager instance
     */
    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return em;
    }
    
    /**
     * Produce Logger for injection.
     * 
     * Uses InjectionPoint to create a logger with the name of the injecting class.
     * This allows each class to have its own logger automatically.
     * 
     * Example:
     * <pre>
     * {@code
     * @Inject
     * private Logger logger;  // Will be named "com.bank.service.ClientService"
     * }
     * </pre>
     * 
     * @param injectionPoint The injection point metadata
     * @return A Logger instance for the injecting class
     */
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(
            injectionPoint.getMember()
                         .getDeclaringClass()
                         .getName()
        );
    }
}

// Made with Bob