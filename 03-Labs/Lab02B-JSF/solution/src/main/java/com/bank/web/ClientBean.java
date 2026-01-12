// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.web;

import com.bank.model.Client;
import com.bank.model.Address;
import com.bank.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Managed bean for client operations in Lab 02B - JSF Client Management
 */
@Named
@SessionScoped
public class ClientBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ClientService clientService;
    
    private Client client = new Client();
    private List<Client> clients;
    private List<Client> filteredClients;
    private String searchTerm = "";
    private Long selectedClientId;
    
    @PostConstruct
    public void init() {
        loadClients();
        client.setAddress(new Address());
    }
    
    /**
     * Load all clients from service
     */
    public void loadClients() {
        clients = clientService.getAllClients();
        filteredClients = null;
    }
    
    /**
     * Search clients based on search term (AJAX method)
     */
    public void search() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            filteredClients = null;
        } else {
            filteredClients = clientService.searchClients(searchTerm);
        }
    }
    
    /**
     * Save client (create or update)
     */
    public String save() {
        try {
            clientService.save(client);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", 
                      client.getId() == null ? "Client created successfully" : "Client updated successfully");
            loadClients();
            reset();
            return "client-list?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save client: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create new client - navigate to form
     */
    public String createNew() {
        reset();
        return "client-form?faces-redirect=true";
    }
    
    /**
     * Edit client - load client and navigate to form
     */
    public String edit() {
        try {
            client = clientService.findById(selectedClientId);
            if (client == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Client not found");
                return null;
            }
            if (client.getAddress() == null) {
                client.setAddress(new Address());
            }
            return "client-form?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load client: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Delete client
     */
    public String delete() {
        try {
            boolean deleted = clientService.delete(selectedClientId);
            if (deleted) {
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Client deleted successfully");
                loadClients();
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Client not found");
            }
            return null; // Stay on same page
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete client: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cancel operation and return to list
     */
    public String cancel() {
        reset();
        return "client-list?faces-redirect=true";
    }
    
    /**
     * View client details
     */
    public String viewDetails() {
        try {
            client = clientService.findById(selectedClientId);
            if (client == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Client not found");
                return null;
            }
            return "client-details?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load client: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Reset client form
     */
    private void reset() {
        client = new Client();
        client.setAddress(new Address());
    }
    
    /**
     * Add FacesMessage
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public List<Client> getClients() {
        return clients;
    }
    
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
    
    public List<Client> getFilteredClients() {
        return filteredClients;
    }
    
    public void setFilteredClients(List<Client> filteredClients) {
        this.filteredClients = filteredClients;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public Long getSelectedClientId() {
        return selectedClientId;
    }
    
    public void setSelectedClientId(Long selectedClientId) {
        this.selectedClientId = selectedClientId;
    }
}

// Made with Bob
