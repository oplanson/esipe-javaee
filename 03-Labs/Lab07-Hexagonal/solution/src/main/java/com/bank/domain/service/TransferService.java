package com.bank.domain.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.model.Account;
import com.bank.domain.valueobject.Money;

/**
 * TransferService Domain Service for money transfers between accounts.
 *
 * DDD Pattern: Domain Service
 * - Stateless service containing domain logic
 * - Operates on multiple aggregates (Account)
 * - Enforces business rules for transfers
 * - Coordinates operations that don't naturally fit in a single aggregate
 *
 * Hexagonal Architecture: Pure Domain Service
 * - NO framework dependencies (no @ApplicationScoped, @Inject, @Transactional)
 * - NO infrastructure concerns (no Logger injection)
 * - Pure business logic only
 * - Can be instantiated and tested without any framework
 *
 * Why a Domain Service?
 * - Transfer involves two Account aggregates
 * - Business logic doesn't belong to either account alone
 * - Needs to coordinate atomic operations across aggregates
 *
 * Note: Transaction management is handled at the application layer (use case services)
 */
public class TransferService {
    
    /**
     * Transfer money between two accounts.
     * Enforces business rules and ensures atomicity.
     * 
     * Business Rules:
     * - Both accounts must exist
     * - Accounts must be different
     * - Amount must be positive
     * - Currencies must match
     * - Source account must have sufficient funds
     * - Transfer must respect account type limits
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if business rules are violated
     */
    public void transfer(Account fromAccount, Account toAccount, Money amount) {
        // Validate parameters
        validateTransferParameters(fromAccount, toAccount, amount);
        
        // Perform the transfer using aggregate methods
        // This delegates to the domain logic in Account aggregate
        fromAccount.transferTo(toAccount, amount);
    }
    
    /**
     * Transfer money with fee calculation.
     * Premium clients get free transfers, standard clients pay a fee.
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @param isPremiumClient Whether the client is premium
     * @return The total amount deducted (amount + fee)
     */
    public Money transferWithFee(Account fromAccount, Account toAccount, Money amount, boolean isPremiumClient) {
        validateTransferParameters(fromAccount, toAccount, amount);
        
        // Calculate fee
        Money fee = calculateTransferFee(amount, isPremiumClient);
        Money totalAmount = amount.add(fee);
        
        // Withdraw total amount from source
        fromAccount.withdraw(totalAmount);
        
        // Deposit only the transfer amount to destination (fee is kept by bank)
        toAccount.deposit(amount);
        
        return totalAmount;
    }
    
    /**
     * Calculate transfer fee based on amount and client status.
     * 
     * Business Rules:
     * - Premium clients: No fee
     * - Standard clients: 1% of amount, minimum 1 EUR, maximum 50 EUR
     * 
     * @param amount The transfer amount
     * @param isPremiumClient Whether the client is premium
     * @return The fee amount
     */
    public Money calculateTransferFee(Money amount, boolean isPremiumClient) {
        if (isPremiumClient) {
            return Money.zero(amount.getCurrency());
        }
        
        // Calculate 1% fee
        Money fee = amount.multiply(0.01);
        
        // Apply minimum and maximum
        Money minimumFee = Money.of(1.0, amount.getCurrency());
        Money maximumFee = Money.of(50.0, amount.getCurrency());
        
        if (fee.isLessThan(minimumFee)) {
            return minimumFee;
        }
        if (fee.isGreaterThan(maximumFee)) {
            return maximumFee;
        }
        
        return fee;
    }
    
    /**
     * Check if a transfer is possible without executing it.
     * Useful for validation before attempting transfer.
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @return true if transfer is possible
     */
    public boolean canTransfer(Account fromAccount, Account toAccount, Money amount) {
        try {
            validateTransferParameters(fromAccount, toAccount, amount);
            return fromAccount.canWithdraw(amount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }
    
    /**
     * Check if a transfer with fee is possible.
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @param isPremiumClient Whether the client is premium
     * @return true if transfer is possible
     */
    public boolean canTransferWithFee(Account fromAccount, Account toAccount, Money amount, boolean isPremiumClient) {
        try {
            validateTransferParameters(fromAccount, toAccount, amount);
            Money fee = calculateTransferFee(amount, isPremiumClient);
            Money totalAmount = amount.add(fee);
            return fromAccount.canWithdraw(totalAmount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }
    
    /**
     * Validate transfer parameters.
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The amount to transfer
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void validateTransferParameters(Account fromAccount, Account toAccount, Money amount) {
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account cannot be null");
        }
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Transfer amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Transfer amount must be positive: " + amount);
        }
        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Validate currency compatibility
        if (!amount.getCurrency().equals(fromAccount.getBalance().getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch: transfer amount is " + amount.getCurrency() +
                ", source account uses " + fromAccount.getBalance().getCurrency()
            );
        }
        if (!amount.getCurrency().equals(toAccount.getBalance().getCurrency())) {
            throw new IllegalArgumentException(
                "Currency mismatch: transfer amount is " + amount.getCurrency() +
                ", destination account uses " + toAccount.getBalance().getCurrency()
            );
        }
    }
}

// Made with Bob