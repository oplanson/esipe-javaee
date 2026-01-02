/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.AccountDTO;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.ClientManagementUseCase;
import com.bank.application.port.out.AccountRepository;
import com.bank.application.port.out.ClientRepository;
import com.bank.application.port.out.EventPublisher;
import com.bank.domain.model.Account;
import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ClientManagementUseCase.
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
public class ClientManagementService implements ClientManagementUseCase {
    
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private AccountRepository accountRepository;
    
    @Inject
    private EventPublisher eventPublisher;
    
    @Override
    public ClientDTO createClient(CreateClientCommand command) {
        // Create domain client
        Client client = new Client(
            command.name(),
            command.email()
        );
        
        if (command.premium()) {
            client.makePremium();
        }
        
        // Save through repository port
        clientRepository.save(client);
        
        // Publish domain events
        client.getDomainEvents().forEach(eventPublisher::publish);
        client.clearDomainEvents();
        
        // Convert to DTO
        return toDTO(client);
    }
    
    @Override
    public ClientDTO updateClient(Long clientId, UpdateClientCommand command) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        
        // Update domain object
        if (command.name() != null) {
            client.updateName(command.name());
        }
        
        if (command.email() != null) {
            client.updateInfo(command.name() != null ? command.name() : client.getName(), command.email());
        }
        
        if (command.premium() != null) {
            if (command.premium()) {
                client.makePremium();
            } else {
                client.makeStandard();
            }
        }
        
        // Save through repository port
        clientRepository.save(client);
        
        // Publish domain events
        client.getDomainEvents().forEach(eventPublisher::publish);
        client.clearDomainEvents();
        
        // Convert to DTO
        return toDTO(client);
    }
    
    @Override
    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        
        // Check if client has accounts
        List<Account> accounts = accountRepository.findByClientId(clientId);
        if (!accounts.isEmpty()) {
            throw new IllegalStateException("Cannot delete client with existing accounts");
        }
        
        // Delete through repository port
        clientRepository.delete(client);
    }
    
    @Override
    public ClientDTO getClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        
        return toDTO(client);
    }
    
    @Override
    public ClientDTO getClientByEmail(Email email) {
        Client client = clientRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Client not found with email: " + email.getValue()));
        
        return toDTO(client);
    }
    
    @Override
    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        
        return clients.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ClientDTO> getPremiumClients() {
        List<Client> clients = clientRepository.findAll();
        
        return clients.stream()
            .filter(Client::isPremium)
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void upgradeToPremium(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        
        client.upgradeToPremium();
        clientRepository.save(client);
        
        // Publish domain events
        client.getDomainEvents().forEach(eventPublisher::publish);
        client.clearDomainEvents();
    }
    
    @Override
    public void downgradeFromPremium(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        
        client.downgradeFromPremium();
        clientRepository.save(client);
        
        // Publish domain events
        client.getDomainEvents().forEach(eventPublisher::publish);
        client.clearDomainEvents();
    }
    
    /**
     * Convert domain Client to DTO.
     * 
     * @param client The domain client
     * @return ClientDTO
     */
    private ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail().getValue());
        dto.setPremium(client.isPremium());
        
        // Load accounts if needed
        List<Account> accounts = accountRepository.findByClientId(client.getId());
        List<AccountDTO> accountDTOs = accounts.stream()
            .map(this::accountToDTO)
            .collect(Collectors.toList());
        dto.setAccounts(accountDTOs);
        
        return dto;
    }
    
    /**
     * Convert domain Account to DTO (simplified).
     * 
     * @param account The domain account
     * @return AccountDTO
     */
    private AccountDTO accountToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber().getValue());
        dto.setBalance(account.getBalance().getAmount());
        dto.setCurrency(account.getBalance().getCurrency());
        dto.setAccountType(account.getAccountType().name());
        dto.setClientId(account.getClientId());
        return dto;
    }
}

// Made with Bob
