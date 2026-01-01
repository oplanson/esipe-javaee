package com.bank.web;

/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.service.ClientService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller servlet for managing clients.
 * Handles all client-related HTTP requests.
 * 
 * Updated for Lab 4 with CDI:
 * - @Inject for dependency injection
 * - @ConfigProperty for configuration
 * - No more Singleton pattern
 * - Cleaner, more testable code
 */
@WebServlet(
    name = "ClientController",
    urlPatterns = {"/clients", "/client"},
    loadOnStartup = 1
)
public class ClientController extends HttpServlet {
    
    /**
     * ClientService injected by CDI.
     * No need for getInstance() - CDI manages the lifecycle.
     */
    @Inject
    private ClientService clientService;
    
    /**
     * Logger injected by CDI.
     * Automatically configured with this class's name.
     */
    @Inject
    private Logger logger;
    
    /**
     * Configuration properties injected by MicroProfile Config.
     * Values come from microprofile-config.properties or environment variables.
     */
    @Inject
    @ConfigProperty(name = "web.pagination.default.size", defaultValue = "10")
    private Integer defaultPageSize;
    
    @Inject
    @ConfigProperty(name = "feature.client.deletion.enabled", defaultValue = "true")
    private Boolean deletionEnabled;
    
    @Inject
    @ConfigProperty(name = "app.name", defaultValue = "Banking Application")
    private String appName;
    
    /**
     * Initialize the servlet.
     * With CDI, dependencies are already injected.
     * No need to manually get instances or read configuration.
     *
     * Note: Cannot call service methods here because RequestScoped
     * EntityManager is not available during servlet initialization.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        logger.info("ClientController initialized with CDI");
        logger.info("App name: " + appName);
        logger.info("Default page size: " + defaultPageSize);
        logger.info("Deletion enabled: " + deletionEnabled);
        // Note: Cannot count clients here - RequestScoped context not active
    }
    
    /**
     * Handle GET requests.
     * Routes to appropriate handler based on action parameter.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        String path = req.getServletPath();
        
        try {
            // Route based on path and action
            if ("/clients".equals(path)) {
                listClients(req, resp);
            } else if ("/client".equals(path)) {
                if (action == null || "list".equals(action)) {
                    resp.sendRedirect(req.getContextPath() + "/clients");
                } else if ("view".equals(action)) {
                    viewClient(req, resp);
                } else if ("new".equals(action)) {
                    showForm(req, resp, null);
                } else if ("edit".equals(action)) {
                    editClient(req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in doGet: " + e.getMessage());
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
    
    /**
     * Handle POST requests.
     * Processes form submissions for create, update, and delete.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                createClient(req, resp);
            } else if ("update".equals(action)) {
                updateClient(req, resp);
            } else if ("delete".equals(action)) {
                deleteClient(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.severe("Error in doPost: " + e.getMessage());
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
    
    /**
     * Display list of all clients.
     */
    private void listClients(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Get search parameter if present
        String search = req.getParameter("search");
        List<Client> clients;
        
        if (search != null && !search.trim().isEmpty()) {
            clients = clientService.findByName(search);
            req.setAttribute("search", search);
            logger.info("Searching clients by name: " + search + " - Found: " + clients.size());
        } else {
            clients = clientService.findAll();
            logger.info("Listing all clients - Total: " + clients.size());
        }
        
        // Set attributes
        req.setAttribute("clients", clients);
        req.setAttribute("appName", appName);
        req.setAttribute("deletionEnabled", deletionEnabled);
        
        // Forward to JSP
        req.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(req, resp);
    }
    
    /**
     * Display details of a specific client.
     */
    private void viewClient(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Client client = clientService.findByIdWithAccounts(id);
            
            if (client == null) {
                logger.warning("Client not found: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
                return;
            }
            
            logger.info("Viewing client: " + id);
            req.setAttribute("client", client);
            req.setAttribute("deletionEnabled", deletionEnabled);
            req.getRequestDispatcher("/WEB-INF/views/client-details.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
        }
    }
    
    /**
     * Show form for editing a client.
     */
    private void editClient(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Client client = clientService.findById(id);
            
            if (client == null) {
                logger.warning("Client not found for edit: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
                return;
            }
            
            showForm(req, resp, client);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
        }
    }
    
    /**
     * Show client form (for create or edit).
     */
    private void showForm(HttpServletRequest req, HttpServletResponse resp, Client client)
            throws ServletException, IOException {
        
        req.setAttribute("client", client);
        req.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(req, resp);
    }
    
    /**
     * Create a new client.
     */
    private void createClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        // Get form parameters
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String premiumParam = req.getParameter("premium");
        boolean premium = "true".equals(premiumParam);
        
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=new&error=name_required");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=new&error=email_required");
            return;
        }
        
        // Create client - transaction managed by @Transactional in service
        Client client = new Client(name.trim(), email.trim(), premium);
        clientService.create(client);
        
        logger.info("Created new client: " + client.getId() + " (Premium: " + premium + ")");
        
        // PRG pattern: redirect to list with success message
        resp.sendRedirect(req.getContextPath() + "/clients?message=created");
    }
    
    /**
     * Update an existing client.
     */
    private void updateClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        // Get form parameters
        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String premiumParam = req.getParameter("premium");
        boolean premium = "true".equals(premiumParam);
        
        // Validate input
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID is required");
            return;
        }
        
        if (name == null || name.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=edit&id=" + idParam + "&error=name_required");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=edit&id=" + idParam + "&error=email_required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Client client = clientService.findById(id);
            
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
                return;
            }
            
            // Update client - transaction managed by @Transactional in service
            client.setName(name.trim());
            client.setEmail(email.trim());
            client.setPremium(premium);
            clientService.update(client);
            
            logger.info("Updated client: " + id + " (Premium: " + premium + ")");
            
            // PRG pattern: redirect to details with success message
            resp.sendRedirect(req.getContextPath() + "/client?action=view&id=" + id + "&message=updated");
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
        }
    }
    
    /**
     * Delete a client.
     */
    private void deleteClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        if (!deletionEnabled) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Client deletion is disabled");
            return;
        }
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Delete client - transaction managed by @Transactional in service
            boolean deleted = clientService.delete(id);
            
            if (deleted) {
                logger.info("Deleted client: " + id);
                resp.sendRedirect(req.getContextPath() + "/clients?message=deleted");
            } else {
                logger.warning("Client not found for deletion: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
        }
    }
    
    @Override
    public void destroy() {
        logger.info("ClientController destroyed");
        super.destroy();
    }
}

// Made with Bob
