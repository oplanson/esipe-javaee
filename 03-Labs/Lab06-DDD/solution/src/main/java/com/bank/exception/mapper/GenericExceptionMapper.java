package com.bank.exception.mapper;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.dto.ErrorResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps all uncaught exceptions to HTTP 500 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 06
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    
    @Inject
    private Logger logger;
    
    @Override
    public Response toResponse(Exception exception) {
        logger.severe("Unexpected error: " + exception.getMessage());
        exception.printStackTrace();
        
        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support."
        );
        
        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .build();
    }
}

// Made with Bob
