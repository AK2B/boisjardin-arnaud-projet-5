package com.safetynet.alerts.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.Flood;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/flood/stations")
@Tag(name = "flood", description = "Flood API")
public class FloodController {

	private static final Logger logger = LogManager.getLogger(FloodController.class);

	private AlertsService alertsService;

	@Autowired
	public FloodController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get flood stations")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = Flood.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Flood stations not found")})
	public ResponseEntity<List<Flood>> getFloodStations(@RequestParam("stations") String stationNumber)
			throws Exception {
		
			if (stationNumber == null || stationNumber.isEmpty()) {
				logger.error("La station est nul ou vide.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			int fireStation;
			try {
				fireStation = Integer.parseInt(stationNumber);
			} catch (NumberFormatException e) {
				logger.error("La station doit être un integer.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			List<Flood> flood = alertsService.getFloodStations(fireStation);

			if (flood == null) {
				logger.error("La station n'existe pas.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			logger.info("La méthode getFloodStations a été exécutée avec succès.");
			return new ResponseEntity<>(flood, HttpStatus.OK);

	}
}
