// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.web;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Web Controller for Client operations
 * Handles JSP-based web interface requests
 * Uses RestClientBuilder for programmatic REST client creation
 * Configured in web.xml
 */
public class ClientWebController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ClientWebController.class.getName());
    
    private ClientServiceClient clientServiceClient;
    private BankingAggregationService aggregationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Get the client service URL from MicroProfile Config
        // Prefer explicit `client.service.url`, otherwise fall back to the MP Rest Client config
        java.util.Optional<String> optClientUrl = ConfigProvider.getConfig()
            .getOptionalValue("client.service.url", String.class);
        String clientServiceUrl = optClientUrl.orElseGet(() ->
            ConfigProvider.getConfig().getOptionalValue(
                "com.bank.gateway.client.ClientServiceClient/mp-rest/url", String.class
            ).orElse("http://localhost:9081/api")
        );
        
        // Build REST client programmatically
        clientServiceClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(clientServiceUrl))
            .build(ClientServiceClient.class);
        
        // Create aggregation service manually
        java.util.Optional<String> optAccountUrl = ConfigProvider.getConfig()
            .getOptionalValue("account.service.url", String.class);
        String accountServiceUrl = optAccountUrl.orElseGet(() ->
            ConfigProvider.getConfig().getOptionalValue(
                "com.bank.gateway.client.AccountServiceClient/mp-rest/url", String.class
            ).orElse("http://localhost:9082/api")
        );
        
        aggregationService = new BankingAggregationService(
            clientServiceClient,
            RestClientBuilder.newBuilder()
                .baseUri(URI.create(accountServiceUrl))
                .build(com.bank.gateway.client.AccountServiceClient.class)
        );
        
        LOGGER.info("ClientWebController initialized with client service URL: " + clientServiceUrl);
    }
    
    private ClientServiceClient getClientServiceClient() {
        return clientServiceClient;
    }
    
    private BankingAggregationService getAggregationService() {
        return aggregationService;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        String action = request.getParameter("action");
        
        try {
            switch (path) {
                case "/clients":
                    if ("view".equals(action)) {
                        viewClient(request, response);
                    } else {
                        listClients(request, response);
                    }
                    break;
                    
                case "/clients/new":
                    showNewForm(request, response);
                    break;
                    
                case "/clients/edit":
                    showEditForm(request, response);
                    break;
                    
                case "/clients/delete":
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
                case "/clients/new":
                    createClient(request, response);
                    break;
                    
                case "/clients/edit":
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
        List<ClientWithAccountsDTO> clientsWithAccounts = getAggregationService().getAllClientsWithAccounts();
        
        request.setAttribute("clientsWithAccounts", clientsWithAccounts);
        request.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(request, response);
    }
    
    private void viewClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Viewing client: " + id);
        
        // Get aggregated data (client with accounts)
        ClientWithAccountsDTO clientWithAccounts = getAggregationService().getClientWithAccounts(id);
        
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
        
        ClientDTO client = getClientServiceClient().getClientById(id);
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
        
        getClientServiceClient().createClient(client);
        
        response.sendRedirect(request.getContextPath() + "/clients");
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
        
        getClientServiceClient().updateClient(id, client);
        
        response.sendRedirect(request.getContextPath() + "/clients");
    }
    
    private void deleteClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Deleting client: " + id);
        
        getClientServiceClient().deleteClient(id);
        
        response.sendRedirect(request.getContextPath() + "/clients");
    }
}

// Made with Bob
