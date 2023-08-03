package com.safetynet.alerts.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.FloodDTO;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/flood/stations")
@Tag(name = "flood", description = "FloodDTO API")
public class FloodController {

	private static final Logger logger = LogManager.getLogger(FloodController.class);

	private AlertsService alertsService;

	public FloodController(AlertsService alertsService) {
		this.alertsService = alertsService;
	}

	@GetMapping
	@Operation(summary = "Get flood stations")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = FloodDTO.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "FloodDTO stations not found")})
	public ResponseEntity<List<FloodDTO>> getFloodStations(@RequestParam("stations") String stationNumber)
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
			List<FloodDTO> floodDTO = alertsService.getFloodStations(fireStation);

			if (floodDTO == null) {
				logger.error("La station n'existe pas.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(floodDTO, HttpStatus.OK);

	}
}
