// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.service;

import com.bank.model.Role;
import com.bank.model.User;
import com.bank.security.DatabaseIdentityStore;
import com.bank.security.PasswordService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Service for user management operations
 */
@ApplicationScoped
public class UserService {
    
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    
    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;
    
    @Inject
    private DatabaseIdentityStore identityStore;
    
    @Inject
    private PasswordService passwordService;
    
    /**
     * Register a new user
     * 
     * @param username Username
     * @param email Email
     * @param password Plain text password
     * @param roles User roles
     * @return Created user
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public User registerUser(String username, String email, String password, Set<Role> roles) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        // Check if username already exists
        if (identityStore.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (identityStore.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Validate password strength
        if (!passwordService.isPasswordStrong(password)) {
            throw new IllegalArgumentException(passwordService.getPasswordRequirements());
        }
        
        // Create user
        User user = identityStore.createUser(username, email, password, roles);
        
        LOGGER.info("User registered successfully: " + username);
        return user;
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return identityStore.getUserByUsername(username);
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return identityStore.getUserByEmail(email);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return em.createNamedQuery("User.findAll", User.class)
                .getResultList();
    }
    
    /**
     * Update user
     */
    @Transactional
    public User updateUser(User user) {
        return em.merge(user);
    }
    
    /**
     * Delete user
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            em.remove(user);
            LOGGER.info("User deleted: " + user.getUsername());
        }
    }
    
    /**
     * Change user password
     */
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = identityStore.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Verify old password
        if (!passwordService.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        
        // Validate new password strength
        if (!passwordService.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(passwordService.getPasswordRequirements());
        }
        
        // Update password
        user.setPassword(passwordService.hashPassword(newPassword));
        em.merge(user);
        
        LOGGER.info("Password changed for user: " + username);
    }
    
    /**
     * Enable user account
     */
    @Transactional
    public void enableUser(String username) {
        User user = identityStore.getUserByUsername(username);
        if (user != null) {
            user.setEnabled(true);
            em.merge(user);
            LOGGER.info("User enabled: " + username);
        }
    }
    
    /**
     * Disable user account
     */
    @Transactional
    public void disableUser(String username) {
        User user = identityStore.getUserByUsername(username);
        if (user != null) {
            user.setEnabled(false);
            em.merge(user);
            LOGGER.info("User disabled: " + username);
        }
    }
    
    /**
     * Unlock user account
     */
    @Transactional
    public void unlockAccount(String username) {
        identityStore.unlockAccount(username);
    }
    
    /**
     * Add role to user
     */
    @Transactional
    public void addRole(String username, Role role) {
        User user = identityStore.getUserByUsername(username);
        if (user != null) {
            user.addRole(role);
            em.merge(user);
            LOGGER.info("Added role " + role + " to user: " + username);
        }
    }
    
    /**
     * Remove role from user
     */
    @Transactional
    public void removeRole(String username, Role role) {
        User user = identityStore.getUserByUsername(username);
        if (user != null) {
            user.removeRole(role);
            em.merge(user);
            LOGGER.info("Removed role " + role + " from user: " + username);
        }
    }
}

// Made with Bob
