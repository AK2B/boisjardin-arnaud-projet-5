package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes par adresse.
 */
@Data
public class Flood {
	
    private String address;
    private List<PersonFlood> persons;

    public Flood(String address, List<PersonFlood> persons) {
        this.address = address;
        this.persons = persons;
    }

}

