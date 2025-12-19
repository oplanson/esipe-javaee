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
 * Complete implementation with MVC pattern using JavaBeans.
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
     * Creates service instance and loads sample data.
     * Reads configuration from ServletContext init parameters.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Initialize clientService (JavaBean)
        clientService = new ClientService();
        
        // Read configuration from ServletContext init parameters
        String pageSizeParam = getServletContext().getInitParameter("web.pagination.default.size");
        defaultPageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 10;
        
        String deletionParam = getServletContext().getInitParameter("feature.client.deletion.enabled");
        deletionEnabled = (deletionParam != null) ? Boolean.parseBoolean(deletionParam) : true;
        
        appName = getServletContext().getInitParameter("app.name");
        if (appName == null) {
            appName = "Banking Application";
        }
        
        // Add sample data
        clientService.initializeSampleData();
        
        log("ClientController initialized successfully with " + clientService.count() + " clients");
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
            log("Error in doGet", e);
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
            log("Error in doPost", e);
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
        } else {
            clients = clientService.findAll();
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
            Client client = clientService.findById(id);
            
            if (client == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
                return;
            }
            
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
        
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=new&error=name_required");
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/client?action=new&error=email_required");
            return;
        }
        
        // Create client
        Client client = new Client(name.trim(), email.trim());
        clientService.create(client);
        
        log("Created new client: " + client);
        
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
            
            // Update client
            client.setName(name.trim());
            client.setEmail(email.trim());
            clientService.update(client);
            
            log("Updated client: " + client);
            
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
            boolean deleted = clientService.delete(id);
            
            if (deleted) {
                log("Deleted client with ID: " + id);
                resp.sendRedirect(req.getContextPath() + "/clients?message=deleted");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
        }
    }
    
    @Override
    public void destroy() {
        log("ClientController destroyed");
        super.destroy();
    }
}

// Made with Bob