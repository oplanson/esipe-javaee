package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.Objects;
import java.util.Random;

/**
 * AccountNumber Value Object representing a bank account number.
 * Immutable and defined by its value.
 * 
 * DDD Pattern: Value Object
 * - Immutable: All fields are final
 * - No identity: Equality based on value
 * - Self-validating: Enforces format rules
 * - Rich behavior: Contains generation and validation logic
 * 
 * Format: IBAN-like format (simplified)
 * Example: FR7612345678901234567890123
 */
public class AccountNumber {
    
    private static final String COUNTRY_CODE = "FR";
    private static final int ACCOUNT_LENGTH = 23; // FR + 2 check digits + 23 digits
    private static final String ACCOUNT_PATTERN = "^FR\\d{25}$";
    
    private final String value;
    
    /**
     * Default constructor for JPA.
     */
    protected AccountNumber() {
        this.value = null;
    }
    
    /**
     * Private constructor to enforce factory methods.
     * 
     * @param value The account number value
     */
    private AccountNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        
        String normalized = value.trim().toUpperCase();
        
        if (!normalized.matches(ACCOUNT_PATTERN)) {
            throw new IllegalArgumentException(
                "Invalid account number format. Expected format: " + ACCOUNT_PATTERN + ", got: " + normalized
            );
        }
        
        this.value = normalized;
    }
    
    /**
     * Factory method to create AccountNumber from string.
     * 
     * @param value The account number value
     * @return A new AccountNumber instance
     */
    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }
    
    /**
     * Factory method to generate a new random account number.
     * Uses simplified IBAN format with French country code.
     * 
     * @return A new AccountNumber instance with generated value
     */
    public static AccountNumber generate() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(COUNTRY_CODE);
        
        // Generate check digits (simplified - not real IBAN check)
        int checkDigits = random.nextInt(100);
        sb.append(String.format("%02d", checkDigits));
        
        // Generate 23 random digits
        for (int i = 0; i < ACCOUNT_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        
        return new AccountNumber(sb.toString());
    }
    
    /**
     * Factory method to generate account number with specific bank code.
     * 
     * @param bankCode The 5-digit bank code
     * @return A new AccountNumber instance
     */
    public static AccountNumber generateWithBankCode(String bankCode) {
        if (bankCode == null || !bankCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("Bank code must be 5 digits");
        }
        
        Random random = new Random();
        StringBuilder sb = new StringBuilder(COUNTRY_CODE);
        
        // Generate check digits
        int checkDigits = random.nextInt(100);
        sb.append(String.format("%02d", checkDigits));
        
        // Add bank code
        sb.append(bankCode);
        
        // Generate remaining 18 random digits
        for (int i = 0; i < 18; i++) {
            sb.append(random.nextInt(10));
        }
        
        return new AccountNumber(sb.toString());
    }
    
    /**
     * Get the account number value.
     * 
     * @return The account number value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Get the country code from the account number.
     * 
     * @return The country code (first 2 characters)
     */
    public String getCountryCode() {
        return value != null ? value.substring(0, 2) : null;
    }
    
    /**
     * Get the check digits from the account number.
     * 
     * @return The check digits (characters 3-4)
     */
    public String getCheckDigits() {
        return value != null && value.length() >= 4 ? value.substring(2, 4) : null;
    }
    
    /**
     * Get the bank code from the account number.
     * 
     * @return The bank code (characters 5-9)
     */
    public String getBankCode() {
        return value != null && value.length() >= 9 ? value.substring(4, 9) : null;
    }
    
    /**
     * Get a masked version of the account number for display.
     * Shows only the last 4 digits.
     * 
     * @return Masked account number (e.g., "FR**...***1234")
     */
    public String getMasked() {
        if (value == null || value.length() < 4) {
            return "****";
        }
        String lastFour = value.substring(value.length() - 4);
        return value.substring(0, 2) + "**...***" + lastFour;
    }
    
    /**
     * Check if this account number is valid.
     * Validates format and basic structure.
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return value != null && value.matches(ACCOUNT_PATTERN);
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