<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 09: Secure Banking Application

**Duration:** 3 hours  
**Difficulty:** Advanced  
**Prerequisites:** Labs 1-5, Lecture 9 (Jakarta EE Security)

---

## 📋 Objectives

In this lab, you will implement comprehensive security features for a banking application:

1. **Database-backed authentication** with custom IdentityStore
2. **Role-based authorization** (@RolesAllowed, SecurityContext)
3. **JWT token generation and validation**
4. **Secure REST API endpoints**
5. **Password hashing** with PBKDF2
6. **Security audit logging**
7. **HTTPS configuration**
8. **Security headers** (CORS, CSP, etc.)

---

## 🎯 Learning Outcomes

By completing this lab, you will:

- Implement Jakarta Security API
- Create custom authentication mechanisms
- Secure REST endpoints with JWT
- Apply security best practices
- Prevent common vulnerabilities (SQL injection, XSS, CSRF)
- Implement comprehensive audit logging

---

## 📁 Project Structure

```
Lab09-Security/
├── solution/
│   ├── pom.xml
│   ├── Containerfile
│   ├── docker-compose.yml
│   └── src/main/
│       ├── java/com/bank/
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   ├── Role.java
│       │   │   ├── Account.java
│       │   │   └── SecurityAuditLog.java
│       │   ├── security/
│       │   │   ├── DatabaseIdentityStore.java
│       │   │   ├── JwtService.java
│       │   │   ├── JwtAuthenticationMechanism.java
│       │   │   ├── PasswordService.java
│       │   │   └── SecurityAuditService.java
│       │   ├── api/
│       │   │   ├── AuthResource.java
│       │   │   ├── AccountResource.java
│       │   │   └── UserResource.java
│       │   ├── service/
│       │   │   ├── UserService.java
│       │   │   └── AccountService.java
│       │   ├── filter/
│       │   │   ├── SecurityHeadersFilter.java
│       │   │   └── CorsFilter.java
│       │   └── config/
│       │       └── SecurityConfig.java
│       ├── liberty/config/
│       │   ├── server.xml
│       │   └── bootstrap.properties
│       ├── resources/
│       │   └── META-INF/
│       │       ├── persistence.xml
│       │       └── microprofile-config.properties
│       └── webapp/
│           ├── index.html
│           ├── login.html
│           └── WEB-INF/
│               └── web.xml
├── starter/
│   └── (same structure with TODOs)
├── README.md
├── test-lab.sh
├── podman-test.sh
└── TESTING-GUIDE.md
```

---

## 🚀 Part A: Database Schema and User Model (30 minutes)

### Step 1: Create User Entity

Create `src/main/java/com/bank/model/User.java`:

```java
package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(
        name = "User.findByUsername",
        query = "SELECT u FROM User u WHERE u.username = :username"
    ),
    @NamedQuery(
        name = "User.findByEmail",
        query = "SELECT u FROM User u WHERE u.email = :email"
    )
})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password; // Hashed password
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(name = "account_locked")
    private boolean accountLocked = false;
    
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    // ...
}
```

### Step 2: Create Role Enum

Create `src/main/java/com/bank/model/Role.java`:

```java
package com.bank.model;

public enum Role {
    ADMIN,
    MANAGER,
    TELLER,
    CUSTOMER
}
```

### Step 3: Create Security Audit Log Entity

Create `src/main/java/com/bank/model/SecurityAuditLog.java`:

```java
package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_logs")
public class SecurityAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String action; // LOGIN, LOGOUT, ACCESS_DENIED, etc.
    
    private String resource;
    
    @Column(nullable = false)
    private String result; // SUCCESS, FAILURE
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    private String details;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    // ...
}
```

---

## 🔐 Part B: Password Hashing Service (20 minutes)

### Step 4: Implement Password Service

Create `src/main/java/com/bank/security/PasswordService.java`:

```java
package com.bank.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PasswordService {
    
    @Inject
    private Pbkdf2PasswordHash passwordHash;
    
    private static final int ITERATIONS = 310000;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int SALT_SIZE = 64;
    private static final int KEY_SIZE = 64;
    
    public String hashPassword(String plainPassword) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", String.valueOf(ITERATIONS));
        parameters.put("Pbkdf2PasswordHash.Algorithm", ALGORITHM);
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", String.valueOf(SALT_SIZE));
        parameters.put("Pbkdf2PasswordHash.KeySizeBytes", String.valueOf(KEY_SIZE));
        
        passwordHash.initialize(parameters);
        return passwordHash.generate(plainPassword.toCharArray());
    }
    
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordHash.verify(plainPassword.toCharArray(), hashedPassword);
    }
}
```

