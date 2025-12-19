package com.bank.model;

/**
 * Account entity representing a bank account.
 * Complete implementation with all required methods.
 */
public class Account {
    
    private Long id;
    private String number;
    private double balance;
    private String type; // CHECKING or SAVINGS
    private Long clientId;
    
    /**
     * Default constructor.
     */
    public Account() {
    }
    
    /**
     * Constructor with all parameters except ID.
     * 
     * @param number The account number
     * @param balance The account balance
     * @param type The account type (CHECKING or SAVINGS)
     * @param clientId The client ID this account belongs to
     */
    public Account(String number, double balance, String type, Long clientId) {
        this.number = number;
        this.balance = balance;
        this.type = type;
        this.clientId = clientId;
    }
    
    /**
     * Full constructor with all parameters.
     * 
     * @param id The account ID
     * @param number The account number
     * @param balance The account balance
     * @param type The account type
     * @param clientId The client ID
     */
    public Account(Long id, String number, double balance, String type, Long clientId) {
        this(number, balance, type, clientId);
        this.id = id;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String number) {
        this.number = number;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    /**
     * Deposit money into the account.
     * 
     * @param amount The amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
    
    /**
     * Withdraw money from the account.
     * 
     * @param amount The amount to withdraw
     * @return true if successful, false if insufficient funds
     */
    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", balance=" + balance +
                ", type='" + type + '\'' +
                ", clientId=" + clientId +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

// Made with Bob