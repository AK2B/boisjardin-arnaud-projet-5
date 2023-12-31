package com.safetynet.alerts.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.PhoneAlertDTO;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/phoneAlert")
@Tag(name = "phoneAlert", description = "Phone Alert API")
public class PhoneAlertController {

	private static final Logger logger = LogManager.getLogger(PhoneAlertController.class);

	private AlertsService alertsService;

	public PhoneAlertController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get phone alert by fire station number")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = PhoneAlertDTO.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "FireDTO station not found")})
	public ResponseEntity<PhoneAlertDTO> getPhoneAlert(@RequestParam("firestation") String firestationNumberStr) throws Exception {
		
			if (firestationNumberStr == null || firestationNumberStr.isEmpty()) {
				logger.error("Le nuréro de station est nul ou vide.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			int firestationNumber;
			try {
				firestationNumber = Integer.parseInt(firestationNumberStr);
			} catch (NumberFormatException e) {
				logger.error("Le nuréro de station doit être un entier.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			PhoneAlertDTO phoneAlertDTO = alertsService.getPhoneAlert(firestationNumber);

			if (phoneAlertDTO == null) {
				logger.error("La station n'a pas été trouvé : {}", firestationNumber);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			return ResponseEntity.ok(phoneAlertDTO);
		
	}
}
