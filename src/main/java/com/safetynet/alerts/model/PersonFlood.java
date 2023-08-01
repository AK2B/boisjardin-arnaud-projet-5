package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes avec leur informations,
 * composition pour l'inondation.
 */
@Data
public class PersonFlood {

	private String firstName;
	private String lastName;
	private String phone;
	private int age;
	private List<String> medications;
	private List<String> allergies;

	public PersonFlood(String firstName, String lastName, String phone, int age, List<String> medications,
			List<String> allergies) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.age = age;
		this.medications = medications;
		this.allergies = allergies;
	}

}