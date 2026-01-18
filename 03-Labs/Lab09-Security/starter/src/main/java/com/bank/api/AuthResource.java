// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.api;

import com.bank.dto.AuthResponse;
import com.bank.dto.ErrorResponse;
import com.bank.dto.LoginRequest;
import com.bank.dto.RegisterRequest;
import com.bank.model.Role;
import com.bank.model.User;
import com.bank.security.DatabaseIdentityStore;
import com.bank.security.JwtService;
import com.bank.security.SecurityAuditService;
import com.bank.service.UserService;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REST resource for authentication operations
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());
    
    @Inject
    private DatabaseIdentityStore identityStore;
    
    @Inject
    private JwtService jwtService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @Context
    private HttpServletRequest request;
    
    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            
            // Get client info for audit
            String ipAddress = getClientIpAddress();
            String userAgent = request.getHeader("User-Agent");
            
            // Validate credentials using IdentityStore
            UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
            CredentialValidationResult result = identityStore.validate(credential);
            
            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                // Get user details
                User user = identityStore.getUserByUsername(username);
                
                // Generate JWT token
                String token = jwtService.generateToken(username, user.getRoles());
                
                // Convert roles to string set
                Set<String> roleNames = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());
                
                // Log successful login
                auditService.logSuccessfulLogin(username, ipAddress, userAgent);
                
                // Return token and user info
                AuthResponse response = new AuthResponse(token, username, roleNames);
                return Response.ok(response).build();
                
            } else {
                // Check if account is locked
                User user = identityStore.getUserByUsername(username);
                if (user != null && user.isAccountLocked()) {
                    auditService.logAccountLockout(username, ipAddress, userAgent);
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponse("Account locked", 
                                    "Account has been locked due to too many failed login attempts", 403))
                            .build();
                }
                
                // Log failed login
                auditService.logFailedLogin(username, ipAddress, userAgent, "Invalid credentials");
                
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Authentication failed", 
                                "Invalid username or password", 401))
                        .build();
            }
            
        } catch (Exception e) {
            LOGGER.severe("Login error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Login error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Register endpoint
     * POST /api/auth/register
     */
    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest registerRequest) {
        try {
            String username = registerRequest.getUsername();
            String email = registerRequest.getEmail();
            String password = registerRequest.getPassword();
            
            // Get client info for audit
            String ipAddress = getClientIpAddress();
            String userAgent = request.getHeader("User-Agent");
            
            // Register user with CUSTOMER role by default
            Set<Role> roles = Set.of(Role.CUSTOMER);
            User user = userService.registerUser(username, email, password, roles);
            
            // Generate JWT token
            String token = jwtService.generateToken(username, user.getRoles());
            
            // Convert roles to string set
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toSet());
            
            // Log registration
            auditService.logUserRegistration(username, ipAddress, userAgent);
            
            // Return token and user info
            AuthResponse response = new AuthResponse(token, username, roleNames);
            response.setMessage("Registration successful");
            
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Registration failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Registration failed", e.getMessage(), 400))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Registration error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Registration error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Logout endpoint (for audit logging)
     * POST /api/auth/logout
     */
    @POST
    @Path("/logout")
    public Response logout(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal() != null ? 
                    securityContext.getUserPrincipal().getName() : "anonymous";
            
            // Get client info for audit
            String ipAddress = getClientIpAddress();
            String userAgent = request.getHeader("User-Agent");
            
            // Log logout
            auditService.logLogout(username, ipAddress, userAgent);
            
            return Response.ok()
                    .entity(new AuthResponse("Logout successful"))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Logout error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Logout error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Get current user info
     * GET /api/auth/me
     */
    @GET
    @Path("/me")
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            if (securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Not authenticated", "No user logged in", 401))
                        .build();
            }
            
            String username = securityContext.getUserPrincipal().getName();
            User user = userService.getUserByUsername(username);
            
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("User not found", "User does not exist", 404))
                        .build();
            }
            
            // Convert roles to string set
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toSet());
            
            AuthResponse response = new AuthResponse(null, username, roleNames);
            response.setMessage("Current user info");
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOGGER.severe("Get current user error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
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
