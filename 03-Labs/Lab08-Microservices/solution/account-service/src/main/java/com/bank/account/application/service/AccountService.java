// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.application.service;

import com.bank.account.application.dto.AccountDTO;
import com.bank.account.application.dto.ClientDTO;
import com.bank.account.application.dto.TransactionDTO;
import com.bank.account.application.mapper.AccountMapper;
import com.bank.account.domain.model.Account;
import com.bank.account.domain.model.AccountStatus;
import com.bank.account.domain.model.AccountType;
import com.bank.account.domain.port.AccountRepository;
import com.bank.account.infrastructure.client.ClientServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Account Service - Application Layer
 * Implements use cases for account management
 * Uses MicroProfile Rest Client to communicate with Client Service
 */
@ApplicationScoped
@Transactional
public class AccountService {
    
    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
    
    @Inject
    private AccountRepository accountRepository;
    
    @Inject
    private AccountMapper accountMapper;
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    /**
     * Create a new account
     * Validates that the client exists by calling Client Service
     * 
     * @param accountDTO The account data
     * @return The created account
     * @throws IllegalArgumentException if client doesn't exist or account number already exists
     */
    public AccountDTO createAccount(AccountDTO accountDTO) {
        LOGGER.info("Creating new account for client: " + accountDTO.getClientId());
        
        // Verify client exists by calling Client Service
        try {
            ClientDTO client = clientServiceClient.getClientById(accountDTO.getClientId());
            if (client == null || client.getId() == null) {
                throw new IllegalArgumentException("Client not found with ID: " + accountDTO.getClientId());
            }
            LOGGER.info("Client verified: " + client.getFirstName() + " " + client.getLastName());
        } catch (Exception e) {
            LOGGER.warning("Failed to verify client: " + e.getMessage());
            throw new IllegalArgumentException("Unable to verify client with ID: " + accountDTO.getClientId());
        }
        
        // Check if account number already exists
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new IllegalArgumentException("Account with number " + accountDTO.getAccountNumber() + " already exists");
        }
        
        // Convert DTO to domain model
        Account account = accountMapper.toDomain(accountDTO);
        
        // Validate domain model
        if (!account.isValid()) {
            throw new IllegalArgumentException("Invalid account data");
        }
        
        // Save account
        Account savedAccount = accountRepository.save(account);
        
        LOGGER.info("Account created successfully with ID: " + savedAccount.getId());
        
