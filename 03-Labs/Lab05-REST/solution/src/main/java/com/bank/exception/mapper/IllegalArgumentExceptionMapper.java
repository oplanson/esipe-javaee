package com.bank.exception.mapper;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.dto.ErrorResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps IllegalArgumentException to HTTP 400 response.
 *
 * Caller-input errors (insufficient funds, invalid amounts, etc.) are
 * reported as Bad Request rather than falling through to a 500.
 *
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            exception.getMessage()
        );

        return Response
            .status(400)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}

// Made with Bob
