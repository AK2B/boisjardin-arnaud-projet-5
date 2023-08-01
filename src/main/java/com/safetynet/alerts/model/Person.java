package com.safetynet.alerts.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Représentation d'une personne")
@Data
public class Person {
	
	private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
    
    public Person(String firstName, String lastName, String address, String city, String zip, String phone, String email) {
    	this.id = lastName.toLowerCase() + "-" + firstName.toLowerCase();
    	this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

}



