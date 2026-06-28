package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email Value Object representing an email address.
 * Immutable and defined by its value.
 * 
 * DDD Pattern: Value Object
 * - Immutable: All fields are final
 * - No identity: Equality based on value
 * - Self-validating: Enforces email format rules
 * - Rich behavior: Contains validation and normalization logic
 */
public class Email {
    
    // RFC 5322 compliant email pattern (simplified)
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);
    
    private final String value;
    
    /**
     * Default constructor for JPA.
     */
    protected Email() {
        this.value = null;
    }
    
    /**
     * Private constructor to enforce factory methods.
     * 
     * @param value The email address
     */
    private Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String normalized = value.trim().toLowerCase();
        
        if (!isValidFormat(normalized)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        
        this.value = normalized;
    }
    
    /**
     * Factory method to create Email from string.
     * 
     * @param value The email address
     * @return A new Email instance
     */
    public static Email of(String value) {
        return new Email(value);
    }
    
    /**
     * Validate email format.
     * 
     * @param email The email to validate
     * @return true if valid format
     */
    private static boolean isValidFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Check length constraints
        if (email.length() > 254) { // RFC 5321
            return false;
        }
        
        // Check pattern
        if (!PATTERN.matcher(email).matches()) {
            return false;
        }
        
        // Check local part length (before @)
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }
        
        String localPart = parts[0];
        String domainPart = parts[1];
        
        // Local part must be 1-64 characters
        if (localPart.length() < 1 || localPart.length() > 64) {
            return false;
        }
        
        // Domain part must be valid
        if (domainPart.length() < 1 || domainPart.length() > 253) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the email value.
     * 
     * @return The email address
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Get the local part of the email (before @).
     * 
     * @return The local part
     */
    public String getLocalPart() {
        if (value == null) {
            return null;
        }
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(0, atIndex) : null;
    }
    
    /**
     * Get the domain part of the email (after @).
     * 
     * @return The domain part
     */
    public String getDomain() {
        if (value == null) {
            return null;
        }
        int atIndex = value.indexOf('@');
        return atIndex >= 0 && atIndex < value.length() - 1 ? value.substring(atIndex + 1) : null;
    }
    
    /**
     * Get a masked version of the email for display.
     * Shows first 2 characters of local part and full domain.
     * 
     * @return Masked email (e.g., "jo***@example.com")
     */
    public String getMasked() {
        if (value == null) {
            return "***@***.***";
        }
        
        String localPart = getLocalPart();
        String domain = getDomain();
        
        if (localPart == null || domain == null) {
            return "***@***.***";
        }
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + domain;
        }
        
        return localPart.substring(0, 2) + "***@" + domain;
    }
    
    /**
     * Check if this email belongs to a specific domain.
     * 
     * @param domain The domain to check
     * @return true if email is from the specified domain
     */
    public boolean isFromDomain(String domain) {
        if (domain == null || value == null) {
            return false;
        }
        String emailDomain = getDomain();
        return emailDomain != null && emailDomain.equalsIgnoreCase(domain.trim());
    }
    
    /**
     * Check if this is a valid email.
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return value != null && isValidFormat(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

// Made with Bob