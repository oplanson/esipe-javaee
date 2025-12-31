package com.bank.web;

import com.bank.model.Client;
import com.bank.model.Account;
import com.bank.service.ClientService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet for managing clients.
 * Handles all client-related HTTP requests.
 * Uses JavaBeans pattern (no CDI).
 *
 * TODO: Implement the MVC controller pattern:
 * 1. Complete init() method
 * 2. Implement doGet() routing
 * 3. Implement doPost() form handling
 * 4. Add helper methods for each action
 */
@WebServlet(
    name = "ClientController",
    urlPatterns = {"/clients", "/client"},
    loadOnStartup = 1
)
public class ClientController extends HttpServlet {
    
    private ClientService clientService;
    
    // Configuration properties (read from ServletContext init parameters)
    private int defaultPageSize;
    private boolean deletionEnabled;
    private String appName;
    
    /**
     * Initialize the servlet.
     *
     * TODO: Implement this method
     * Steps:
     * 1. Create ClientService instance (JavaBean)
     * 2. Read configuration from ServletContext init parameters
     * 3. Add some sample data for testing
     * 4. Log initialization message
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        // TODO: Initialize clientService (JavaBean)
        // clientService = new ClientService();
        
        // TODO: Read configuration from ServletContext
        // Example:
        // String pageSizeParam = getServletContext().getInitParameter("web.pagination.default.size");
        // defaultPageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 10;
        
        // TODO: Add sample data
        // Create 3-5 sample clients with accounts
        // Example:
        // Client client1 = new Client("John Doe", "john@example.com");
        // clientService.create(client1);
        // Account account1 = new Account("FR7612345678901234567890123", 1000.0, "CHECKING", client1.getId());
        // account1.setId(1L);
        // client1.addAccount(account1);
        
        
        log("ClientController initialized successfully");
    }
    
    /**
     * Handle GET requests.
     * Routes to appropriate handler based on action parameter.
     * 
     * TODO: Implement routing logic
     * Actions:
     * - null or "list": show client list
     * - "view": show client details
     * - "new": show empty form
     * - "edit": show form with client data
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // TODO: Implement routing
        // if (action == null || "list".equals(action)) {
        //     listClients(req, resp);
        // } else if ("view".equals(action)) {
        //     viewClient(req, resp);
        // } else if ...
        
    }
    
    /**
     * Handle POST requests.
     * Processes form submissions.
     * 
     * TODO: Implement form handling
     * Actions:
     * - "create": create new client
     * - "update": update existing client
     * - "delete": delete client
     * Use PRG pattern (Post-Redirect-Get)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // TODO: Implement form handling
        // if ("create".equals(action)) {
        //     createClient(req, resp);
        // } else if ...
        
    }
    
    /**
     * Display list of all clients.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get all clients from service
     * 2. Set clients as request attribute
     * 3. Set other attributes (appName, deletionEnabled)
     * 4. Forward to client-list.jsp
     */
    private void listClients(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // TODO: Get clients from service
        
        
        // TODO: Set attributes
        
        
        // TODO: Forward to JSP
        
    }
    
    /**
     * Display details of a specific client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get id parameter
     * 2. Find client by ID
     * 3. If not found, send 404 error
     * 4. Set client as request attribute
     * 5. Forward to client-details.jsp
     */
    private void viewClient(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // TODO: Get id parameter and parse to Long
        
        
        // TODO: Find client
        
        
        // TODO: Check if found
        
        
        // TODO: Set attribute and forward
        
    }
    
    /**
     * Show form for creating/editing client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. If editing, get id and find client
     * 2. Set client as attribute (null for new)
     * 3. Forward to client-form.jsp
     */
    private void showForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // TODO: If editing, get client
        
        
        // TODO: Set attribute and forward
        
    }
    
    /**
     * Create a new client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Get form parameters (name, email)
     * 2. Validate input
     * 3. Create Client object
     * 4. Save using service
     * 5. Redirect to list with success message (PRG pattern)
     */
    private void createClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        // TODO: Get parameters
        
        
        // TODO: Validate
        
        
        // TODO: Create and save
        
        
        // TODO: Redirect (PRG pattern)
        
    }
    
    /**
     * Update an existing client.
     * 
     * TODO: Implement this method
     * Similar to createClient but:
     * 1. Get id parameter
     * 2. Find existing client
     * 3. Update fields
     * 4. Save using service
     * 5. Redirect to list
     */
    private void updateClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        // TODO: Implement update logic
        
    }
    
    /**
     * Delete a client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Check if deletion is enabled (deletionEnabled)
     * 2. Get id parameter
     * 3. Delete using service
     * 4. Redirect to list
     */
    private void deleteClient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        // TODO: Implement delete logic
        
    }
    
    /**
     * Cleanup resources.
     */
    @Override
    public void destroy() {
        log("ClientController destroyed");
        super.destroy();
    }
}

// Made with Bob
