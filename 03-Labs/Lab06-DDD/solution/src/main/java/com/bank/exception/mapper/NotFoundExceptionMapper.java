package com.bank.exception.mapper;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.dto.ErrorResponse;
import com.bank.exception.NotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps NotFoundException to HTTP 404 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 06
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Not Found",
            exception.getMessage()
        );
        
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity(error)
            .build();
    }
}

// Made with Bob
