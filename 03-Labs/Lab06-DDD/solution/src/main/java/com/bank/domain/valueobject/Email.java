package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Email Value Object representing an email address.
 * 
 * DDD Pattern: Value Object
 * - Immutable
 * - Self-validating
 * - Encapsulates email validation logic
 * - Provides domain-specific behavior
 */
@Embeddable
public class Email {
    
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    @NotNull(message = "Email is required")
    @Pattern(regexp = EMAIL_PATTERN, message = "Invalid email format")
    private String value;
    
    /**
     * Default constructor required by JPA.
     */
    protected Email() {
    }
    
    /**
     * Creates a new Email value object.
     * 
     * @param value the email address
     * @throws IllegalArgumentException if email is invalid
     */
    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String normalized = value.trim().toLowerCase();
        
        if (!normalized.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        
        this.value = normalized;
    }
    
    /**
     * Factory method for creating Email (for backward compatibility).
     *
     * @param value the email address
     * @return an Email object
     */
    public static Email of(String value) {
        return new Email(value);
    }
    
    /**
     * Gets the email value.
     *
     * @return the email address
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Gets the local part of the email (before @).
     * 
     * @return the local part
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
    
    /**
     * Gets the domain part of the email (after @).
     * 
     * @return the domain
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    /**
     * Returns a masked version of the email for display purposes.
     * Example: j***@example.com
     * 
     * @return masked email
     */
    public String getMasked() {
        String local = getLocalPart();
        String domain = getDomain();
        
        if (local.length() <= 1) {
            return local + "***@" + domain;
        }
        
        return local.charAt(0) + "***@" + domain;
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