**TODO for students:**
- Initialize password hash parameters
- Implement hash generation
- Implement password verification

---

## 🔑 Part C: Custom Identity Store (30 minutes)

### Step 5: Implement Database Identity Store

Create `src/main/java/com/bank/security/DatabaseIdentityStore.java`:

```java
package com.bank.security;

import com.bank.model.Role;
import com.bank.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {
    
    private static final Logger logger = Logger.getLogger(DatabaseIdentityStore.class.getName());
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private PasswordService passwordService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential upc = (UsernamePasswordCredential) credential;
            String username = upc.getCaller();
            String password = upc.getPasswordAsString();
            
            try {
                User user = em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
                
                // Check if account is enabled and not locked
                if (!user.isEnabled()) {
                    auditService.logSecurityEvent(username, "LOGIN", null, "FAILURE", 
                        "Account disabled");
                    return CredentialValidationResult.INVALID_RESULT;
                }
                
                if (user.isAccountLocked()) {
                    auditService.logSecurityEvent(username, "LOGIN", null, "FAILURE", 
                        "Account locked");
                    return CredentialValidationResult.INVALID_RESULT;
                }
                
                // Verify password
                if (passwordService.verifyPassword(password, user.getPassword())) {
                    // Reset failed login attempts
                    user.setFailedLoginAttempts(0);
                    user.setLastLogin(java.time.LocalDateTime.now());
                    em.merge(user);
                    
                    // Convert roles to strings
                    Set<String> roles = user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());
                    
                    auditService.logSecurityEvent(username, "LOGIN", null, "SUCCESS", null);
                    
                    return new CredentialValidationResult(username, roles);
                } else {
                    // Increment failed login attempts
                    user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                    
                    // Lock account after 5 failed attempts
                    if (user.getFailedLoginAttempts() >= 5) {
                        user.setAccountLocked(true);
                        logger.warning("Account locked for user: " + username);
                    }
                    
                    em.merge(user);
                    
                    auditService.logSecurityEvent(username, "LOGIN", null, "FAILURE", 
                        "Invalid password");
                }
                
            } catch (NoResultException e) {
                auditService.logSecurityEvent(username, "LOGIN", null, "FAILURE", 
                    "User not found");
            }
        }
        
        return CredentialValidationResult.INVALID_RESULT;
    }
}
```

**TODO for students:**
- Implement credential validation
- Add account lockout logic
- Integrate audit logging

---

## 🎫 Part D: JWT Service (30 minutes)

### Step 6: Implement JWT Service

Create `src/main/java/com/bank/security/JwtService.java`:

```java
package com.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    
    @Inject
    @ConfigProperty(name = "jwt.secret", defaultValue = "your-256-bit-secret-key-change-this-in-production-environment")
    private String secretKey;
    
    @Inject
    @ConfigProperty(name = "jwt.expiration", defaultValue = "3600000") // 1 hour
    private Long expirationTime;
    
    @Inject
    @ConfigProperty(name = "jwt.issuer", defaultValue = "banking-app")
    private String issuer;
    
    public String generateToken(String username, Set<String> roles) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .setIssuer(issuer)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    
    public Claims parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return Set.copyOf((java.util.List<String>) claims.get("roles"));
    }
}
```

**TODO for students:**
- Implement token generation
- Implement token parsing
- Add token validation

---

## 🛡️ Part E: JWT Authentication Mechanism (25 minutes)

### Step 7: Implement JWT Authentication Mechanism

Create `src/main/java/com/bank/security/JwtAuthenticationMechanism.java`:

```java
package com.bank.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;

@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {
    
    @Inject
    private JwtService jwtService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @Override
    public AuthenticationStatus validateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context) throws AuthenticationException {
        
        // Skip authentication for public endpoints
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            return context.doNothing();
        }
        
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtService.validateToken(token)) {
                    String username = jwtService.getUsernameFromToken(token);
                    Set<String> roles = jwtService.getRolesFromToken(token);
                    
                    return context.notifyContainerAboutLogin(username, roles);
                } else {
                    auditService.logSecurityEvent("unknown", "ACCESS", path, 
                        "FAILURE", "Invalid or expired token");
                }
            } catch (Exception e) {
                auditService.logSecurityEvent("unknown", "ACCESS", path, 
                    "FAILURE", "Token parsing error: " + e.getMessage());
            }
        }
        
        // No valid token found
        return context.responseUnauthorized();
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.endsWith("/auth/login") ||
               path.endsWith("/auth/register") ||
               path.endsWith("/") ||
               path.endsWith("/index.html") ||
               path.endsWith("/login.html") ||
               path.contains("/health") ||
               path.contains("/metrics");
    }
}
```

