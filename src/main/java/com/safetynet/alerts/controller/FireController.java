package com.safetynet.alerts.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.Fire;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/fire")
@Tag(name = "fire", description = "Fire API")
public class FireController {

	private static final Logger logger = LogManager.getLogger(FireController.class);

	private AlertsService alertsService;

	@Autowired
	public FireController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get fire details by address")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = Fire.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Address not found")})
	public ResponseEntity<Fire> getFireDetails(@RequestParam("address") String address) throws Exception {
		
			if (address == null || address.isEmpty()) {
				logger.error("L'adresse est nul ou vide.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			Fire fireDetails = alertsService.getFireInformation(address);
			if (fireDetails == null) {
				logger.error("L'adresse est introuvable.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			logger.info("La méthode getFireDetails a été exécutée avec succès.");
			return new ResponseEntity<>(fireDetails, HttpStatus.OK);
		
	}
}
