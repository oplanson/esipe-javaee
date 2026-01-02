/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.OpenAccountCommand;
import com.bank.application.dto.AccountDTO;
import com.bank.application.port.in.AccountManagementUseCase;
import com.bank.application.port.out.AccountRepository;
import com.bank.application.port.out.ClientRepository;
import com.bank.application.port.out.EventPublisher;
import com.bank.domain.model.Account;
import com.bank.domain.model.Client;
import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AccountManagementUseCase.
 * 
 * Hexagonal Architecture: Application Service
 * - Implements primary port (driving port)
 * - Orchestrates domain objects
 * - Uses secondary ports (driven ports) for infrastructure
 * - Contains no business logic (delegates to domain)
 * - Transactional boundary
 */
@ApplicationScoped
@Transactional
public class AccountManagementService implements AccountManagementUseCase {
    
    @Inject
    private AccountRepository accountRepository;
    
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private EventPublisher eventPublisher;
    
    @Override
    public AccountDTO openAccount(OpenAccountCommand command) {
        // Validate client exists
        Client client = clientRepository.findById(command.clientId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + command.clientId()));
        
        // Use provided account number or generate one
        AccountNumber accountNumber = command.accountNumber() != null ?
            command.accountNumber() : AccountNumber.generate();
        
        // Create domain account with Money from initialBalance
        Money initialBalance = command.initialBalance();
        
        // Create domain account
        Account account = new Account(
            accountNumber,
            initialBalance,
            command.accountType(),
            client
        );
        
        // Save through repository port
        accountRepository.save(account);
        
        // Publish domain events
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.clearDomainEvents();
        
        // Convert to DTO
        return toDTO(account, client);
    }
    
    @Override
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        // Domain logic for closing
        account.close();
        
        // Save through repository port
        accountRepository.save(account);
        
        // Publish domain events
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.clearDomainEvents();
    }
    
    @Override
    public AccountDTO getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        Client client = clientRepository.findById(account.getClientId())
            .orElse(null);
        
        return toDTO(account, client);
    }
    
    @Override
    public AccountDTO getAccountByNumber(AccountNumber accountNumber) {
        Account account = accountRepository.findByNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber.getValue()));
        
        Client client = clientRepository.findById(account.getClientId())
            .orElse(null);
        
        return toDTO(account, client);
    }
    
    @Override
    public List<AccountDTO> getClientAccounts(Long clientId) {
        List<Account> accounts = accountRepository.findByClientId(clientId);
        Client client = clientRepository.findById(clientId).orElse(null);
        
        return accounts.stream()
            .map(account -> toDTO(account, client))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        
        return accounts.stream()
            .map(account -> {
                Client client = clientRepository.findById(account.getClientId()).orElse(null);
                return toDTO(account, client);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public void reactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        
        // Domain logic for reactivating
        account.reactivate();
        
        // Save through repository port
        accountRepository.save(account);
        
        // Publish domain events
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.clearDomainEvents();
    }
    
    /**
     * Convert domain Account to DTO.
     * 
     * @param account The domain account
     * @param client The client (optional)
     * @return AccountDTO
     */
    private AccountDTO toDTO(Account account, Client client) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber().getValue());
        dto.setBalance(account.getBalance().getAmount());
        dto.setCurrency(account.getBalance().getCurrency());
        dto.setAccountType(account.getAccountType().name());
        dto.setClientId(account.getClientId());
        
        if (client != null) {
            dto.setClientName(client.getName());
        }
        
        return dto;
    }
}

// Made with Bob
