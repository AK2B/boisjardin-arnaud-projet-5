package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste d'enfants et d'adultes couvert par une station
 * de pompier.
 */
@Data
public class FireStationCoverageDTO {

	private String address;
	private int numAdults;
	private int numChildren;
	private List<PersonCoverageDTO> persons;

	public FireStationCoverageDTO(String address, int numAdults, int numChildren, List<PersonCoverageDTO> persons) {
		super();
		this.address = address;
		this.numAdults = numAdults;
		this.numChildren = numChildren;
		this.persons = persons;
	}

}