**TODO for students:**
- Implement token extraction
- Add token validation
- Handle authentication errors

---

## 🔒 Part F: Secure REST Endpoints (30 minutes)

### Step 8: Create Authentication Resource

Create `src/main/java/com/bank/api/AuthResource.java`:

```java
package com.bank.api;

import com.bank.model.Role;
import com.bank.model.User;
import com.bank.security.JwtService;
import com.bank.security.PasswordService;
import com.bank.security.SecurityAuditService;
import com.bank.service.UserService;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Set;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @Inject
    private IdentityStoreHandler identityStoreHandler;
    
    @Inject
    private JwtService jwtService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private PasswordService passwordService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(
            request.getUsername(),
            request.getPassword()
        );
        
        CredentialValidationResult result = identityStoreHandler.validate(credential);
        
        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = jwtService.generateToken(
                result.getCallerPrincipal().getName(),
                result.getCallerGroups()
            );
            
            return Response.ok(new TokenResponse(token, 
                result.getCallerPrincipal().getName(),
                result.getCallerGroups()
            )).build();
        }
        
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(new ErrorResponse("Invalid credentials"))
            .build();
    }
    
    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordService.hashPassword(request.getPassword()));
            user.setRoles(Set.of(Role.CUSTOMER)); // Default role
            
            userService.createUser(user);
            
            auditService.logSecurityEvent(user.getUsername(), "REGISTER", 
                null, "SUCCESS", null);
            
            return Response.status(Response.Status.CREATED)
                .entity(new MessageResponse("User registered successfully"))
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Registration failed: " + e.getMessage()))
                .build();
        }
    }
    
    // DTOs
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        
        // Getters and setters
    }
    
    public static class RegisterRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        
        // Getters and setters
    }
    
    public static class TokenResponse {
        private String token;
        private String username;
        private Set<String> roles;
        
        public TokenResponse(String token, String username, Set<String> roles) {
            this.token = token;
            this.username = username;
            this.roles = roles;
        }
        
        // Getters and setters
    }
    
    public static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        // Getters
    }
    
    public static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        // Getters
    }
}
```

### Step 9: Create Secured Account Resource

Create `src/main/java/com/bank/api/AccountResource.java`:

```java
package com.bank.api;

import com.bank.model.Account;
import com.bank.service.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    
    @Inject
    private AccountService accountService;
    
    @Inject
    private SecurityContext securityContext;
    
    @GET
    @RolesAllowed({"ADMIN", "TELLER", "CUSTOMER"})
    public Response getAccounts() {
        String username = securityContext.getCallerPrincipal().getName();
        
        if (securityContext.isCallerInRole("CUSTOMER")) {
            // Customers see only their accounts
            List<Account> accounts = accountService.getAccountsByUsername(username);
            return Response.ok(accounts).build();
        }
        
        // Admin and teller see all accounts
        List<Account> accounts = accountService.getAllAccounts();
        return Response.ok(accounts).build();
    }
    
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "TELLER", "CUSTOMER"})
    public Response getAccount(@PathParam("id") Long id) {
        Account account = accountService.getAccount(id);
        
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Customers can only access their own accounts
        if (securityContext.isCallerInRole("CUSTOMER")) {
            String username = securityContext.getCallerPrincipal().getName();
            if (!account.getOwnerUsername().equals(username)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        }
        
        return Response.ok(account).build();
    }
    
    @POST
    @RolesAllowed({"ADMIN", "TELLER"})
    public Response createAccount(Account account) {
        Account created = accountService.createAccount(account);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteAccount(@PathParam("id") Long id) {
        accountService.deleteAccount(id);
        return Response.noContent().build();
    }
}
```

**TODO for students:**
- Implement role-based access control
- Add ownership validation for customers
- Integrate security context

---

## 📊 Part G: Security Audit Service (20 minutes)

### Step 10: Implement Security Audit Service

Create `src/main/java/com/bank/security/SecurityAuditService.java`:

