package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes habitant à une adresse et associés
 * à une station de pompier.
 */
@Data
public class FireDTO {
	private List<PersonFireDTO> personFireDTOs;
	private int fireStationNumber;

	public FireDTO(List<PersonFireDTO> personFireDTOs, int fireStationNumber) {
		super();
		this.personFireDTOs = personFireDTOs;
		this.fireStationNumber = fireStationNumber;
	}

}