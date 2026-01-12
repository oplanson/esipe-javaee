// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import java.util.regex.Pattern;

/**
 * Custom email validator for Lab 02B - JSF Client Management
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator<String> {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    @Override
    public void validate(FacesContext context, UIComponent component, String value) 
            throws ValidatorException {
        
        // Return if value is null or empty (use required="true" for that)
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        
        // Check if value matches EMAIL_PATTERN
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Invalid Email",
                "Please enter a valid email address (e.g., user@example.com)"
            );
            throw new ValidatorException(message);
        }
    }
}

// Made with Bob
