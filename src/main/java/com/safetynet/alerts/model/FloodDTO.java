package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes par adresse.
 */
@Data
public class FloodDTO {
	
    private String address;
    private List<PersonFloodDTO> persons;

    public FloodDTO(String address, List<PersonFloodDTO> persons) {
        this.address = address;
        this.persons = persons;
    }

}

