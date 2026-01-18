// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Security audit log entity for tracking security events
 */
@Entity
@Table(name = "security_audit_logs")
@NamedQueries({
    @NamedQuery(
        name = "SecurityAuditLog.findByUsername",
        query = "SELECT s FROM SecurityAuditLog s WHERE s.username = :username ORDER BY s.timestamp DESC"
    ),
    @NamedQuery(
        name = "SecurityAuditLog.findByAction",
        query = "SELECT s FROM SecurityAuditLog s WHERE s.action = :action ORDER BY s.timestamp DESC"
    ),
    @NamedQuery(
        name = "SecurityAuditLog.findRecent",
        query = "SELECT s FROM SecurityAuditLog s ORDER BY s.timestamp DESC"
    )
})
public class SecurityAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 50)
    private String action; // LOGIN, LOGOUT, ACCESS_DENIED, etc.
    
    @Column(length = 255)
    private String resource;
    
    @Column(nullable = false, length = 20)
    private String result; // SUCCESS, FAILURE
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    @Column(length = 500)
    private String details;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    // Constructors
    public SecurityAuditLog() {
    }
    
    public SecurityAuditLog(String username, String action, String result) {
        this.username = username;
        this.action = action;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "SecurityAuditLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}

// Made with Bob
