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

import com.safetynet.alerts.model.PersonInfoDTO;
import com.safetynet.alerts.service.AlertsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/personInfo")
@Tag(name = "personInfo" , description = "Person Info API")
public class PersonInfoController {
	
	private static final Logger logger = LogManager.getLogger(PersonInfoController.class);

    private AlertsService alertsService;

    public PersonInfoController(AlertsService alertsService) {
        this.alertsService = alertsService;
    }

    @GetMapping
    @Operation(summary = "Get person info")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = PersonInfoDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoDTO(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            logger.error("Paramètres invalides ou vides. firstName: {}, lastName: {}", firstName, lastName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
       
            List<PersonInfoDTO> personInfoList = alertsService.getPersonInfoDTO(firstName, lastName);
            if (personInfoList.isEmpty()) {
                logger.error("La personne n'existe pas avec le firstName: {} et lastName: {}", firstName, lastName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(personInfoList);
       
    }
}