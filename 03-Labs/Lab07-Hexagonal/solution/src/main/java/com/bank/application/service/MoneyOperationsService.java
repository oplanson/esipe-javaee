/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.DepositCommand;
import com.bank.application.command.TransferCommand;
import com.bank.application.command.WithdrawCommand;
import com.bank.application.port.in.MoneyOperationsUseCase;
import com.bank.application.port.out.AccountRepository;
import com.bank.application.port.out.EventPublisher;
import com.bank.domain.model.Account;
import com.bank.domain.service.TransferService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Implementation of MoneyOperationsUseCase.
 * 
 * Hexagonal Architecture: Application Service
 * - Implements primary port (driving port)
 * - Orchestrates domain objects and services
 * - Uses secondary ports (driven ports) for infrastructure
 * - Contains no business logic (delegates to domain)
 * - Transactional boundary
 */
@ApplicationScoped
@Transactional
public class MoneyOperationsService implements MoneyOperationsUseCase {
    
    @Inject
    private AccountRepository accountRepository;
    
    @Inject
    private TransferService transferService;
    
    @Inject
    private EventPublisher eventPublisher;
    
    @Override
    public void deposit(DepositCommand command) {
        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));
        
        // Domain logic for deposit
        account.deposit(command.amount());
        
        // Save through repository port
        accountRepository.save(account);
        
        // Publish domain events
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.clearDomainEvents();
    }
    
    @Override
    public void withdraw(WithdrawCommand command) {
        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));
        
        // Domain logic for withdrawal
        account.withdraw(command.amount());
        
        // Save through repository port
        accountRepository.save(account);
        
        // Publish domain events
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.clearDomainEvents();
    }
    
    @Override
    public void transfer(TransferCommand command) {
        Account fromAccount = accountRepository.findById(command.fromAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + command.fromAccountId()));
        
        Account toAccount = accountRepository.findById(command.toAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + command.toAccountId()));
        
        // Use domain service for transfer
        transferService.transfer(fromAccount, toAccount, command.amount());
        
        // Save both accounts through repository port
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // Publish domain events from both accounts
        fromAccount.getDomainEvents().forEach(eventPublisher::publish);
        fromAccount.clearDomainEvents();
        
        toAccount.getDomainEvents().forEach(eventPublisher::publish);
        toAccount.clearDomainEvents();
    }
}

// Made with Bob
