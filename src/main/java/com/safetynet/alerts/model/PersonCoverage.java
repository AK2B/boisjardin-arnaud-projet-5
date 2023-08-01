package com.safetynet.alerts.model;

import lombok.Data;
/**
 * Classe représentant une liste de personne, composition de coverage.
 */
@Data
public class PersonCoverage {
	
    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    public PersonCoverage(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    
}
