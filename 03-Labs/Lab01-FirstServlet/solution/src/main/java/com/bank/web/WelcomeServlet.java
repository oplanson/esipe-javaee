/*
 * © Copyright Olivier Planson - 2025
 */
package com.bank.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Welcome servlet that displays a greeting page with current date/time.
 * This servlet demonstrates basic HTTP GET request handling.
 *
 * @author Olivier Planson
 * @version 1.0
 */
@WebServlet(name = "WelcomeServlet", urlPatterns = {"/welcome"})
public class WelcomeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Handles HTTP GET requests.
     * Displays a welcome page with current date/time and navigation links.
     * 
     * @param request  The HTTP request
     * @param response The HTTP response
     * @throws ServletException If a servlet error occurs
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response content type
        response.setContentType("text/html;charset=UTF-8");
        
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        
        // Get PrintWriter to write response
        try (PrintWriter out = response.getWriter()) {
            // Write HTML response
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Welcome - Banking Application</title>");
            out.println("    <link rel='stylesheet' href='css/style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container'>");
            out.println("        <h1>🏦 Welcome to Banking Application</h1>");
            out.println("        <p class='subtitle'>Jakarta EE Course - Lab 01</p>");
            out.println("        <div class='card'>");
            out.println("            <h2>Hello!</h2>");
            out.println("            <p>Your first servlet is running successfully.</p>");
            out.println("            <p><strong>Current Date and Time:</strong></p>");
            out.println("            <p class='text-center' style='font-size: 1.2em; color: #667eea;'>");
            out.println("                " + formattedDateTime);
            out.println("            </p>");
            out.println("            <div class='mt-20'>");
            out.println("                <h3>Quick Navigation</h3>");
            out.println("                <nav>");
            out.println("                    <ul>");
            out.println("                        <li><a href='index.html'>Home Page</a></li>");
            out.println("                        <li><a href='clients'>View All Clients</a></li>");
            out.println("                        <li><a href='add-client.html'>Add New Client</a></li>");
            out.println("                    </ul>");
            out.println("                </nav>");
            out.println("            </div>");
            out.println("        </div>");
            out.println("        <footer>");
            out.println("            <p>&copy; Copyright Olivier Planson - 2025</p>");
            out.println("        </footer>");
            out.println("    </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    /**
     * Returns a short description of the servlet.
     * 
     * @return A String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Welcome Servlet - Displays greeting page with current date/time";
    }
}

