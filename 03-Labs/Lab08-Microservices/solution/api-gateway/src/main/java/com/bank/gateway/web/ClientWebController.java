// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.web;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Web Controller for Client operations
 * Handles JSP-based web interface requests
 * Uses CDI injection for REST clients to enable fault tolerance
 */
@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
public class ClientWebController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ClientWebController.class.getName());
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    @Inject
    private BankingAggregationService aggregationService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        String action = request.getParameter("action");
        
        try {
            switch (path) {
                case "/web/clients":
                    if ("view".equals(action)) {
                        viewClient(request, response);
                    } else {
                        listClients(request, response);
                    }
                    break;
                    
                case "/web/clients/new":
                    showNewForm(request, response);
                    break;
                    
                case "/web/clients/edit":
                    showEditForm(request, response);
                    break;
                    
                case "/web/clients/delete":
                    deleteClient(request, response);
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.severe("Error in ClientWebController: " + e.getMessage());
            request.setAttribute("error", "Service temporarily unavailable: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

        try {
            switch (path) {
                case "/web/clients/new":
                    createClient(request, response);
                    break;
                    
                case "/web/clients/edit":
                    updateClient(request, response);
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.severe("Error in ClientWebController POST: " + e.getMessage());
            request.setAttribute("error", "Failed to process request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void listClients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("Listing all clients with accounts");
        
        // Get aggregated data (clients with their accounts)
        List<ClientWithAccountsDTO> clientsWithAccounts = aggregationService.getAllClientsWithAccounts();
        
        request.setAttribute("clientsWithAccounts", clientsWithAccounts);
        request.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(request, response);
    }
    
    private void viewClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Viewing client: " + id);
        
        // Get aggregated data (client with accounts)
        ClientWithAccountsDTO clientWithAccounts = aggregationService.getClientWithAccounts(id);
        
        request.setAttribute("clientWithAccounts", clientWithAccounts);
        request.getRequestDispatcher("/WEB-INF/views/client-details.jsp").forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("Showing new client form");
        request.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Showing edit form for client: " + id);
        
        ClientDTO client = clientServiceClient.getClientById(id);
        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(request, response);
    }
    
    private void createClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        LOGGER.info("Creating new client");
        
        ClientDTO client = new ClientDTO();
        client.setFirstName(request.getParameter("firstName"));
        client.setLastName(request.getParameter("lastName"));
        client.setEmail(request.getParameter("email"));
        client.setPhone(request.getParameter("phone"));
        
        clientServiceClient.createClient(client);
        
        response.sendRedirect(request.getContextPath() + "/web/clients");
    }
    
    private void updateClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Updating client: " + id);
        
        ClientDTO client = new ClientDTO();
        client.setId(id);
        client.setFirstName(request.getParameter("firstName"));
        client.setLastName(request.getParameter("lastName"));
        client.setEmail(request.getParameter("email"));
        client.setPhone(request.getParameter("phone"));
        
        clientServiceClient.updateClient(id, client);
        
        response.sendRedirect(request.getContextPath() + "/web/clients");
    }
    
    private void deleteClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Deleting client: " + id);
        
        clientServiceClient.deleteClient(id);
        
        response.sendRedirect(request.getContextPath() + "/web/clients");
    }
}

// Made with Bob
