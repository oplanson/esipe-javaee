/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.web.adapter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.ClientManagementUseCase;
import com.bank.domain.valueobject.Email;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Web adapter for Client operations (JSP/Servlet).
 *
 * Hexagonal Architecture: Primary Adapter (Driving Adapter)
 * - Receives HTTP requests from web browser
 * - Converts form data to use case commands
 * - Delegates to use cases (primary ports)
 * - Forwards to JSP views with DTOs
 * - Isolated from domain and application logic
 *
 * CDI-managed servlet (declared in web.xml, NOT with @WebServlet).
 * This allows proper CDI injection to work.
 */
public class ClientWebAdapter extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ClientWebAdapter.class.getName());
    
    @Inject
    private ClientManagementUseCase clientManagement;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        System.out.println("=== ClientWebAdapter.doGet() ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Path Info: " + pathInfo);
        System.out.println("clientManagement is null? " + (clientManagement == null));
        System.out.println("================================");
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all clients
                System.out.println(">>> Calling listClients()");
                listClients(request, response);
            } else if (pathInfo.equals("/new")) {
                // Show create form
                System.out.println(">>> Forwarding to client-form.jsp");
                request.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(request, response);
                System.out.println(">>> Forward completed");
            } else if (pathInfo.matches("/\\d+")) {
                // Show client details
                System.out.println(">>> Calling showClient()");
                Long id = Long.parseLong(pathInfo.substring(1));
                showClient(request, response, id);
            } else if (pathInfo.matches("/\\d+/edit")) {
                // Show edit form
                System.out.println(">>> Calling editClient()");
                Long id = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/edit")));
                editClient(request, response, id);
            } else {
                System.out.println(">>> Sending 404 error");
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(">>> Exception caught: " + e.getMessage());
            e.printStackTrace();
            LOGGER.log(Level.WARNING, "Client GET request failed: " + e.getMessage(), e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println(">>> Unexpected exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Create new client
                createClient(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                // Update client
                Long id = Long.parseLong(pathInfo.substring(1));
                updateClient(request, response, id);
            } else if (pathInfo.matches("/\\d+/delete")) {
                // Delete client
                Long id = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/delete")));
                deleteClient(request, response, id);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Client POST request failed: " + e.getMessage(), e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void listClients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ClientDTO> clients = clientManagement.getAllClients();
        request.setAttribute("clients", clients);
        request.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(request, response);
    }
    
    private void showClient(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {
        ClientDTO client = clientManagement.getClient(id);
        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/client-details.jsp").forward(request, response);
    }
    
    private void editClient(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {
        ClientDTO client = clientManagement.getClient(id);
        request.setAttribute("client", client);
        request.setAttribute("edit", true);
        request.getRequestDispatcher("/WEB-INF/views/client-form.jsp").forward(request, response);
    }
    
    private void createClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        boolean premium = "on".equals(request.getParameter("premium"));
        
        CreateClientCommand command = new CreateClientCommand(name, Email.of(email), premium);
        ClientDTO client = clientManagement.createClient(command);

        response.sendRedirect(request.getContextPath() + "/clients/" + client.getId());
    }
    
    private void updateClient(HttpServletRequest request, HttpServletResponse response, Long id)
            throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        Boolean premium = "on".equals(request.getParameter("premium")) ? true : null;
        
        UpdateClientCommand command = new UpdateClientCommand(id, name, Email.of(email), premium);
        clientManagement.updateClient(id, command);
        
        response.sendRedirect(request.getContextPath() + "/clients/" + id);
    }
    
    private void deleteClient(HttpServletRequest request, HttpServletResponse response, Long id)
            throws IOException {
        clientManagement.deleteClient(id);
        response.sendRedirect(request.getContextPath() + "/clients");
    }
}

// Made with Bob
