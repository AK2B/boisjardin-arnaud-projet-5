package com.safetynet.alerts.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Représentation d'une station de pompier")
@Data
public class FireStation {

	private String address;
	private int station;

	public FireStation(String address, int station) {
		this.address = address;
		this.station = station;
	}

}
