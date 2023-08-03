package com.safetynet.alerts.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.CommunityEmailDTO;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/communityEmail")
@Tag(name = "communityEmail", description = "Community Email API")
public class CommunityEmailController {

	private static final Logger logger = LogManager.getLogger(CommunityEmailController.class);

	private AlertsService alertsService;

	public CommunityEmailController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get community emails by city")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success", content = {
			@Content(schema = @Schema(implementation = CommunityEmailDTO.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "City not found") })
	public ResponseEntity<CommunityEmailDTO> getCommunityEmails(@RequestParam("city") String city) {
		
			if (city == null || city.isEmpty()) {
				logger.error("La ville est nul ou vide.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			CommunityEmailDTO communityEmailDTO = alertsService.getCommunityEmails(city);
			if (communityEmailDTO == null) {
				logger.error("La ville n'est pas trouvée.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(communityEmailDTO, HttpStatus.OK);

	}
}
