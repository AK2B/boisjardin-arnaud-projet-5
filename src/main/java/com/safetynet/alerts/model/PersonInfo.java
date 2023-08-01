package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de personnes avec leur âge et leur médications.
 */
@Data
public class PersonInfo {
    private InfoPerson infoPerson;
    private int age;
    private List<String> medications;
    private List<String> allergies;

    public PersonInfo(InfoPerson infoPerson, int age, List<String> medications, List<String> allergies) {
        this.infoPerson = infoPerson;
        this.age = age;
        this.medications = medications;
        this.allergies = allergies;
    }

}