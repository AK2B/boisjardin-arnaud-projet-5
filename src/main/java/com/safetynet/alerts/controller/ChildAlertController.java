package com.safetynet.alerts.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.ChildAlertDTO;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/childAlert")
@Tag(name = "childAlert", description = "ChildDTO Alert API")
public class ChildAlertController {

	private static final Logger logger = LogManager.getLogger(ChildAlertController.class);
	
	private AlertsService alertsService;
	
	public ChildAlertController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get child alert by address")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = ChildAlertDTO.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Address not found")})
	public ResponseEntity<ChildAlertDTO> getChildAlert(@RequestParam("address") String address) throws Exception {
		
			if (address == null || address.isEmpty()) {
				logger.error("L'adresse est nul ou vide.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			ChildAlertDTO childAlertDTO = alertsService.getChildAlert(address);

			if (childAlertDTO == null) {
				logger.error("L'adresse est introuvable.", address);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(childAlertDTO, HttpStatus.OK);
		
	}
}