        // Convert back to DTO
        return accountMapper.toDTO(savedAccount);
    }
    
    /**
     * Get an account by ID
     * @param id The account ID
     * @return Optional containing the account if found
     */
    public Optional<AccountDTO> getAccountById(Long id) {
        LOGGER.info("Fetching account with ID: " + id);
        
        return accountRepository.findById(id)
                .map(accountMapper::toDTO);
    }
    
    /**
     * Get an account by account number
     * @param accountNumber The account number
     * @return Optional containing the account if found
     */
    public Optional<AccountDTO> getAccountByAccountNumber(String accountNumber) {
        LOGGER.info("Fetching account with number: " + accountNumber);
        
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::toDTO);
    }
    
    /**
     * Get all accounts
     * @return List of all accounts
     */
    public List<AccountDTO> getAllAccounts() {
        LOGGER.info("Fetching all accounts");
        
        return accountRepository.findAll().stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all accounts for a specific client
     * @param clientId The client ID
     * @return List of accounts for the client
     */
    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        LOGGER.info("Fetching accounts for client: " + clientId);
        
        return accountRepository.findByClientId(clientId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get accounts by type
     * @param accountType The account type
     * @return List of accounts of the specified type
     */
    public List<AccountDTO> getAccountsByType(AccountType accountType) {
        LOGGER.info("Fetching accounts of type: " + accountType);
        
        return accountRepository.findByAccountType(accountType).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get accounts by status
     * @param status The account status
     * @return List of accounts with the specified status
     */
    public List<AccountDTO> getAccountsByStatus(AccountStatus status) {
        LOGGER.info("Fetching accounts with status: " + status);
        
        return accountRepository.findByStatus(status).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update an existing account
     * @param id The account ID
     * @param accountDTO The updated account data
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     */
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        LOGGER.info("Updating account with ID: " + id);
        
        // Find existing account
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        // Check if account number is being changed and if new number already exists
        if (!existingAccount.getAccountNumber().equals(accountDTO.getAccountNumber())) {
            if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
                throw new IllegalArgumentException("Account with number " + accountDTO.getAccountNumber() + " already exists");
            }
        }
        
        // Update account from DTO
        accountMapper.updateFromDTO(existingAccount, accountDTO);
        
        // Validate updated account
        if (!existingAccount.isValid()) {
            throw new IllegalArgumentException("Invalid account data");
        }
        
        // Save updated account
        Account updatedAccount = accountRepository.save(existingAccount);
        
        LOGGER.info("Account updated successfully: " + id);
        
        // Convert back to DTO
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Deposit money into an account
     * @param id The account ID
     * @param transaction The transaction details
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     * @throws IllegalStateException if account is not active
     */
    public AccountDTO deposit(Long id, TransactionDTO transaction) {
        LOGGER.info("Depositing " + transaction.getAmount() + " to account: " + id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.deposit(transaction.getAmount());
        Account updatedAccount = accountRepository.save(account);
        
        LOGGER.info("Deposit successful. New balance: " + updatedAccount.getBalance());
        
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Withdraw money from an account
     * @param id The account ID
     * @param transaction The transaction details
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     * @throws IllegalStateException if account is not active or insufficient funds
     */
    public AccountDTO withdraw(Long id, TransactionDTO transaction) {
        LOGGER.info("Withdrawing " + transaction.getAmount() + " from account: " + id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.withdraw(transaction.getAmount());
        Account updatedAccount = accountRepository.save(account);
        
        LOGGER.info("Withdrawal successful. New balance: " + updatedAccount.getBalance());
        
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Transfer money between accounts
     * @param sourceId The source account ID
     * @param transaction The transaction details (including target account ID)
     * @return The updated source account
     * @throws IllegalArgumentException if accounts not found
     * @throws IllegalStateException if accounts are not active or insufficient funds
     */
    public AccountDTO transfer(Long sourceId, TransactionDTO transaction) {
        LOGGER.info("Transferring " + transaction.getAmount() + " from account " + sourceId + " to account " + transaction.getTargetAccountId());
        
        Account sourceAccount = accountRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found with ID: " + sourceId));
        
        Account targetAccount = accountRepository.findById(transaction.getTargetAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Target account not found with ID: " + transaction.getTargetAccountId()));
        
        sourceAccount.transferTo(targetAccount, transaction.getAmount());
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        LOGGER.info("Transfer successful. Source balance: " + sourceAccount.getBalance() + ", Target balance: " + targetAccount.getBalance());
        
        return accountMapper.toDTO(sourceAccount);
    }
    
    /**
     * Suspend an account
     * @param id The account ID
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     */
    public AccountDTO suspendAccount(Long id) {
        LOGGER.info("Suspending account: " + id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.suspend();
        Account updatedAccount = accountRepository.save(account);
        
        LOGGER.info("Account suspended: " + id);
        
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Activate an account
     * @param id The account ID
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     */
    public AccountDTO activateAccount(Long id) {
        LOGGER.info("Activating account: " + id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.activate();
        Account updatedAccount = accountRepository.save(account);
        
        LOGGER.info("Account activated: " + id);
        
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Close an account
     * @param id The account ID
     * @return The updated account
     * @throws IllegalArgumentException if account not found
     * @throws IllegalStateException if account has non-zero balance
     */
    public AccountDTO closeAccount(Long id) {
        LOGGER.info("Closing account: " + id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.close();
        Account updatedAccount = accountRepository.save(account);
        
        LOGGER.info("Account closed: " + id);
        
        return accountMapper.toDTO(updatedAccount);
    }
    
    /**
     * Delete an account
     * @param id The account ID
     * @throws IllegalArgumentException if account not found
     */
    public void deleteAccount(Long id) {
        LOGGER.info("Deleting account with ID: " + id);
        
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        
        accountRepository.deleteById(id);
        
        LOGGER.info("Account deleted successfully: " + id);
    }
    
    /**
     * Check if an account exists
     * @param id The account ID
     * @return true if account exists, false otherwise
     */
    public boolean accountExists(Long id) {
        return accountRepository.existsById(id);
    }
    
    /**
     * Get total number of accounts
     * @return Total number of accounts
     */
    public long getTotalAccounts() {
        return accountRepository.count();
    }
    
    /**
     * Get number of accounts for a specific client
     * @param clientId The client ID
     * @return Number of accounts for the client
     */
    public long getAccountCountByClientId(Long clientId) {
        return accountRepository.countByClientId(clientId);
    }
}

// Made with Bob
