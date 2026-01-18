// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.security;

import com.bank.model.SecurityAuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for security audit logging
 */
@ApplicationScoped
public class SecurityAuditService {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityAuditService.class.getName());
    
    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;
    
    /**
     * Log a security event
     * 
     * @param username Username (can be null for failed login attempts)
     * @param action Action performed
     * @param resource Resource accessed
     * @param result Result (SUCCESS, FAILURE, DENIED)
     * @param ipAddress Client IP address
     * @param userAgent User agent string
     * @param details Additional details
     */
    @Transactional
    public void logSecurityEvent(String username, String action, String resource, 
                                 String result, String ipAddress, String userAgent, String details) {
        try {
            SecurityAuditLog log = new SecurityAuditLog();
            log.setUsername(username != null ? username : "anonymous");
            log.setAction(action);
            log.setResource(resource);
            log.setResult(result);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setDetails(details);
            
            em.persist(log);
            
            LOGGER.info(String.format("Security audit: %s - %s - %s - %s", 
                    username, action, resource, result));
                    
        } catch (Exception e) {
            LOGGER.severe("Failed to log security event: " + e.getMessage());
        }
    }
    
    /**
     * Log successful login
     */
    @Transactional
    public void logSuccessfulLogin(String username, String ipAddress, String userAgent) {
        logSecurityEvent(username, "LOGIN", "/api/auth/login", "SUCCESS", 
                        ipAddress, userAgent, "User logged in successfully");
    }
    
    /**
     * Log failed login
     */
    @Transactional
    public void logFailedLogin(String username, String ipAddress, String userAgent, String reason) {
        logSecurityEvent(username, "LOGIN", "/api/auth/login", "FAILURE", 
                        ipAddress, userAgent, "Login failed: " + reason);
    }
    
    /**
     * Log logout
     */
    @Transactional
    public void logLogout(String username, String ipAddress, String userAgent) {
        logSecurityEvent(username, "LOGOUT", "/api/auth/logout", "SUCCESS", 
                        ipAddress, userAgent, "User logged out");
    }
    
    /**
     * Log access denied
     */
    @Transactional
    public void logAccessDenied(String username, String resource, String ipAddress, 
                               String userAgent, String reason) {
        logSecurityEvent(username, "ACCESS_DENIED", resource, "DENIED", 
                        ipAddress, userAgent, "Access denied: " + reason);
    }
    
    /**
     * Log successful resource access
     */
    @Transactional
    public void logResourceAccess(String username, String resource, String ipAddress, String userAgent) {
        logSecurityEvent(username, "ACCESS", resource, "SUCCESS", 
                        ipAddress, userAgent, "Resource accessed successfully");
    }
    
    /**
     * Log account lockout
     */
    @Transactional
    public void logAccountLockout(String username, String ipAddress, String userAgent) {
        logSecurityEvent(username, "ACCOUNT_LOCKED", "/api/auth/login", "FAILURE", 
                        ipAddress, userAgent, "Account locked due to too many failed login attempts");
    }
    
    /**
     * Log password change
     */
    @Transactional
    public void logPasswordChange(String username, String ipAddress, String userAgent) {
        logSecurityEvent(username, "PASSWORD_CHANGE", "/api/auth/change-password", "SUCCESS", 
                        ipAddress, userAgent, "Password changed successfully");
    }
    
    /**
     * Log user registration
     */
    @Transactional
    public void logUserRegistration(String username, String ipAddress, String userAgent) {
        logSecurityEvent(username, "REGISTRATION", "/api/auth/register", "SUCCESS", 
                        ipAddress, userAgent, "New user registered");
    }
    
    /**
     * Get recent audit logs for a user
     * 
     * @param username Username
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    public List<SecurityAuditLog> getRecentLogsForUser(String username, int limit) {
        return em.createNamedQuery("SecurityAuditLog.findByUsername", SecurityAuditLog.class)
                .setParameter("username", username)
                .setMaxResults(limit)
                .getResultList();
    }
    
    /**
     * Get recent audit logs by action
     * 
     * @param action Action type
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    public List<SecurityAuditLog> getRecentLogsByAction(String action, int limit) {
        return em.createNamedQuery("SecurityAuditLog.findByAction", SecurityAuditLog.class)
                .setParameter("action", action)
                .setMaxResults(limit)
                .getResultList();
    }
    
    /**
     * Get recent audit logs
     * 
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    public List<SecurityAuditLog> getRecentLogs(int limit) {
        return em.createNamedQuery("SecurityAuditLog.findRecent", SecurityAuditLog.class)
                .setMaxResults(limit)
                .getResultList();
    }
    
    /**
     * Count failed login attempts for a user in the last hour
     * 
     * @param username Username
     * @return Number of failed login attempts
     */
    public long countRecentFailedLogins(String username) {
        return em.createQuery(
                "SELECT COUNT(l) FROM SecurityAuditLog l " +
                "WHERE l.username = :username " +
                "AND l.action = 'LOGIN' " +
                "AND l.result = 'FAILURE' " +
                "AND l.timestamp > CURRENT_TIMESTAMP - 1 HOUR", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}

// Made with Bob
