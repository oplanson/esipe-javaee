// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.security;

import com.bank.model.Role;
import com.bank.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Custom IdentityStore implementation backed by database
 * Implements account lockout after failed login attempts
 */
@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseIdentityStore.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;
    
    @Inject
    private PasswordService passwordService;
    
    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        
        UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;
        String username = usernamePassword.getCaller();
        String password = usernamePassword.getPasswordAsString();
        
        try {
            User user = findUserByUsername(username);
            
            if (user == null) {
                LOGGER.warning("User not found: " + username);
                return CredentialValidationResult.INVALID_RESULT;
            }
            
            // Check if account is locked
            if (user.isAccountLocked()) {
                LOGGER.warning("Account locked: " + username);
                return CredentialValidationResult.INVALID_RESULT;
            }
            
            // Check if account is enabled
            if (!user.isEnabled()) {
                LOGGER.warning("Account disabled: " + username);
                return CredentialValidationResult.INVALID_RESULT;
            }
            
            // Verify password
            if (passwordService.verifyPassword(password, user.getPassword())) {
                // Password correct
                // Note: Failed attempts reset is handled by UserService after successful login
                
                // Convert roles to string set
                Set<String> roles = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());
                
                LOGGER.info("User authenticated successfully: " + username);
                return new CredentialValidationResult(username, roles);
                
            } else {
                // Password incorrect
                // Note: Failed attempts increment is handled by UserService after failed login
                
                LOGGER.warning("Invalid password for user: " + username);
                return CredentialValidationResult.INVALID_RESULT;
            }
            
        } catch (Exception e) {
            LOGGER.severe("Error validating credentials: " + e.getMessage());
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
    
    /**
     * Find user by username
     */
    private User findUserByUsername(String username) {
        try {
            return em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Reset failed login attempts after successful login
     */
    @Transactional
    private void resetFailedAttempts(User user) {
        try {
            user.setFailedLoginAttempts(0);
            user.setLastLogin(LocalDateTime.now());
            em.merge(user);
            em.flush();
            
            LOGGER.info("Reset failed attempts for user: " + user.getUsername());
        } catch (Exception e) {
            LOGGER.severe("Error resetting failed attempts: " + e.getMessage());
        }
    }
    
    /**
     * Increment failed login attempts and lock account if threshold reached
     */
    @Transactional
    private void incrementFailedAttempts(User user) {
        try {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            
            // Lock account if max attempts reached
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                LOGGER.warning("Account locked due to too many failed attempts: " + user.getUsername());
            }
            
            em.merge(user);
            em.flush();
            
            LOGGER.info("Incremented failed attempts for user: " + user.getUsername() + 
                       " (attempts: " + attempts + ")");
        } catch (Exception e) {
            LOGGER.severe("Error incrementing failed attempts: " + e.getMessage());
        }
    }
    
    /**
     * Get user by username (for external use)
     */
    public User getUserByUsername(String username) {
        return findUserByUsername(username);
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        try {
            return em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        return findUserByUsername(username) != null;
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }
    
    /**
     * Create new user
     */
    @Transactional
    public User createUser(String username, String email, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordService.hashPassword(password));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        
        for (Role role : roles) {
            user.addRole(role);
        }
        
        em.persist(user);
        em.flush();
        
        LOGGER.info("Created new user: " + username);
        return user;
    }
    
    /**
     * Unlock user account (admin function)
     */
    @Transactional
    public void unlockAccount(String username) {
        User user = findUserByUsername(username);
        if (user != null) {
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            em.merge(user);
            em.flush();
            
            LOGGER.info("Unlocked account: " + username);
        }
    }
}

// Made with Bob
