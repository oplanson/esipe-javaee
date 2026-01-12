package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.AccountType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: Part 1 - Stateless Session Bean
 * 
 * Instructions:
 * 1. Add @Stateless annotation to make this a stateless session bean
 * 2. Add @TransactionAttribute(TransactionAttributeType.REQUIRED) for CMT
 * 3. Add @DeclareRoles({"admin", "teller", "customer"}) for security
 * 4. Implement all TODO methods below
 */
// TODO: Add @Stateless annotation here

public class AccountServiceBean {

    // TODO: Inject EntityManager with @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;

    /**
     * TODO: Create a new account
     * - Create new Account instance
     * - Set account number and type
     * - Initialize balance to 0
     * - Persist the account
     * - Return the created account
     * 
     * Security: @RolesAllowed({"admin", "teller"})
     */
    public Account createAccount(String accountNumber, AccountType type) {
        // TODO: Implement account creation
        throw new UnsupportedOperationException("TODO: Implement createAccount");
    }

    /**
     * TODO: Find account by ID
     * - Use em.find() to retrieve account
     * - Throw exception if not found
     */
    public Account findById(Long id) {
        // TODO: Implement find by ID
        throw new UnsupportedOperationException("TODO: Implement findById");
    }

    /**
     * TODO: Find all accounts
     * - Create JPQL query: "SELECT a FROM Account a"
     * - Return list of accounts
     */
    public List<Account> findAll() {
        // TODO: Implement find all
        throw new UnsupportedOperationException("TODO: Implement findAll");
    }

    /**
     * TODO: Deposit money into account
     * - Find account by ID
     * - Call account.deposit(amount)
     * - Merge the account
     * 
     * Security: @RolesAllowed({"admin", "teller"})
     * Transaction: @TransactionAttribute(TransactionAttributeType.REQUIRED)
     */
    public void deposit(Long accountId, BigDecimal amount) {
        // TODO: Implement deposit
        throw new UnsupportedOperationException("TODO: Implement deposit");
    }

    /**
     * TODO: Withdraw money from account
     * - Find account by ID
     * - Call account.withdraw(amount)
     * - Merge the account
     * 
     * Security: @RolesAllowed({"admin", "teller"})
     * Transaction: @TransactionAttribute(TransactionAttributeType.REQUIRED)
     */
    public void withdraw(Long accountId, BigDecimal amount) {
        // TODO: Implement withdrawal
        throw new UnsupportedOperationException("TODO: Implement withdraw");
    }

    /**
     * TODO: Transfer money between accounts
     * - Withdraw from source account
     * - Deposit to destination account
     * - Both operations in same transaction
     * 
     * Security: @RolesAllowed({"admin", "teller"})
     * Transaction: @TransactionAttribute(TransactionAttributeType.REQUIRED)
     */
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // TODO: Implement transfer
        throw new UnsupportedOperationException("TODO: Implement transfer");
    }
}

// Made with Bob