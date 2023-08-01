package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste d'enfants et d'adultes couvert par une station
 * de pompier.
 */
@Data
public class FireStationCoverage {

	private String address;
	private int numAdults;
	private int numChildren;
	private List<PersonCoverage> persons;

	public FireStationCoverage(String address, int numAdults, int numChildren, List<PersonCoverage> persons) {
		super();
		this.address = address;
		this.numAdults = numAdults;
		this.numChildren = numChildren;
		this.persons = persons;
	}

}
