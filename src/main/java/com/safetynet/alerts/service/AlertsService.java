package com.safetynet.alerts.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Child;
import com.safetynet.alerts.model.ChildAlert;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.FireStationCoverage;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.PersonCoverage;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;


@Service
public class AlertsService {
	private static final Logger logger = LogManager.getLogger(AlertsService.class);

	private PersonRepository personRepository;
	private FireStationRepository fireStationRepository;
	private MedicalRecordRepository medicalRecordRepository;

	public AlertsService(PersonRepository personRepository, FireStationRepository fireStationRepository,
			MedicalRecordRepository medicalRecordRepository) {
		this.personRepository = personRepository;
		this.fireStationRepository = fireStationRepository;
		this.medicalRecordRepository = medicalRecordRepository;
	}

	/**
	 * Récupère les informations des personnes couvertes par la caserne de pompiers
	 * correspondante.
	 *
	 * @param fireStationNumber Le numéro de la caserne de pompiers
	 * @return Liste des personnes couvertes par la caserne de pompiers avec leurs
	 *         informations
	 * @throws Exception
	 */
	public List<FireStationCoverage> getFireStationCoverage(Integer fireStationNumber) throws Exception {

		List<FireStationCoverage> fireStationCoverages = new ArrayList<>();
		int[] totalNumAdults = { 0 }; // Using an array container for the sum of adults
		int[] totalNumChildren = { 0 }; // Using an array container for the sum of children

		try {
			List<String> fireStationAddresses = fireStationRepository.getAllFireStations().stream().filter(
					fireStation -> String.valueOf(fireStation.getStation()).equals(String.valueOf(fireStationNumber)))
					.map(FireStation::getAddress).collect(Collectors.toList());

			List<Person> persons = personRepository.getAllPersons();

			fireStationAddresses.forEach(address -> {
				List<Person> personsCovered = persons.stream().filter(person -> person.getAddress().equals(address))
						.collect(Collectors.toList());

				int numAdults = (int) personsCovered.stream()
						.map(person -> calculateAge(medicalRecordRepository
								.getMedicalRecordByFullName(person.getFirstName(), person.getLastName())
								.getBirthdate()))
						.filter(age -> age >= 18).count();

				int numChildren = personsCovered.size() - numAdults; // Calculate number of children

				totalNumAdults[0] += numAdults; // Adding the number of adults to the total sum
				totalNumChildren[0] += numChildren; // Adding the number of children to the total sum

				List<PersonCoverage> personCoverages = personsCovered.stream()
						.map(person -> new PersonCoverage(person.getFirstName(), person.getLastName(),
								person.getAddress(), person.getPhone()))
						.collect(Collectors.toList());

				FireStationCoverage fireStationCoverage = new FireStationCoverage(address, numAdults, numChildren,
						personCoverages);
				fireStationCoverage.setAddress(address);
				fireStationCoverage.setNumAdults(numAdults);
				fireStationCoverage.setNumChildren(numChildren);
				fireStationCoverage.setPersons(personCoverages);

				fireStationCoverages.add(fireStationCoverage);
			});

			if (totalNumAdults[0] == 0 || totalNumChildren[0] == 0) {
			    return null;
			}

			// Adding an additional FireStationCoverage with the total sum for all addresses
			fireStationCoverages.add(new FireStationCoverage("Zone désservie", totalNumAdults[0], totalNumChildren[0],
					new ArrayList<>()));

			// Logging successful responses at Info level
			logger.info("La méthode getFireStationCoverage a été exécutée avec succès.");

		} catch (Exception e) {
			logger.error("Une erreur s'est produite lors de l'exécution de la méthode getFireStationCoverage.", e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");
		}

		return fireStationCoverages;
	}
	
	/**
	 * Renvoie une liste d'enfants habitant à une adresse donnée, avec les membres
	 * du foyer.
	 *
	 * @param address l'adresse pour laquelle récupérer les informations
	 * @return un objet ChildAlert contenant la liste des enfants et les membres du
	 *         foyer
	 * @throws Exception si une erreur se produit lors de la récupération des
	 *                   informations
	 */
	public ChildAlert getChildAlert(String address) throws Exception {
		List<Child> children = new ArrayList<>();
		List<Person> householdMembers = new ArrayList<>();
		boolean addressFound = false; // Boolean pour suivre si l'adresse a été trouvée

		try {
			List<Person> persons = personRepository.getAllPersons();

			for (Person person : persons) {
				if (person.getAddress().equals(address)) {
					addressFound = true; // L'adresse a été trouvée
					MedicalRecord medicalRecord = medicalRecordRepository
							.getMedicalRecordByFullName(person.getFirstName(), person.getLastName());
					int age = calculateAge(medicalRecord.getBirthdate());

					if (age <= 18) {
						Child child = new Child(person.getFirstName(), person.getLastName(), age);
						children.add(child);
					} else {
						householdMembers.add(person);
					}
				}
			}

			logger.info("La méthode getChildAlert a été exécutée avec succès.");
		} catch (Exception e) {
			
			logger.error("Une erreur s'est produite lors de l'exécution de la méthode getChildAlert.", e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");
		}

		// Si l'adresse n'a pas été trouvée, renvoyer null
		if (!addressFound) {
			return null;
		}

		return new ChildAlert(children, householdMembers);
	}
	
	/**
	 * Calcule l'âge à partir de la date de naissance.
	 *
	 * @param birthDate la date de naissance (au format "MM/dd/yyyy")
	 * @return l'âge calculé
	 */
	public int calculateAge(String birthDate) {
		logger.debug("Calcul de l'âge pour la date de naissance : " + birthDate);

		int age = 0;

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDate parsedDate = LocalDate.parse(birthDate, formatter);
			LocalDate currentDate = LocalDate.now();
			Period agePeriod = Period.between(parsedDate, currentDate);
			age = agePeriod.getYears();

			logger.debug("Âge calculé : " + age);
		} catch (Exception e) {
			logger.error("Erreur lors du calcul de l'âge pour la date de naissance : " + birthDate, e);
		}

		return age;
	}

}
