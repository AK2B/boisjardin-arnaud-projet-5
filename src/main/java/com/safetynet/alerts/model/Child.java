package com.safetynet.alerts.model;

import lombok.Data;

/**
 * Classe représentant un enfant.
 */
@Data
public class Child {
    private String firstName;
    private String lastName;
    private int age;

    public Child(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

}
