package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes habitant à une adresse et associés
 * à une station de pompier.
 */
@Data
public class Fire {
	private List<PersonFire> personFires;
	private int fireStationNumber;

	public Fire(List<PersonFire> personFires, int fireStationNumber) {
		super();
		this.personFires = personFires;
		this.fireStationNumber = fireStationNumber;
	}

}