package com.bank.domain.valueobject;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money Value Object representing a monetary amount with currency.
 * 
 * DDD Pattern: Value Object
 * - Immutable
 * - Self-validating
 * - Encapsulates monetary calculations
 * - Provides domain-specific behavior
 */
@Embeddable
public class Money {
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    /**
     * Default constructor required by JPA.
     */
    protected Money() {
    }
    
    /**
     * Creates a new Money value object.
     * 
     * @param amount the monetary amount
     * @param currency the currency code (ISO 4217)
     * @throws IllegalArgumentException if amount or currency is invalid
     */
    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        
        try {
            Currency.getInstance(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currency);
        }
        
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }
    
    /**
     * Creates a Money object in Euros.
     * 
     * @param amount the amount in euros
     * @return a Money object
     */
    public static Money euros(BigDecimal amount) {
        return new Money(amount, "EUR");
    }
    
    /**
     * Creates a Money object in Euros from a double value.
     * 
     * @param amount the amount in euros
     * @return a Money object
     */
    public static Money euros(double amount) {
        return euros(BigDecimal.valueOf(amount));
    }
    
    /**
     * Creates a zero Money object in the specified currency.
     *
     * @param currency the currency code
     * @return a Money object with zero amount
     */
    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    /**
     * Factory method for creating Money from BigDecimal (for backward compatibility).
     *
     * @param amount the amount
     * @param currency the currency code
     * @return a Money object
     */
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
    
    /**
     * Factory method for creating Money from double (for backward compatibility).
     *
     * @param amount the amount
     * @param currency the currency code
     * @return a Money object
     */
    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }
    
    /**
     * Gets the amount.
     * 
     * @return the monetary amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Gets the amount as a double value (for backward compatibility).
     *
     * @return the amount as double
     */
    public double getAmountAsDouble() {
        return amount.doubleValue();
    }
    
    /**
     * Adds another Money object to this one.
     * 
     * @param other the Money to add
     * @return a new Money object with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * Subtracts another Money object from this one.
     * 
     * @param other the Money to subtract
     * @return a new Money object with the difference
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }
    
    /**
     * Multiplies this Money by a factor.
     * 
     * @param factor the multiplication factor
     * @return a new Money object with the product
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }
    
    /**
     * Multiplies this Money by a factor.
     * 
     * @param factor the multiplication factor
     * @return a new Money object with the product
     */
    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }
    
    /**
     * Checks if this Money is greater than another.
     * 
     * @param other the Money to compare with
     * @return true if this is greater than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return this.amount.compareTo(other.amount) > 0;
    }
    
    /**
     * Checks if this Money is greater than or equal to another.
     * 
     * @param other the Money to compare with
     * @return true if this is greater than or equal to other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThanOrEqualTo(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    /**
     * Checks if this Money is less than another.
     * 
     * @param other the Money to compare with
     * @return true if this is less than other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isLessThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return this.amount.compareTo(other.amount) < 0;
    }
    
    /**
     * Checks if this Money is less than or equal to another.
     * 
     * @param other the Money to compare with
     * @return true if this is less than or equal to other
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isLessThanOrEqualTo(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies: " + 
                this.currency + " and " + other.currency);
        }
        return this.amount.compareTo(other.amount) <= 0;
    }
    
    /**
     * Checks if this Money is zero.
     * 
     * @return true if amount is zero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Checks if this Money is positive.
     * 
     * @return true if amount is greater than zero
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Checks if this Money is negative.
     * 
     * @return true if amount is less than zero
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Returns the absolute value of this Money.
     * 
     * @return a new Money object with absolute amount
     */
    public Money abs() {
        return new Money(this.amount.abs(), this.currency);
    }
    
    /**
     * Returns the negated value of this Money.
     * 
     * @return a new Money object with negated amount
     */
    public Money negate() {
        return new Money(this.amount.negate(), this.currency);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && 
               Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

// Made with Bob
