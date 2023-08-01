package com.safetynet.alerts.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de numéro de téléphone.
 */
@Data
public class PhoneAlert {
	private List<String> phoneNumbers;

	public PhoneAlert() {
		this.phoneNumbers = new ArrayList<>();
	}

}
