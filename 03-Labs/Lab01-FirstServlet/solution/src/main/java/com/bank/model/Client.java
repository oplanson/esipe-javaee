/*
 * © Copyright Olivier Planson - 2025
 */
package com.bank.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Client entity representing a bank customer.
 * This is a simple POJO (Plain Old Java Object) for Lab 01.
 * In later labs, this will become a JPA entity.
 *
 * @author Olivier Planson
 * @version 1.0
 */
public class Client implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    
    /**
     * Default constructor required for JavaBeans specification
     */
    public Client() {
    }
    
    /**
     * Constructor with all fields
     * 
     * @param id    Client unique identifier
     * @param name  Client full name
     * @param email Client email address
     * @param phone Client phone number
     */
    public Client(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    // Object methods
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

