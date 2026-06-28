// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.security;

import com.bank.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for JWT token generation and validation
 */
@ApplicationScoped
public class JwtService {
    
    @Inject
    @ConfigProperty(name = "jwt.secret", defaultValue = "MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForHS256Algorithm")
    private String jwtSecret;
    
    @Inject
    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    private int expirationHours;
    
    @Inject
    @ConfigProperty(name = "jwt.issuer", defaultValue = "bank-security-app")
    private String issuer;
    
    private SecretKey secretKey;
    
    @PostConstruct
    public void init() {
        // Ensure the secret is at least 256 bits (32 bytes) for HS256
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes) long");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Generate JWT token for a user
     * 
     * @param username Username
     * @param roles User roles
     * @return JWT token string
     */
    public String generateToken(String username, Set<Role> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationHours, ChronoUnit.HOURS);
        
        // Convert roles to comma-separated string
        String rolesString = roles.stream()
                .map(Role::name)
                .collect(Collectors.joining(","));
        
        return Jwts.builder()
                .subject(username)
                .claim("roles", rolesString)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
    
    /**
     * Parse and validate JWT token
     * 
     * @param token JWT token string
     * @return Claims if valid
     * @throws JwtException if token is invalid
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Extract username from token
     * 
     * @param token JWT token string
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Extract roles from token
     * 
     * @param token JWT token string
     * @return Set of roles
     */
    public Set<Role> getRolesFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            String rolesString = claims.get("roles", String.class);
            
            if (rolesString == null || rolesString.isEmpty()) {
                return Set.of();
            }
            
            return java.util.Arrays.stream(rolesString.split(","))
                    .map(String::trim)
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
                    
        } catch (Exception e) {
            return Set.of();
        }
    }
    
    /**
     * Validate token
     * 
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // Token expired
            return false;
        } catch (JwtException e) {
            // Invalid token
            return false;
        }
    }
    
    /**
     * Check if token is expired
     * 
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return true;
        }
    }
    
    /**
     * Get token expiration date
     * 
     * @param token JWT token string
     * @return Expiration date or null if invalid
     */
    public Date getExpirationDate(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration();
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Extract token from Authorization header
     * 
     * @param authorizationHeader Authorization header value
     * @return Token string or null
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}

// Made with Bob