```java
package com.bank.security;

import com.bank.model.SecurityAuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.logging.Logger;

@ApplicationScoped
public class SecurityAuditService {
    
    private static final Logger logger = Logger.getLogger(SecurityAuditService.class.getName());
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private SecurityContext securityContext;
    
    @Inject
    private HttpServletRequest request;
    
    @Transactional
    public void logSecurityEvent(String username, String action, 
                                 String resource, String result, String details) {
        try {
            SecurityAuditLog log = new SecurityAuditLog();
            log.setUsername(username != null ? username : getCurrentUsername());
            log.setAction(action);
            log.setResource(resource);
            log.setResult(result);
            log.setDetails(details);
            log.setIpAddress(getClientIpAddress());
            log.setUserAgent(request.getHeader("User-Agent"));
            
            em.persist(log);
            
            logger.info(String.format("Security event: %s - %s - %s - %s", 
                username, action, resource, result));
                
        } catch (Exception e) {
            logger.severe("Failed to log security event: " + e.getMessage());
        }
    }
    
    private String getCurrentUsername() {
        Principal principal = securityContext.getCallerPrincipal();
        return principal != null ? principal.getName() : "anonymous";
    }
    
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

---

## 🛡️ Part H: Security Filters (20 minutes)

### Step 11: Implement Security Headers Filter

Create `src/main/java/com/bank/filter/SecurityHeadersFilter.java`:

```java
package com.bank.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SecurityHeadersFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        
        // Prevent clickjacking
        responseContext.getHeaders().add("X-Frame-Options", "DENY");
        
        // XSS protection
        responseContext.getHeaders().add("X-Content-Type-Options", "nosniff");
        responseContext.getHeaders().add("X-XSS-Protection", "1; mode=block");
        
        // Content Security Policy
        responseContext.getHeaders().add("Content-Security-Policy",
            "default-src 'self'; script-src 'self' 'unsafe-inline'; " +
            "style-src 'self' 'unsafe-inline'; img-src 'self' data:;");
        
        // HTTPS enforcement (in production)
        responseContext.getHeaders().add("Strict-Transport-Security",
            "max-age=31536000; includeSubDomains");
        
        // Referrer policy
        responseContext.getHeaders().add("Referrer-Policy",
            "strict-origin-when-cross-origin");
        
        // Permissions policy
        responseContext.getHeaders().add("Permissions-Policy",
            "geolocation=(), microphone=(), camera=()");
    }
}
```

### Step 12: Implement CORS Filter

Create `src/main/java/com/bank/filter/CorsFilter.java`:

```java
package com.bank.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        
        // Allow specific origin (change in production)
        responseContext.getHeaders().add(
            "Access-Control-Allow-Origin", "http://localhost:3000");
        
        // Allow credentials
        responseContext.getHeaders().add(
            "Access-Control-Allow-Credentials", "true");
        
        // Allow methods
        responseContext.getHeaders().add(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS");
        
        // Allow headers
        responseContext.getHeaders().add(
            "Access-Control-Allow-Headers",
            "Content-Type, Authorization, X-Requested-With");
        
        // Max age for preflight
        responseContext.getHeaders().add(
            "Access-Control-Max-Age", "3600");
    }
}
```

---

## ✅ Verification Checklist

After completing all parts, verify:

- [ ] Users can register with hashed passwords
- [ ] Users can login and receive JWT token
- [ ] JWT token is validated on protected endpoints
- [ ] Role-based access control works correctly
- [ ] Customers can only access their own accounts
- [ ] Admin can access all resources
- [ ] Failed login attempts are tracked
- [ ] Accounts are locked after 5 failed attempts
- [ ] Security events are logged in database
- [ ] Security headers are present in responses
- [ ] CORS is configured correctly
- [ ] Password hashing uses PBKDF2 with proper parameters

---

## 🧪 Testing

### Manual Testing

1. **Register a new user:**
```bash
curl -X POST http://localhost:9080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'
```

2. **Login:**
```bash
curl -X POST http://localhost:9080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
```

3. **Access protected endpoint:**
```bash
curl http://localhost:9080/api/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Automated Testing

Run the test scripts:

```bash
# Local testing
./test-lab.sh

# Container testing
./podman-test.sh
```

---

## 🎓 Learning Points

### Security Best Practices Applied

1. **Password Security**
   - PBKDF2 hashing with 310,000 iterations
   - SHA-512 algorithm
   - 64-byte salt
   - Never store plain-text passwords

2. **Authentication**
   - JWT for stateless authentication
   - Token expiration (1 hour)
   - Secure token storage
   - Account lockout after failed attempts

3. **Authorization**
   - Role-based access control
   - Principle of least privilege
   - Resource ownership validation
   - Declarative and programmatic security

4. **Audit Logging**
   - All security events logged
   - IP address tracking
   - User agent logging
   - Timestamp for all events

5. **Security Headers**
   - XSS protection
   - Clickjacking prevention
   - Content Security Policy
   - HTTPS enforcement

---

## 🚀 Next Steps

1. Add multi-factor authentication (MFA)
2. Implement password reset functionality
3. Add rate limiting per user
4. Implement session management
5. Add OAuth2 integration
6. Implement API key authentication
7. Add security monitoring dashboard

---

## 📚 Resources

- [Jakarta Security Specification](https://jakarta.ee/specifications/security/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Password Hashing](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

---

**Congratulations!** You've implemented a comprehensive security solution for a banking application! 🎉

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->