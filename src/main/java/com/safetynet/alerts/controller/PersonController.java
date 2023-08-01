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

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/person")
@Tag(name = "person", description = "Person API")
public class PersonController {

	private static final Logger logger = LogManager.getLogger(PersonController.class);

	private PersonService personService;

	@Autowired
	public PersonController(PersonService personService) {
		this.personService = personService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a person by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = Person.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "Person not found") })
	public ResponseEntity<Person> getPersonById(@PathVariable String id) {

			Person person = personService.getPersonById(id);
			if (person != null) {
				logger.info("La méthode getPersonById a été exécutée avec succès ID: {}.", id);
				return ResponseEntity.ok(person);
			} else {
				logger.error("L'id est introuvable ID: {}.", id);
				return ResponseEntity.notFound().build();
			}
	}

	@PostMapping
	@Operation(summary = "Add a new person")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Person created") })
	public ResponseEntity<String> addPerson(@RequestBody Person person) {
		personService.addPerson(person);
		logger.info("La personne a été ajouté avec succès : {}.", person);
		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a person")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Person updated"),
			@ApiResponse(responseCode = "404", description = "Person not found") })
	public ResponseEntity<String> updatePerson(@PathVariable String id, @RequestBody Person person) {

		Person existingPerson = personService.getPersonById(id);
		if (existingPerson != null) {
			person.setId(id);
			personService.updatePerson(person);
			logger.info("La mise à jour a été faite ID: {}.", id);
			return ResponseEntity.ok().build();
		} else {
			logger.error("L'id est introuvable pour la mise à jour ID: {}.", id);
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/{firstName}/{lastName}")
	@Operation(summary = "Delete a person")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Person deleted") })
	public ResponseEntity<String> deletePerson(@PathVariable String firstName, @PathVariable String lastName) {

		personService.deletePerson(firstName, lastName);
		logger.info("La personne a été supprimé : {} {}.", firstName, lastName);
		return ResponseEntity.ok().build();

	}

}