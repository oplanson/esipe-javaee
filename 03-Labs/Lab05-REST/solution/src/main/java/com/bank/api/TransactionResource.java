package com.bank.api;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Transaction;
import com.bank.service.TransactionService;
import com.bank.dto.ErrorResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST API for Transaction operations.
 * Provides endpoints to query transaction history.
 * 
 * Lab 05 - JAX-RS: Transaction Management
 */
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {
    
    @Inject
    private Logger logger;
    
    @Inject
    private TransactionService transactionService;
    
    /**
     * Get all transactions.
     * 
     * @return List of all transactions
     */
    @GET
    public Response getAllTransactions() {
        logger.info("REST: Getting all transactions");
        
        try {
            List<Transaction> transactions = transactionService.findAll();
            return Response.ok(transactions).build();
        } catch (Exception e) {
            logger.severe("Error getting transactions: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Internal Server Error", "Failed to retrieve transactions"))
                    .build();
        }
    }
    
    /**
     * Get a transaction by ID.
     * 
     * @param id The transaction ID
     * @return The transaction if found
     */
    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Long id) {
        logger.info("REST: Getting transaction: " + id);
        
        try {
            Transaction transaction = transactionService.findById(id);
            
            if (transaction == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, "Not Found", "Transaction not found: " + id))
                        .build();
            }
            
            return Response.ok(transaction).build();
        } catch (Exception e) {
            logger.severe("Error getting transaction: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Internal Server Error", "Failed to retrieve transaction"))
                    .build();
        }
    }
    
    /**
     * Get all transactions for a specific account.
     * 
     * @param accountId The account ID
     * @param limit Optional limit for number of transactions (default: all)
     * @return List of transactions for the account
     */
    @GET
    @Path("/account/{accountId}")
    public Response getTransactionsByAccount(
            @PathParam("accountId") Long accountId,
            @QueryParam("limit") @DefaultValue("0") int limit) {
        
        logger.info("REST: Getting transactions for account: " + accountId + 
                   (limit > 0 ? " (limit: " + limit + ")" : ""));
        
        try {
            List<Transaction> transactions;
            
            if (limit > 0) {
                transactions = transactionService.findRecentByAccount(accountId, limit);
            } else {
                transactions = transactionService.findByAccount(accountId);
            }
            
            return Response.ok(transactions).build();
        } catch (Exception e) {
            logger.severe("Error getting transactions for account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Internal Server Error", "Failed to retrieve transactions"))
                    .build();
        }
    }
    
    /**
     * Get transactions by type.
     * 
     * @param type The transaction type (DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN)
     * @return List of transactions of the specified type
     */
    @GET
    @Path("/type/{type}")
    public Response getTransactionsByType(@PathParam("type") String type) {
        logger.info("REST: Getting transactions by type: " + type);
        
        try {
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionService.findByType(transactionType);
            
            return Response.ok(transactions).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Invalid transaction type: " + type +
                           ". Valid types: DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN"))
                    .build();
        } catch (Exception e) {
            logger.severe("Error getting transactions by type: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Internal Server Error", "Failed to retrieve transactions"))
                    .build();
        }
    }
    
    /**
     * Get transactions within a date range.
     * 
     * @param startDate Start date in ISO format (yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate End date in ISO format (yyyy-MM-dd'T'HH:mm:ss)
     * @return List of transactions within the date range
     */
    @GET
    @Path("/date-range")
    public Response getTransactionsByDateRange(
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {
        
        logger.info("REST: Getting transactions between " + startDate + " and " + endDate);
        
        if (startDate == null || endDate == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Both start and end dates are required"))
                    .build();
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            if (start.isAfter(end)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(400, "Bad Request", "Start date must be before end date"))
                        .build();
            }
            
            List<Transaction> transactions = transactionService.findByDateRange(start, end);
            return Response.ok(transactions).build();
            
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Invalid date format. Use ISO format: yyyy-MM-dd'T'HH:mm:ss"))
                    .build();
        } catch (Exception e) {
            logger.severe("Error getting transactions by date range: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Internal Server Error", "Failed to retrieve transactions"))
                    .build();
        }
    }
    
    /**
     * Get transaction count.
     * 
     * @return Total number of transactions
     */
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTransactionCount() {
        logger.info("REST: Getting transaction count");
        
        try {
            long count = transactionService.count();
            return Response.ok(count).build();
        } catch (Exception e) {
            logger.severe("Error getting transaction count: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to count transactions")
                    .build();
        }
    }
}

// Made with Bob