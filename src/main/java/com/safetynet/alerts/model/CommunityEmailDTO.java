package com.safetynet.alerts.model;

import java.util.List;

import lombok.Data;

/**
 * Classe représentant une liste d'email.
 */
@Data
public class CommunityEmailDTO {

	private List<String> emails;

}