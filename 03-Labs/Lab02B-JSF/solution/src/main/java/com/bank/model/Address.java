// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.model;

import java.io.Serializable;

/**
 * Address entity for Lab 02B - JSF Client Management
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String street;
    private String city;
    private String postalCode;
    private String country;
    
    // Constructors
    public Address() {
    }
    
    public Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    // Getters and Setters
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    @Override
    public String toString() {
        return street + ", " + postalCode + " " + city + ", " + country;
    }
}

// Made with Bob
