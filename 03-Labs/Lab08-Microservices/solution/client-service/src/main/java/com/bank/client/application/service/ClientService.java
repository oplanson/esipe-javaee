// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.application.service;

import com.bank.client.application.dto.ClientDTO;
import com.bank.client.application.mapper.ClientMapper;
import com.bank.client.domain.model.Client;
import com.bank.client.domain.port.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Client Service - Application Layer
 * Implements use cases for client management
 */
@ApplicationScoped
@Transactional
public class ClientService {
    
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private ClientMapper clientMapper;
    
    /**
     * Create a new client
     * @param clientDTO The client data
     * @return The created client
     * @throws IllegalArgumentException if email already exists
     */
    public ClientDTO createClient(ClientDTO clientDTO) {
        LOGGER.info("Creating new client: " + clientDTO.getEmail());
        
        // Check if email already exists
        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new IllegalArgumentException("Client with email " + clientDTO.getEmail() + " already exists");
        }
        
        // Convert DTO to domain model
        Client client = clientMapper.toDomain(clientDTO);
        
        // Validate domain model
        if (!client.isValid()) {
            throw new IllegalArgumentException("Invalid client data");
        }
        
        // Save client
        Client savedClient = clientRepository.save(client);
        
        LOGGER.info("Client created successfully with ID: " + savedClient.getId());
        
        // Convert back to DTO
        return clientMapper.toDTO(savedClient);
    }
    
    /**
     * Get a client by ID
     * @param id The client ID
     * @return Optional containing the client if found
     */
    public Optional<ClientDTO> getClientById(Long id) {
        LOGGER.info("Fetching client with ID: " + id);
        
        return clientRepository.findById(id)
                .map(clientMapper::toDTO);
    }
    
    /**
     * Get a client by email
     * @param email The client email
     * @return Optional containing the client if found
     */
    public Optional<ClientDTO> getClientByEmail(String email) {
        LOGGER.info("Fetching client with email: " + email);
        
        return clientRepository.findByEmail(email)
                .map(clientMapper::toDTO);
    }
    
    /**
     * Get all clients
     * @return List of all clients
     */
    public List<ClientDTO> getAllClients() {
        LOGGER.info("Fetching all clients");
        
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all premium clients
     * @return List of premium clients
     */
    public List<ClientDTO> getAllPremiumClients() {
        LOGGER.info("Fetching all premium clients");
        
        return clientRepository.findAllPremium().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update an existing client
     * @param id The client ID
     * @param clientDTO The updated client data
     * @return The updated client
     * @throws IllegalArgumentException if client not found or email already exists
     */
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        LOGGER.info("Updating client with ID: " + id);
        
        // Find existing client
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!existingClient.getEmail().equals(clientDTO.getEmail())) {
            if (clientRepository.existsByEmail(clientDTO.getEmail())) {
                throw new IllegalArgumentException("Client with email " + clientDTO.getEmail() + " already exists");
            }
        }
        
        // Update client from DTO
        clientMapper.updateFromDTO(existingClient, clientDTO);
        
        // Validate updated client
        if (!existingClient.isValid()) {
            throw new IllegalArgumentException("Invalid client data");
        }
        
        // Save updated client
        Client updatedClient = clientRepository.save(existingClient);
        
        LOGGER.info("Client updated successfully: " + id);
        
        // Convert back to DTO
        return clientMapper.toDTO(updatedClient);
    }
    
    /**
     * Upgrade a client to premium status
     * @param id The client ID
     * @return The updated client
     * @throws IllegalArgumentException if client not found
     */
    public ClientDTO upgradeToPremium(Long id) {
        LOGGER.info("Upgrading client to premium: " + id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));
        
        client.upgradeToPremium();
        Client updatedClient = clientRepository.save(client);
        
        LOGGER.info("Client upgraded to premium: " + id);
        
        return clientMapper.toDTO(updatedClient);
    }
    
    /**
     * Downgrade a client from premium status
     * @param id The client ID
     * @return The updated client
     * @throws IllegalArgumentException if client not found
     */
    public ClientDTO downgradeFromPremium(Long id) {
        LOGGER.info("Downgrading client from premium: " + id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));
        
        client.downgradeFromPremium();
        Client updatedClient = clientRepository.save(client);
        
        LOGGER.info("Client downgraded from premium: " + id);
        
        return clientMapper.toDTO(updatedClient);
    }
    
    /**
     * Delete a client
     * @param id The client ID
     * @throws IllegalArgumentException if client not found
     */
    public void deleteClient(Long id) {
        LOGGER.info("Deleting client with ID: " + id);
        
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Client not found with ID: " + id);
        }
        
        clientRepository.deleteById(id);
        
        LOGGER.info("Client deleted successfully: " + id);
    }
    
    /**
     * Check if a client exists
     * @param id The client ID
     * @return true if client exists, false otherwise
     */
    public boolean clientExists(Long id) {
        return clientRepository.existsById(id);
    }
    
    /**
     * Get total number of clients
     * @return Total number of clients
     */
    public long getTotalClients() {
        return clientRepository.count();
    }
    
    /**
     * Get number of premium clients
     * @return Number of premium clients
     */
    public long getPremiumClientsCount() {
        return clientRepository.countPremium();
    }
}

// Made with Bob
