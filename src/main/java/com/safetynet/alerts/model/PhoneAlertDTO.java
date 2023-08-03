package com.safetynet.alerts.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste de numéro de téléphone.
 */
@Data
public class PhoneAlertDTO {
	private List<String> phoneNumbers;

	public PhoneAlertDTO() {
		this.phoneNumbers = new ArrayList<>();
	}

}
