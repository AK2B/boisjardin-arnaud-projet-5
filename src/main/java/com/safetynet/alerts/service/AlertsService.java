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

import com.safetynet.alerts.model.ChildDTO;
import com.safetynet.alerts.model.ChildAlertDTO;
import com.safetynet.alerts.model.CommunityEmailDTO;
import com.safetynet.alerts.model.FireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.FireStationCoverageDTO;
import com.safetynet.alerts.model.FloodDTO;
import com.safetynet.alerts.model.InfoPersonDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.PersonCoverageDTO;
import com.safetynet.alerts.model.PersonFireDTO;
import com.safetynet.alerts.model.PersonFloodDTO;
import com.safetynet.alerts.model.PersonInfoDTO;
import com.safetynet.alerts.model.PhoneAlertDTO;
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
	public List<FireStationCoverageDTO> getFireStationCoverage(Integer fireStationNumber) throws Exception {

		List<FireStationCoverageDTO> fireStationCoverageDTOs = new ArrayList<>();
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

				List<PersonCoverageDTO> personCoverageDTOs = personsCovered.stream()
						.map(person -> new PersonCoverageDTO(person.getFirstName(), person.getLastName(),
								person.getAddress(), person.getPhone()))
						.collect(Collectors.toList());

				FireStationCoverageDTO fireStationCoverageDTO = new FireStationCoverageDTO(address, numAdults, numChildren,
						personCoverageDTOs);
				fireStationCoverageDTO.setAddress(address);
				fireStationCoverageDTO.setNumAdults(numAdults);
				fireStationCoverageDTO.setNumChildren(numChildren);
				fireStationCoverageDTO.setPersons(personCoverageDTOs);

				fireStationCoverageDTOs.add(fireStationCoverageDTO);
			});

			if (totalNumAdults[0] == 0 || totalNumChildren[0] == 0) {
			    return null;
			}

			// Adding an additional FireStationCoverageDTO with the total sum for all addresses
			fireStationCoverageDTOs.add(new FireStationCoverageDTO("Zone désservie", totalNumAdults[0], totalNumChildren[0],
					new ArrayList<>()));

			// Logging successful responses at Info level
			logger.info("La méthode getFireStationCoverage a été exécutée avec succès.");

		} catch (Exception e) {
			logger.error("Une erreur s'est produite lors de l'exécution de la méthode getFireStationCoverage.", e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");
		}

		return fireStationCoverageDTOs;
	}
	
	/**
	 * Renvoie une liste d'enfants habitant à une adresse donnée, avec les membres
	 * du foyer.
	 *
	 * @param address l'adresse pour laquelle récupérer les informations
	 * @return un objet ChildAlertDTO contenant la liste des enfants et les membres du
	 *         foyer
	 * @throws Exception si une erreur se produit lors de la récupération des
	 *                   informations
	 */
	public ChildAlertDTO getChildAlert(String address) throws Exception {
		List<ChildDTO> children = new ArrayList<>();
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
						ChildDTO childDTO = new ChildDTO(person.getFirstName(), person.getLastName(), age);
						children.add(childDTO);
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

		return new ChildAlertDTO(children, householdMembers);
	}
	
	/**
	 * Cette méthode retourne les numéros de téléphone des personnes associées à une
	 * caserne de pompiers spécifiée.
	 *
	 * @param fireStationNumber le numéro de la caserne de pompiers
	 * @return une instance de PhoneAlertDTO contenant les numéros de téléphone
	 * @throws Exception
	 */
	public PhoneAlertDTO getPhoneAlert(int fireStationNumber) throws Exception {
		try {
			List<FireStation> fireStations = fireStationRepository.getFireStationByStation(fireStationNumber);
			List<String> phoneNumbers = new ArrayList<>();

			for (FireStation fireStation : fireStations) {
				String fireStationAddress = fireStation.getAddress();
				List<Person> persons = personRepository.getPersonByAddress(fireStationAddress);

				for (Person person : persons) {
					phoneNumbers.add(person.getPhone());
				}
			}

			PhoneAlertDTO phoneAlertDTO = new PhoneAlertDTO();
			phoneAlertDTO.setPhoneNumbers(phoneNumbers);

			logger.info(
					"Numéros de téléphone récupérés avec succès pour la caserne de pompiers n° : " + fireStationNumber);
			return phoneAlertDTO;
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des numéros de téléphone pour la caserne de pompiers n° : "
					+ fireStationNumber, e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");
		}
	}
	
	/**
	 * Cette méthode retourne les informations relatives à un incendie à une adresse
	 * spécifiée.
	 *
	 * @param address l'adresse où l'incendie s'est produit
	 * @return une instance de FireDTO contenant les informations sur les personnes et
	 *         la caserne de pompiers
	 * @throws Exception
	 */
	public FireDTO getFireInformation(String address) throws Exception {
		try {
			// Rechercher les personnes associées à l'adresse spécifiée
			List<Person> persons = personRepository.getPersonByAddress(address);
			if (persons.isEmpty()) {
				return null;
			}

			// Récupérer la caserne de pompiers associée à l'adresse
			FireStation fireStation = fireStationRepository.getFireStationByAddress(address);
			if (fireStation == null) {
				return null;
			}

			// Créer une liste de PersonFireDTO pour stocker les informations sur les personnes
			List<PersonFireDTO> personFireDTOs = persons.stream().map(person -> {
				// Récupérer le dossier médical de la personne
				MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFullName(person.getFirstName(),
						person.getLastName());

				// Créer un nouvel objet PersonFireDTO pour stocker les informations sur la
				// personne
				int age = calculateAge(medicalRecord.getBirthdate());
				PersonFireDTO personFireDTO = new PersonFireDTO(person.getFirstName(), person.getLastName(), person.getPhone(),
						age, medicalRecord.getMedications(), medicalRecord.getAllergies());

				return personFireDTO;
			}).collect(Collectors.toList());

			// Créer un objet FireDTO avec la liste de PersonFireDTO et le numéro de la caserne de
			// pompiers
			FireDTO fireDTO = new FireDTO(personFireDTOs, fireStation.getStation());

			logger.info("Informations sur l'incendie récupérées avec succès pour l'adresse : " + address);
			return fireDTO;
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des informations sur l'incendie pour l'adresse : " + address,
					e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");

		}
	}

	/**
	 * Récupère une liste de casernes affectées par des inondations avec les
	 * informations des habitants correspondants.
	 *
	 * @param stationNumbers La liste des numéros de caserne.
	 * @return Liste de FloodDTO contenant les adresses et les informations des
	 *         habitants.
	 * @throws Exception
	 */
	public List<FloodDTO> getFloodStations(Integer stationNumber) throws Exception {

		List<FloodDTO> floodStations = new ArrayList<>();

		try {
			// Trouver toutes les adresses correspondant au numéro de station donné
			List<String> addresses = fireStationRepository.getAllFireStations().stream()
					.filter(fireStation -> Integer.valueOf(fireStation.getStation()).equals(stationNumber))
					.map(FireStation::getAddress).collect(Collectors.toList());

			if (addresses.isEmpty()) {
				return null;
			}

			// Parcourir chaque adresse et trouver les personnes correspondantes
			for (String address : addresses) {
				List<PersonFloodDTO> persons = personRepository.getAllPersons().stream()
						.filter(person -> person.getAddress().equals(address)).map(person -> {
							MedicalRecord medicalRecord = medicalRecordRepository
									.getMedicalRecordByFullName(person.getFirstName(), person.getLastName());
							List<String> medications = medicalRecord.getMedications();
							List<String> allergies = medicalRecord.getAllergies();
							int age = calculateAge(medicalRecord.getBirthdate());

							return new PersonFloodDTO(person.getFirstName(), person.getLastName(), person.getPhone(), age,
									medications, allergies);
						}).collect(Collectors.toList());

				FloodDTO floodDTO = new FloodDTO(address, persons);
				floodStations.add(floodDTO);
			}

			// Journal des réponses réussies au niveau Info
			logger.info("La méthode getFloodStations a été exécutée avec succès.");
		} catch (Exception e) {
			// Enregistrer les erreurs ou exceptions au niveau Erreur
			logger.error("Une erreur s'est produite lors de l'exécution de la méthode getFloodStations.", e);
			throw new Exception("Une erreur s'est produite lors de la récupération des informations.");
		}

		return floodStations;
	}

	/**
	 * Cette méthode retourne les informations sur les personnes correspondant au
	 * prénom et au nom spécifiés.
	 *
	 * @param firstName le prénom de la personne
	 * @param lastName  le nom de la personne
	 * @return une liste d'objets PersonInfoDTO contenant les informations demandées
	 */
	public List<PersonInfoDTO> getPersonInfoDTO(String firstName, String lastName) {
		logger.info("Recherche des informations pour la personne : " + firstName + " " + lastName);

		try {
			// Récupérer toutes les personnes depuis le référentiel
			List<Person> persons = personRepository.getAllPersons();

			List<PersonInfoDTO> personInfoDTOs = persons.stream()
					.filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
					.map(person -> {
						// Récupérer le dossier médical de la personne
						MedicalRecord medicalRecord = medicalRecordRepository
								.getMedicalRecordByFullName(person.getFirstName(), person.getLastName());

						if (medicalRecord != null) {
							// Créer un objet PersonMedicalInfo avec les informations de base de la personne
							InfoPersonDTO infoPersonDTO = new InfoPersonDTO(person.getFirstName(), person.getLastName(),
									person.getAddress(), person.getEmail());

							// Calculer l'âge de la personne
							int age = calculateAge(medicalRecord.getBirthdate());

							// Créer un objet PersonInfoDTO avec les informations de la personne et le
							// dossier médical
							return new PersonInfoDTO(infoPersonDTO, age, medicalRecord.getMedications(),
									medicalRecord.getAllergies());
						}
						return null;
					}).filter(personInfoDTO -> personInfoDTO != null).collect(Collectors.toList());

			logger.info("Informations récupérées avec succès pour la personne : " + firstName + " " + lastName);
			return personInfoDTOs;
		} catch (Exception e) {
			logger.error(
					"Erreur lors de la récupération des informations pour la personne : " + firstName + " " + lastName,
					e);
		}

		return new ArrayList<>(); // Retourner une liste vide en cas d'erreur
	}

	/**
	 * Récupère les adresses e-mail des membres d'une communauté basée sur la ville.
	 *
	 * @param city la ville de la communauté
	 * @return CommunityEmailDTO contenant les adresses e-mail des membres de la
	 *         communauté
	 * @throws IllegalArgumentException si la ville est nulle ou vide
	 */
	public CommunityEmailDTO getCommunityEmails(String city) throws IllegalArgumentException {

		List<Person> persons;
		try {
			persons = personRepository.getPersonByCity(city).stream().filter(person -> city.equals(person.getCity()))
					.collect(Collectors.toList());
			// Vérification si la liste des personnes est vide, dans ce cas, la ville n'a
			// pas été trouvée
			if (persons.isEmpty()) {
				return null;
			}
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des personnes par ville.", e);
			throw new RuntimeException("Une erreur est survenue lors de la récupération des personnes par ville.", e);
		}

		List<String> emails = persons.stream().map(Person::getEmail).collect(Collectors.toList());

		CommunityEmailDTO communityEmailDTO = new CommunityEmailDTO();
		communityEmailDTO.setEmails(emails);

		logger.info("Adresses e-mail récupérées avec succès pour la ville : " + city);

		return communityEmailDTO;
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
