package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;
import com.bank.exception.ValidationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps ValidationException to HTTP 400 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    
    @Override
    public Response toResponse(ValidationException exception) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Validation Failed",
            exception.getMessage(),
            exception.getErrors()
        );
        
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }
}

// Made with Bob
