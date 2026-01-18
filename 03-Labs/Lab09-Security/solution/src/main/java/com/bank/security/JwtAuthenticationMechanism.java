// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.security;

import com.bank.model.Role;
import io.jsonwebtoken.JwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Custom HTTP Authentication Mechanism for JWT tokens
 */
@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {
    
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationMechanism.class.getName());
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Inject
    private JwtService jwtService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context) 
            throws AuthenticationException {
        
        // Get Authorization header
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        // If no Authorization header, check if this is a public endpoint
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            // Allow public endpoints (login, register)
            String path = request.getRequestURI();
            if (isPublicEndpoint(path)) {
                return context.doNothing();
            }
            
            // For protected endpoints without token, return unauthorized
            LOGGER.warning("No JWT token found in request to: " + path);
            return context.responseUnauthorized();
        }
        
        // Extract token
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        
        try {
            // Validate token
            if (!jwtService.validateToken(token)) {
                LOGGER.warning("Invalid JWT token");
                return context.responseUnauthorized();
            }
            
            // Extract username and roles from token
            String username = jwtService.getUsernameFromToken(token);
            Set<Role> roles = jwtService.getRolesFromToken(token);
            
            if (username == null || roles == null) {
                LOGGER.warning("Could not extract username or roles from token");
                return context.responseUnauthorized();
            }
            
            // Convert roles to string set
            Set<String> roleNames = roles.stream()
                    .map(Role::name)
                    .collect(Collectors.toSet());
            
            // Log successful authentication
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            auditService.logResourceAccess(username, request.getRequestURI(), ipAddress, userAgent);
            
            // Notify container of successful authentication
            return context.notifyContainerAboutLogin(username, roleNames);
            
        } catch (JwtException e) {
            LOGGER.warning("JWT validation failed: " + e.getMessage());
            return context.responseUnauthorized();
        } catch (Exception e) {
            LOGGER.severe("Error validating JWT: " + e.getMessage());
            return context.responseUnauthorized();
        }
    }
    
    /**
     * Check if endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String path) {
        // Public API endpoints
        if (path.endsWith("/api/auth/login") || path.endsWith("/api/auth/register")) {
            return true;
        }
        
        // Health and metrics endpoints
        if (path.endsWith("/health/live") || path.endsWith("/health/ready") ||
            path.contains("/health/") || path.contains("/metrics")) {
            return true;
        }
        
        // OpenAPI documentation
        if (path.contains("/openapi") || path.contains("/swagger")) {
            return true;
        }
        
        // Static resources and home page
        if (path.equals("/") || path.endsWith("/index.html") ||
            path.contains("/css/") || path.contains("/js/") ||
            path.contains("/images/") || path.endsWith(".css") ||
            path.endsWith(".js") || path.endsWith(".png") ||
            path.endsWith(".jpg") || path.endsWith(".ico")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

// Made with Bob
