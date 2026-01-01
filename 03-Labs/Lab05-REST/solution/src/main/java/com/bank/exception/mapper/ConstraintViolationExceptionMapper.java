package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps ConstraintViolationException to HTTP 400 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class ConstraintViolationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            400,
            "Validation Failed",
            "Input validation failed",
            errors
        );
        
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }
}

// Made with Bob
