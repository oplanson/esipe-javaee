// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.security;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Service for secure password hashing and verification using PBKDF2
 */
@ApplicationScoped
public class PasswordService {
    
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int ITERATIONS = 310000; // OWASP recommendation for PBKDF2-SHA512
    private static final int KEY_LENGTH = 512; // 512 bits = 64 bytes
    private static final int SALT_LENGTH = 64; // 64 bytes
    
    private final SecureRandom random = new SecureRandom();
    
    /**
     * Hash a password using PBKDF2
     * 
     * @param password Plain text password
     * @return Base64 encoded hash in format: salt:hash
     */
    public String hashPassword(String password) {
        try {
            // Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password
            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            
            // Encode salt and hash as Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            // Return in format: salt:hash
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * 
     * @param password Plain text password to verify
     * @param storedHash Stored hash in format: salt:hash
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            // Decode salt and hash from Base64
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the provided password with the same salt
            byte[] actualHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            
            // Compare hashes using constant-time comparison
            return constantTimeEquals(expectedHash, actualHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * PBKDF2 key derivation function
     */
    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }
    
    /**
     * Constant-time comparison to prevent timing attacks
     */
    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        
        return result == 0;
    }
    
    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return true if password meets strength requirements
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Get password strength requirements message
     */
    public String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain: " +
               "uppercase letter, lowercase letter, digit, and special character";
    }
}

// Made with Bob
