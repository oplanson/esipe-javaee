package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money Value Object representing monetary amounts.
 * Immutable and defined by its attributes (amount and currency).
 * 
 * DDD Pattern: Value Object
 * - Immutable: All fields are final
 * - No identity: Equality based on attributes
 * - Self-validating: Enforces business rules
 * - Rich behavior: Contains business logic
 */
@Embeddable
public class Money {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount cannot be negative")
    private final BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private final String currency;
    
    /**
     * Default constructor for JPA.
     * Creates a Money object with zero amount in EUR.
     */
    protected Money() {
        this.amount = BigDecimal.ZERO;
        this.currency = "EUR";
    }
    
    /**
     * Private constructor to enforce factory methods.
     * 
     * @param amount The monetary amount
     * @param currency The currency code (ISO 4217)
     */
    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }
    
    /**
     * Factory method to create Money from BigDecimal.
     * 
     * @param amount The amount
     * @param currency The currency code
     * @return A new Money instance
     */
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
    
    /**
     * Factory method to create Money from double.
     * 
     * @param amount The amount
     * @param currency The currency code
     * @return A new Money instance
     */
    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    
    /**
     * Factory method to create Money in EUR.
     * 
     * @param amount The amount
     * @return A new Money instance in EUR
     */
    public static Money euros(BigDecimal amount) {
        return new Money(amount, "EUR");
    }
    
    /**
     * Factory method to create Money in EUR from double.
     * 
     * @param amount The amount
     * @return A new Money instance in EUR
     */
    public static Money euros(double amount) {
        return new Money(BigDecimal.valueOf(amount), "EUR");
    }
    
    /**
     * Factory method to create zero Money.
     * 
     * @param currency The currency code
     * @return A new Money instance with zero amount
     */
    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    /**
     * Add money to this amount.
     * Returns a new Money instance (immutability).
     * 
     * @param other The money to add
     * @return A new Money instance with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot add money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * Subtract money from this amount.
     * Returns a new Money instance (immutability).
     * 
     * @param other The money to subtract
     * @return A new Money instance with the difference
     * @throws IllegalArgumentException if currencies don't match or result is negative
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot subtract money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative: " + result);
        }
        return new Money(result, this.currency);
    }
    
    /**
     * Multiply money by a factor.
     * Returns a new Money instance (immutability).
     * 
     * @param factor The multiplication factor
     * @return A new Money instance with the product
     */
    public Money multiply(BigDecimal factor) {
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factor cannot be negative: " + factor);
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }
    
    /**
     * Multiply money by a factor.
     * 
     * @param factor The multiplication factor
     * @return A new Money instance with the product
     */
    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }
    
    /**
     * Check if this money is greater than another.
     * 
     * @param other The money to compare
     * @return true if this is greater than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) > 0;
    }
    
    /**
     * Check if this money is greater than or equal to another.
     * 
     * @param other The money to compare
     * @return true if this is greater than or equal to other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThanOrEqualTo(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    /**
     * Check if this money is less than another.
     * 
     * @param other The money to compare
     * @return true if this is less than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isLessThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot compare money with different currencies: " + this.currency + " and " + other.currency
            );
        }
        return this.amount.compareTo(other.amount) < 0;
    }
    
    /**
     * Check if this money is zero.
     * 
     * @return true if amount is zero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Check if this money is positive (greater than zero).
     * 
     * @return true if amount is positive
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Get the amount as BigDecimal.
     * 
     * @return The amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Get the amount as double.
     * Use with caution due to precision issues.
     * 
     * @return The amount as double
     */
    public double getAmountAsDouble() {
        return amount.doubleValue();
    }
    
    /**
     * Get the currency code.
     * 
     * @return The currency code
     */
    public String getCurrency() {
        return currency;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return amount.toString() + " " + currency;
    }
}

// Made with Bob