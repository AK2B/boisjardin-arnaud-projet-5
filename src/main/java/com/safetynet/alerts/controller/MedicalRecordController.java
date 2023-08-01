package com.safetynet.alerts.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/medicalrecord")
@Tag(name = "medicalRecord", description = "Medical Record API")
public class MedicalRecordController {

	private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);

	private MedicalRecordService medicalRecordService;

	@Autowired
	public MedicalRecordController(MedicalRecordService medicalRecordService) {
		this.medicalRecordService = medicalRecordService;
	}

	@GetMapping("/{firstName}/{lastName}")
	@Operation(summary = "Get medical record by full name")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = MedicalRecord.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "Medical record not found") })
	public ResponseEntity<MedicalRecord> getMedicalRecordByFullName(@PathVariable String firstName,
			@PathVariable String lastName) {
		
			MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFullName(firstName, lastName);
			if (medicalRecord != null) {
				logger.info("La méthode getMedicalRecordByFullName a été exécutée avec succès.");
				return ResponseEntity.ok(medicalRecord);
			} else {
				logger.error("Medical record n'est pas trouvé avec le nom et prénom : {} {}", firstName, lastName);
				return ResponseEntity.notFound().build();
			}
		
	}

	@PostMapping
	@Operation(summary = "Add a new medical record")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Medical record created") })
	public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {

		medicalRecordService.addMedicalRecord(medicalRecord);
		logger.info("Medical record créé avec le nom et prénom: {} {} ", medicalRecord.getFirstName(),
				medicalRecord.getLastName());
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@PutMapping("/{firstName}/{lastName}")
	@Operation(summary = "Update a medical record")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Medical record updated"),
			@ApiResponse(responseCode = "404", description = "Medical record not found") })
	public ResponseEntity<String> updateMedicalRecord(@PathVariable String firstName, @PathVariable String lastName,
			@RequestBody MedicalRecord medicalRecord) {

		MedicalRecord existingMedicalRecord = medicalRecordService.getMedicalRecordByFullName(firstName, lastName);
		if (existingMedicalRecord != null) {
			medicalRecord.setFirstName(firstName);
			medicalRecord.setLastName(lastName);
			medicalRecordService.updateMedicalRecord(medicalRecord);
			logger.info("Medical record mis à jour : {} {}", firstName, lastName);
			return ResponseEntity.ok().build();
		} else {
			logger.error("Medical record n'est pas trouvé : {} {}", firstName, lastName);
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/{firstName}/{lastName}")
	@Operation(summary = "Delete a medical record")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Medical record deleted") })
	public ResponseEntity<String> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) {

		medicalRecordService.deleteMedicalRecord(firstName, lastName);
		logger.info("Medical record supprimé : {} {}", firstName, lastName);
		return ResponseEntity.ok().build();

	}
}
