package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

/**
 * AccountType Value Object representing the type of bank account.
 * 
 * DDD Pattern: Value Object (Enum)
 * - Immutable: Enum values are constants
 * - No identity: Equality based on enum value
 * - Self-validating: Only valid values can exist
 * - Rich behavior: Contains business logic for each type
 */
public enum AccountType {
    
    /**
     * Checking account for daily transactions.
     * - No interest
     * - Unlimited withdrawals
     * - Overdraft allowed (with limits)
     */
    CHECKING("Checking Account", "Current account for daily transactions", false, true, -500.0),
    
    /**
     * Savings account for storing money.
     * - Earns interest
     * - Limited withdrawals
     * - No overdraft
     */
    SAVINGS("Savings Account", "Savings account with interest", true, false, 0.0);
    
    private final String displayName;
    private final String description;
    private final boolean earnsInterest;
    private final boolean allowsOverdraft;
    private final double overdraftLimit;
    
    /**
     * Constructor for AccountType enum.
     * 
     * @param displayName The display name
     * @param description The description
     * @param earnsInterest Whether this account type earns interest
     * @param allowsOverdraft Whether overdraft is allowed
     * @param overdraftLimit The overdraft limit (negative value)
     */
    AccountType(String displayName, String description, boolean earnsInterest, 
                boolean allowsOverdraft, double overdraftLimit) {
        this.displayName = displayName;
        this.description = description;
        this.earnsInterest = earnsInterest;
        this.allowsOverdraft = allowsOverdraft;
        this.overdraftLimit = overdraftLimit;
    }
    
    /**
     * Get the display name.
     * 
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the description.
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this account type earns interest.
     * 
     * @return true if earns interest
     */
    public boolean earnsInterest() {
        return earnsInterest;
    }
    
    /**
     * Check if overdraft is allowed for this account type.
     * 
     * @return true if overdraft is allowed
     */
    public boolean allowsOverdraft() {
        return allowsOverdraft;
    }
    
    /**
     * Get the overdraft limit.
     * 
     * @return The overdraft limit (negative value for allowed overdraft)
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }
    
    /**
     * Get the minimum balance allowed for this account type.
     * 
     * @return The minimum balance (same as overdraft limit)
     */
    public Money getMinimumBalance(String currency) {
        return Money.of(overdraftLimit, currency);
    }
    
    /**
     * Calculate interest for a given balance.
     * 
     * @param balance The current balance
     * @param annualRate The annual interest rate (e.g., 0.02 for 2%)
     * @param days The number of days
     * @return The interest amount
     */
    public Money calculateInterest(Money balance, double annualRate, int days) {
        if (!earnsInterest || balance.isZero() || annualRate <= 0 || days <= 0) {
            return Money.zero(balance.getCurrency());
        }
        
        // Simple interest calculation: (balance * rate * days) / 365
        // Use BigDecimal end-to-end to avoid floating-point money errors.
        java.math.BigDecimal dailyRate = java.math.BigDecimal.valueOf(annualRate)
                .divide(java.math.BigDecimal.valueOf(365), 10, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal interestAmount = balance.getAmount()
                .multiply(dailyRate)
                .multiply(java.math.BigDecimal.valueOf(days))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return Money.of(interestAmount, balance.getCurrency());
    }
    
    /**
     * Check if a withdrawal is allowed given the current balance.
     * 
     * @param currentBalance The current balance
     * @param withdrawalAmount The amount to withdraw
     * @return true if withdrawal is allowed
     */
    public boolean canWithdraw(Money currentBalance, Money withdrawalAmount) {
        if (withdrawalAmount.isZero() || !withdrawalAmount.isPositive()) {
            return false;
        }
        
        Money resultingBalance = currentBalance.subtract(withdrawalAmount);
        Money minimumAllowed = Money.of(overdraftLimit, currentBalance.getCurrency());
        
        return resultingBalance.isGreaterThanOrEqualTo(minimumAllowed);
    }
    
    /**
     * Parse AccountType from string.
     * 
     * @param value The string value
     * @return The AccountType
     * @throws IllegalArgumentException if value is invalid
     */
    public static AccountType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Account type cannot be null or empty");
        }
        
        String normalized = value.trim().toUpperCase();
        
        try {
            return AccountType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid account type: " + value + ". Valid values are: CHECKING, SAVINGS"
            );
        }
    }
    
    /**
     * Check if a string is a valid account type.
     * 
     * @param value The string to check
     * @return true if valid
     */
    public static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

// Made with Bob