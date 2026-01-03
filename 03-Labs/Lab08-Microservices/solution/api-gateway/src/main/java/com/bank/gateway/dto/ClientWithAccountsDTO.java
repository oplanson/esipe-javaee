// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated DTO combining Client information with their Accounts
 * This demonstrates the BFF pattern - aggregating data from multiple microservices
 */
public class ClientWithAccountsDTO {
    
    private ClientDTO client;
    private List<AccountDTO> accounts;
    private BigDecimal totalBalance;
    private int accountCount;
    
    // Constructors
    public ClientWithAccountsDTO() {
        this.accounts = new ArrayList<>();
        this.totalBalance = BigDecimal.ZERO;
        this.accountCount = 0;
    }
    
    public ClientWithAccountsDTO(ClientDTO client, List<AccountDTO> accounts) {
        this.client = client;
        this.accounts = accounts != null ? accounts : new ArrayList<>();
        calculateTotals();
    }
    
    // Calculate aggregated values
    private void calculateTotals() {
        this.accountCount = accounts.size();
        this.totalBalance = accounts.stream()
            .map(AccountDTO::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters and Setters
    public ClientDTO getClient() {
        return client;
    }
    
    public void setClient(ClientDTO client) {
        this.client = client;
    }
    
    public List<AccountDTO> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
        calculateTotals();
    }
    
    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
    
    public int getAccountCount() {
        return accountCount;
    }
    
    public boolean hasAccounts() {
        return accountCount > 0;
    }
}

// Made with Bob
