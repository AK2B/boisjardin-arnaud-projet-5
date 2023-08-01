package com.safetynet.alerts.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;

import jakarta.annotation.PostConstruct;

/**
 * Classe DataConfig, classe de configuration pour charger et gérer les données
 * à partir d'un JSON, informations sur les personnes, les casernes de pompiers
 * et les dossiers médicaux.
 */
@Repository
public class DataConfig {
	private static final Logger logger = LogManager.getLogger(DataConfig.class);

	private final ResourceLoader resourceLoader;
	private final ObjectMapper objectMapper;
	private List<Person> persons;
	private List<FireStation> fireStations;
	private List<MedicalRecord> medicalRecords;

	/**
	 * Constructeur DataConfig.
	 *
	 * @param resourceLoader ResourceLoader, pour charger le fichier de données
	 *                       JSON.
	 * @param objectMapper   ObjectMapper, pour désérialiser les données JSON.
	 */
	@Autowired
	public DataConfig(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
		this.resourceLoader = resourceLoader;
		this.objectMapper = objectMapper;
	}

	/**
	 * Cette méthode charge les données à partir du JSON, extrait les informations.
	 * Elle initialise les listes correspondantes et les remplit avec les données
	 * extraites. Si une IOException se produit lors de la récupération des données,
	 * elle sera consignée en tant qu'erreur.
	 */
	@PostConstruct
	public void loadData() {
		try {
			Resource resource = resourceLoader.getResource("classpath:data.json");
			InputStream inputStream = resource.getInputStream();
			JsonNode rootNode = objectMapper.readTree(inputStream);

			extractPersons(rootNode);
			extractFireStations(rootNode);
			extractMedicalRecords(rootNode);

			logger.info("Données récupérées avec succès.");
		} catch (IOException e) {
			logger.error("Erreur lors de la récupération des données.", e);
		}
	}

	/**
	 * Extrait les personnes à partir du nœud JSON spécifié.
	 *
	 * @param rootNode Le nœud JSON racine contenant les personnes.
	 */
	private void extractPersons(JsonNode rootNode) {
		persons = new ArrayList<>();
		JsonNode personsNode = rootNode.get("persons");

		if (personsNode != null && personsNode.isArray()) {
			for (JsonNode personNode : personsNode) {
				String firstName = personNode.get("firstName").asText();
				String lastName = personNode.get("lastName").asText();
				String address = personNode.get("address").asText();
				String city = personNode.get("city").asText();
				String zip = personNode.get("zip").asText();
				String phone = personNode.get("phone").asText();
				String email = personNode.get("email").asText();

				Person person = new Person(firstName, lastName, address, city, zip, phone, email);
				persons.add(person);
			}
		}
	}

	/**
	 * Extrait les casernes de pompiers à partir du nœud JSON spécifié.
	 *
	 * @param rootNode Le nœud JSON racine contenant les casernes de pompiers.
	 */
	private void extractFireStations(JsonNode rootNode) {
		fireStations = new ArrayList<>();
		JsonNode fireStationsNode = rootNode.get("firestations");

		if (fireStationsNode != null && fireStationsNode.isArray()) {
			for (JsonNode fireStationNode : fireStationsNode) {
				String address = fireStationNode.get("address").asText();
				int station = fireStationNode.get("station").asInt();

				FireStation fireStation = new FireStation(address, station);
				fireStations.add(fireStation);
			}
		}
	}

	/**
	 * Extrait les dossiers médicaux à partir du nœud JSON spécifié.
	 *
	 * @param rootNode Le nœud JSON racine contenant les dossiers médicaux.
	 */
	private void extractMedicalRecords(JsonNode rootNode) {
		medicalRecords = new ArrayList<>();
		JsonNode medicalRecordsNode = rootNode.get("medicalrecords");

		if (medicalRecordsNode != null && medicalRecordsNode.isArray()) {
			for (JsonNode medicalRecordNode : medicalRecordsNode) {
				String firstName = medicalRecordNode.get("firstName").asText();
				String lastName = medicalRecordNode.get("lastName").asText();
				String birthdate = medicalRecordNode.get("birthdate").asText();

				List<String> medications = extractMedications(medicalRecordNode);
				List<String> allergies = extractAllergies(medicalRecordNode);

				MedicalRecord medicalRecord = new MedicalRecord(firstName, lastName, birthdate, medications, allergies);
				medicalRecords.add(medicalRecord);
			}
		}
	}

	/**
	 * Extrait la liste des médicaments à partir du nœud JSON spécifié.
	 *
	 * @param medicalRecordNode Le nœud JSON contenant le dossier médical.
	 * @return Liste des médicaments.
	 */
	private List<String> extractMedications(JsonNode medicalRecordNode) {
		List<String> medications = new ArrayList<>();
		JsonNode medicationsNode = medicalRecordNode.get("medications");

		if (medicationsNode != null && medicationsNode.isArray()) {
			for (JsonNode medicationNode : medicationsNode) {
				medications.add(medicationNode.asText());
			}
		}

		return medications;
	}

	/**
	 * Extrait la liste des allergies à partir du nœud JSON spécifié.
	 *
	 * @param medicalRecordNode Le nœud JSON contenant le dossier médical.
	 * @return Liste des allergies.
	 */
	private List<String> extractAllergies(JsonNode medicalRecordNode) {
		List<String> allergies = new ArrayList<>();
		JsonNode allergiesNode = medicalRecordNode.get("allergies");

		if (allergiesNode != null && allergiesNode.isArray()) {
			for (JsonNode allergyNode : allergiesNode) {
				allergies.add(allergyNode.asText());
			}
		}

		return allergies;
	}

	/**
	 * Liste des personnes.
	 *
	 * @return Liste d'objets Person.
	 */
	public List<Person> getPersons() {
		return persons;
	}

	/**
	 * Liste des casernes de pompiers.
	 *
	 * @return Liste d'objets FireStation.
	 */
	public List<FireStation> getFireStations() {
		return fireStations;
	}

	/**
	 * Liste des dossiers médicaux.
	 *
	 * @return Liste d'objets MedicalRecord.
	 */
	public List<MedicalRecord> getMedicalRecords() {
		return medicalRecords;
	}
}
