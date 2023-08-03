package com.safetynet.alerts.model;

import lombok.Data;
/**
 * Classe représentant une liste de personne, composition de coverage.
 */
@Data
public class PersonCoverageDTO {
	
    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    public PersonCoverageDTO(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    
}
