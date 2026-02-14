package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;
import java.util.UUID;

/**
 * AccountNumber Value Object representing a bank account number.
 * 
 * DDD Pattern: Value Object
 * - Immutable
 * - Self-validating
 * - Encapsulates account number validation logic
 * - Provides domain-specific behavior
 */
@Embeddable
public class AccountNumber {
    
    private static final String IBAN_PATTERN = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$";
    
    @NotNull(message = "Account number is required")
    @Pattern(regexp = IBAN_PATTERN, message = "Invalid IBAN format")
    private String value;
    
    /**
     * Default constructor required by JPA.
     */
    protected AccountNumber() {
    }
    
    /**
     * Creates a new AccountNumber value object.
     * 
     * @param value the account number (IBAN format)
     * @throws IllegalArgumentException if account number is invalid
     */
    public AccountNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        
        String normalized = value.trim().toUpperCase().replaceAll("\\s", "");
        
        if (!normalized.matches(IBAN_PATTERN)) {
            throw new IllegalArgumentException("Invalid IBAN format: " + value);
        }
        
        this.value = normalized;
    }
    
    /**
     * Generates a new random account number in IBAN format.
     * Format: FR76 followed by 23 random digits
     * 
     * @return a new AccountNumber
     */
    public static AccountNumber generate() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String digits = uuid.replaceAll("[^0-9]", "");
        
        if (digits.length() < 23) {
            digits = digits + "00000000000000000000000";
        }
        
        String iban = "FR76" + digits.substring(0, 23);
        return new AccountNumber(iban);
    }
    
    /**
     * Gets the account number value.
     * 
     * @return the account number
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Gets the country code (first 2 characters).
     * 
     * @return the country code
     */
    public String getCountryCode() {
        return value.substring(0, 2);
    }
    
    /**
     * Gets the check digits (characters 3-4).
     * 
     * @return the check digits
     */
    public String getCheckDigits() {
        return value.substring(2, 4);
    }
    
    /**
     * Returns a formatted version of the account number with spaces.
     * Example: FR76 1234 5678 9012 3456 7890 123
     * 
     * @return formatted account number
     */
    public String getFormatted() {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(' ');
            }
            formatted.append(value.charAt(i));
        }
        return formatted.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
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
