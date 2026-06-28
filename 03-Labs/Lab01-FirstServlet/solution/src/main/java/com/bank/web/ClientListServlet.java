/*
 * © Copyright Olivier Planson - 2025
 */
package com.bank.web;

import com.bank.model.Client;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client List Servlet - Manages client listing and creation.
 *
 * @author Olivier Planson
 * @version 1.0
 */
@WebServlet(name = "ClientListServlet", urlPatterns = {"/clients"})
public class ClientListServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    // Single servlet instance is shared across all request threads, so the
    // backing list must be thread-safe (concurrent doGet iteration / doPost add).
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    
    @Override
    public void init() throws ServletException {
        super.init();
        clients.add(new Client(1L, "John Smith", "john.smith@email.com", "+1-555-0101"));
        clients.add(new Client(2L, "Emma Johnson", "emma.j@email.com", "+1-555-0102"));
        clients.add(new Client(3L, "Michael Brown", "m.brown@email.com", "+1-555-0103"));
        clients.add(new Client(4L, "Sarah Davis", "sarah.davis@email.com", "+1-555-0104"));
        clients.add(new Client(5L, "James Wilson", "james.w@email.com", "+1-555-0105"));
        log("ClientListServlet initialized with " + clients.size() + " sample clients");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <title>Client List - Banking Application</title>");
            out.println("    <link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container'>");
            out.println("        <h1>Client Management</h1>");
            out.println("        <p class='subtitle'>Total Clients: " + clients.size() + "</p>");
            
            String message = request.getParameter("message");
            if ("success".equals(message)) {
                out.println("        <div class='message success'>Client added successfully!</div>");
            }
            
            out.println("        <div class='card'>");
            out.println("            <h2>Client List</h2>");
            out.println("            <p><a href='add-client.html' class='btn'>Add New Client</a></p>");
            
            if (clients.isEmpty()) {
                out.println("            <p class='message info'>No clients found.</p>");
            } else {
                out.println("            <table>");
                out.println("                <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th></tr></thead>");
                out.println("                <tbody>");
                for (Client client : clients) {
                    out.println("                    <tr>");
                    out.println("                        <td>" + client.getId() + "</td>");
                    out.println("                        <td>" + escapeHtml(client.getName()) + "</td>");
                    out.println("                        <td>" + escapeHtml(client.getEmail()) + "</td>");
                    out.println("                        <td>" + escapeHtml(client.getPhone()) + "</td>");
                    out.println("                    </tr>");
                }
                out.println("                </tbody>");
                out.println("            </table>");
            }
            out.println("            <div class='mt-20'><a href='index.html'>Back to Home</a></div>");
            out.println("        </div>");
            out.println("        <footer><p>&copy; Copyright Olivier Planson - 2025</p></footer>");
            out.println("    </div>");
            out.println("</body></html>");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty()) {
            response.sendRedirect("add-client.html?error=missing");
            return;
        }
        
        Long newId = clients.stream().mapToLong(Client::getId).max().orElse(0L) + 1;
        Client newClient = new Client(newId, name.trim(), email.trim(), phone.trim());
        clients.add(newClient);
        log("New client added: " + newClient);
        response.sendRedirect("clients?message=success");
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    @Override
    public String getServletInfo() {
        return "Client List Servlet";
    }
}
