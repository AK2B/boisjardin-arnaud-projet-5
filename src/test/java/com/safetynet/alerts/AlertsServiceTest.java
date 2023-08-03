package com.safetynet.alerts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.alerts.model.ChildDTO;
import com.safetynet.alerts.model.ChildAlertDTO;
import com.safetynet.alerts.model.CommunityEmailDTO;
import com.safetynet.alerts.model.FireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.FireStationCoverageDTO;
import com.safetynet.alerts.model.FloodDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.PersonFloodDTO;
import com.safetynet.alerts.model.PersonInfoDTO;
import com.safetynet.alerts.model.PhoneAlertDTO;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.service.AlertsService;

@ExtendWith(MockitoExtension.class)
public class AlertsServiceTest {

	private static Instant startedAt;

	@Mock
	private PersonRepository personRepository;

	@Mock
	private FireStationRepository fireStationRepository;

	@Mock
	private MedicalRecordRepository medicalRecordRepository;

	@InjectMocks
	private AlertsService alertsService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@BeforeAll
	static public void initStartingTime() {
		System.out.println("Appel avant tous les tests");
		startedAt = Instant.now();
	}

	@AfterAll
	static public void showTestDuration() {
		System.out.println("Appel après tous les tests");
		Instant endedAt = Instant.now();
		long duration = Duration.between(startedAt, endedAt).toMillis();
		System.out.println(MessageFormat.format("Durée des tests : {0} ms", duration));
	}
	
	@Test
	public void testGetFireStationCoverage() throws Exception {
        // Arrange
        int fireStationNumber = 1;

        // Mock de fireStationRepository
        List<FireStation> fireStations = Arrays.asList(
                new FireStation("1509 Culver St", 1),
                new FireStation("123 Main St", 1),
                new FireStation("456 Elm St", 2));
        when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);

        // Mock de personRepository
        List<Person> persons = Arrays.asList(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "john@example.com"),
                new Person("Jane", "Smith", "123 Main St", "Culver", "97451", "841-874-1234", "jane@example.com"),
                new Person("Robert", "Doe", "456 Elm St", "Culver", "97451", "841-874-5678", "robert@example.com"));
        when(personRepository.getAllPersons()).thenReturn(persons);

        // Mock de medicalRecordRepository
        MedicalRecord medicalRecord1 = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(), new ArrayList<>());
        MedicalRecord medicalRecord2 = new MedicalRecord("Jane", "Smith", "05/01/2015", new ArrayList<>(), new ArrayList<>());
        MedicalRecord medicalRecord3 = new MedicalRecord("Robert", "Doe", "02/05/2016", new ArrayList<>(), new ArrayList<>());
        when(medicalRecordRepository.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord1);
        when(medicalRecordRepository.getMedicalRecordByFullName("Jane", "Smith")).thenReturn(medicalRecord2);
        lenient().when(medicalRecordRepository.getMedicalRecordByFullName("Robert", "Doe")).thenReturn(medicalRecord3);

        // Act
        List<FireStationCoverageDTO> result = alertsService.getFireStationCoverage(fireStationNumber);

        //Assert
        assertThat(result).isNotNull();
       
        // Filtrer pour "Zone désservie"
        FireStationCoverageDTO zoneDesservieCoverage = result.stream()
                .filter(coverage -> coverage.getAddress().equals("Zone désservie"))
                .findFirst()
                .orElse(null);

        assertThat(zoneDesservieCoverage).isNotNull();
        assertThat(zoneDesservieCoverage.getNumAdults()).isEqualTo(1);
        assertThat(zoneDesservieCoverage.getNumChildren()).isEqualTo(1);

		// Vérification des appels de méthodes simulées
        verify(fireStationRepository, times(1)).getAllFireStations();
        verify(personRepository, times(1)).getAllPersons();
        verify(medicalRecordRepository, times(2)).getMedicalRecordByFullName(anyString(), anyString());
    }
	
	@Test
	public void testGetChildAlert() throws Exception {
		// Adresse de test
		String address = "1509 Culver St";

		// Création de personnes
		Person person1 = new Person("John", "Boyd", address, "Culver", "97451", "841-874-6512",
				"john.boyd@example.com");
		Person person2 = new Person("Jane", "Smith", address, "Culver", "97451", "841-874-1234",
				"jane.smith@example.com");
		Person person3 = new Person("Robert", "Doe", "123 Main St", "Culver", "97451", "841-874-5678",
				"robert.doe@example.com");

		// Création de dossiers médicaux
		MedicalRecord medicalRecord1 = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(),
				new ArrayList<>());
		MedicalRecord medicalRecord2 = new MedicalRecord("Jane", "Smith", "02/01/2015", new ArrayList<>(),
				new ArrayList<>());
		MedicalRecord medicalRecord3 = new MedicalRecord("Robert", "Doe", "03/05/2016", new ArrayList<>(),
				new ArrayList<>());

		// Liste de personnes
		List<Person> persons = new ArrayList<>();
		persons.add(person1);
		persons.add(person2);
		persons.add(person3);

		// Configuration des méthodes simulées
		when(personRepository.getAllPersons()).thenReturn(persons);
		when(medicalRecordRepository.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord1);
		when(medicalRecordRepository.getMedicalRecordByFullName("Jane", "Smith")).thenReturn(medicalRecord2);
		lenient().when(medicalRecordRepository.getMedicalRecordByFullName("Robert", "Doe")).thenReturn(medicalRecord3);

		// Appel de la méthode à tester
		ChildAlertDTO childAlertDTO = alertsService.getChildAlert(address);

		// Vérification des résultats
		assertThat(childAlertDTO).isNotNull();

		List<ChildDTO> expectedChildren = new ArrayList<>();

		// Ajouter les enfants qui habitent à l'adresse demandée
		for (Person person : persons) {
			if (person.getAddress().equals(address)) {
				MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFullName(person.getFirstName(),
						person.getLastName());
				int age = alertsService.calculateAge(medicalRecord.getBirthdate());

				if (age <= 18) {
					ChildDTO childDTO = new ChildDTO(person.getFirstName(), person.getLastName(), age);
					expectedChildren.add(childDTO);
				}
			}
		}

		List<Person> expectedHouseholdMembers = new ArrayList<>();
		expectedHouseholdMembers.add(person1);

		assertThat(childAlertDTO.getChildren().size()).isEqualTo(expectedChildren.size());
		assertThat(childAlertDTO.getHouseholdMembers().size()).isEqualTo(expectedHouseholdMembers.size());

		for (int i = 0; i < expectedChildren.size(); i++) {
			ChildDTO expectedChild = expectedChildren.get(i);
			ChildDTO actualChild = childAlertDTO.getChildren().get(i);

			assertThat(actualChild.getFirstName()).isEqualTo(expectedChild.getFirstName());
			assertThat(actualChild.getLastName()).isEqualTo(expectedChild.getLastName());
			assertThat(actualChild.getAge()).isEqualTo(expectedChild.getAge());

		}
		for (int i = 0; i < expectedHouseholdMembers.size(); i++) {
			Person expectedMember = expectedHouseholdMembers.get(i);
			Person actualMember = childAlertDTO.getHouseholdMembers().get(i);

			assertThat(actualMember.getFirstName()).isEqualTo(expectedMember.getFirstName());
			assertThat(actualMember.getLastName()).isEqualTo(expectedMember.getLastName());

		}

		// Vérification des appels de méthodes simulées
		verify(personRepository, times(1)).getAllPersons();
		verify(medicalRecordRepository, times(2)).getMedicalRecordByFullName("John", "Boyd");
	}

	@Test
	public void testGetPhoneAlert() throws Exception {
		int fireStationNumber = 1;
		String address1 = "1509 Culver St";
		String address2 = "123 Main St";

		FireStation fireStation1 = new FireStation(address1, fireStationNumber);
		FireStation fireStation2 = new FireStation(address2, fireStationNumber);

		Person person1 = new Person("John", "Boyd", address1, "Culver", "97451", "841-874-6512",
				"john.boyd@example.com");
		Person person2 = new Person("Jane", "Smith", address2, "Culver", "97451", "841-874-1234",
				"jane.smith@example.com");

		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(fireStation1);
		fireStations.add(fireStation2);

		List<Person> persons1 = new ArrayList<>();
		persons1.add(person1);

		List<Person> persons2 = new ArrayList<>();
		persons2.add(person2);

		when(fireStationRepository.getFireStationByStation(fireStationNumber)).thenReturn(fireStations);
		when(personRepository.getPersonByAddress(address1)).thenReturn(persons1);
		when(personRepository.getPersonByAddress(address2)).thenReturn(persons2);

		PhoneAlertDTO phoneAlertDTO = alertsService.getPhoneAlert(fireStationNumber);

		assertThat(phoneAlertDTO).isNotNull();
		assertThat(phoneAlertDTO.getPhoneNumbers().size()).isEqualTo(2);
		Assertions.assertThat(phoneAlertDTO.getPhoneNumbers()).contains(person1.getPhone()).contains(person2.getPhone());

		verify(fireStationRepository, times(1)).getFireStationByStation(fireStationNumber);
		verify(personRepository, times(1)).getPersonByAddress(address1);
		verify(personRepository, times(1)).getPersonByAddress(address2);
	}
	
	@Test
	public void testGetFireInformation() throws Exception {
		// Arrange
		String address = "1509 Culver St";

		// Mock de personRepository
		List<Person> persons = new ArrayList<>();
		Person person = new Person("John", "Boyd", address, "Culver", "97451", "841-874-6512", "jaboyd@example.com");
		persons.add(person);
		when(personRepository.getPersonByAddress(address)).thenReturn(persons);

		// Mock de fireStationRepository
		FireStation fireStation = new FireStation(address, 1);
		when(fireStationRepository.getFireStationByAddress(address)).thenReturn(fireStation);

		// Mock de medicalRecordRepository
		MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(),
				new ArrayList<>());
		when(medicalRecordRepository.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord);

		// Act
		FireDTO result = alertsService.getFireInformation(address);

		// Assert
		assertThat(result.getPersonFireDTOs().size()).isEqualTo(1);
		assertThat(result.getFireStationNumber()).isEqualTo(1);

	}
	
	@Test
	public void testGetFloodStations() throws Exception {
		// Arrange
		Integer stationNumbers = 1;

		// Mocking the fireStationRepository
		List<FireStation> fireStations = new ArrayList<>();
		fireStations.add(new FireStation("1509 Culver St", 1));
		fireStations.add(new FireStation("123 Main St", 2));
		fireStations.add(new FireStation("456 Elm St", 3));
		when(fireStationRepository.getAllFireStations()).thenReturn(fireStations);

		// Mocking the personRepository
		List<Person> persons = new ArrayList<>();
		persons.add(
				new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "john@example.com"));
		persons.add(new Person("Jane", "Smith", "123 Main St", "Culver", "97451", "841-874-1234", "jane@example.com"));
		persons.add(new Person("Robert", "Doe", "456 Elm St", "Culver", "97451", "841-874-5678", "robert@example.com"));
		when(personRepository.getAllPersons()).thenReturn(persons);

		// Mocking the medicalRecordRepository
		MedicalRecord medicalRecord1 = new MedicalRecord("John", "Boyd", "03/06/1984", Arrays.asList("Medicine1"),
				Arrays.asList("Allergy1"));
		MedicalRecord medicalRecord2 = new MedicalRecord("Jane", "Smith", "05/01/2015", Arrays.asList("Medicine2"),
				Arrays.asList("Allergy2"));
		MedicalRecord medicalRecord3 = new MedicalRecord("Robert", "Doe", "01/05/2016", Arrays.asList("Medicine3"),
				Arrays.asList("Allergy3"));
		when(medicalRecordRepository.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord1);
		lenient().when(medicalRecordRepository.getMedicalRecordByFullName("Jane", "Smith")).thenReturn(medicalRecord2);
		lenient().when(medicalRecordRepository.getMedicalRecordByFullName("Robert", "Doe")).thenReturn(medicalRecord3);

		// Act
		List<FloodDTO> result = alertsService.getFloodStations(stationNumbers);

		// Assert
		assertThat(result).isNotNull();		
		
		for (int i = 0; i < result.size(); i++) {
			FloodDTO floodDTO = result.get(i);
			assertThat(floodDTO.getPersons().size()).isEqualTo(1);

			PersonFloodDTO personFloodDTO = floodDTO.getPersons().get(0);
			Person person = persons.get(i);
			MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFullName(person.getFirstName(),
					person.getLastName());

			assertThat(personFloodDTO.getFirstName()).isEqualTo(person.getFirstName());
			assertThat(personFloodDTO.getLastName()).isEqualTo(person.getLastName());
			assertThat(personFloodDTO.getPhone()).isEqualTo(person.getPhone());
			assertThat(personFloodDTO.getAge()).isEqualTo(alertsService.calculateAge(medicalRecord.getBirthdate()));
			assertThat(personFloodDTO.getMedications()).isEqualTo(medicalRecord.getMedications());
			assertThat(personFloodDTO.getAllergies()).isEqualTo(medicalRecord.getAllergies());

		}

		// Verify the method calls
		verify(fireStationRepository, times(1)).getAllFireStations();
		verify(personRepository, times(1)).getAllPersons();
		verify(medicalRecordRepository, times(2)).getMedicalRecordByFullName(anyString(), anyString());
	}
	
	@Test
	public void testGetPersonInfo() {

		Person person = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
				"jaboyd@email.com");
		MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(),
				new ArrayList<>());
		List<Person> persons = new ArrayList<>();
		persons.add(person);

		when(personRepository.getAllPersons()).thenReturn(persons);
		when(medicalRecordRepository.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord);

		List<PersonInfoDTO> personInfoDTOs = alertsService.getPersonInfoDTO("John", "Boyd");

		assertThat(personInfoDTOs.size()).isEqualTo(1);

		PersonInfoDTO personInfoDTO = personInfoDTOs.get(0);

		assertThat(personInfoDTO.getInfoPersonDTO().getFirstName()).isEqualTo("John");
		assertThat(personInfoDTO.getInfoPersonDTO().getLastName()).isEqualTo("Boyd");
		assertThat(personInfoDTO.getInfoPersonDTO().getAddress()).isEqualTo("1509 Culver St");
		assertThat(personInfoDTO.getInfoPersonDTO().getEmail()).isEqualTo("jaboyd@email.com");
		assertThat(personInfoDTO.getAge()).isEqualTo(39);
		assertThat(personInfoDTO.getMedications().size()).isEqualTo(0);
		assertThat(personInfoDTO.getAllergies().size()).isEqualTo(0);

		verify(personRepository, times(1)).getAllPersons();
		verify(medicalRecordRepository, times(1)).getMedicalRecordByFullName("John", "Boyd");
	}

	@Test
	public void testGetCommunityEmails() {
		// Ville de test
		String city = "Culver";

		// Création de personnes
		Person person1 = new Person("John", "Boyd", "1509 Culver St", city, "97451", "841-874-6512",
				"john.boyd@example.com");
		Person person2 = new Person("Jane", "Smith", "1510 Culver St", city, "97451", "841-874-1234",
				"jane.smith@example.com");
		Person person3 = new Person("Robert", "Doe", "123 Main St", "Springfield", "12345", "555-123-4567",
				"robert.doe@example.com");

		// Liste de personnes
		List<Person> persons = new ArrayList<>();
		persons.add(person1);
		persons.add(person2);
		persons.add(person3);

		// Configuration de la méthode simulée
		when(personRepository.getPersonByCity(city)).thenReturn(persons);

		// Appel de la méthode à tester
		CommunityEmailDTO communityEmailDTO = alertsService.getCommunityEmails(city);

		// Vérification des résultats
		assertThat(communityEmailDTO).isNotNull();

		List<String> expectedEmails = Arrays.asList("john.boyd@example.com", "jane.smith@example.com");
		
		assertThat(communityEmailDTO.getEmails().size()).isEqualTo(expectedEmails.size());
		Assertions.assertThat(communityEmailDTO.getEmails()).containsAll(expectedEmails);
		
		// Vérification que seules les personnes de la ville sont présentes dans la
		// liste
		for (Person person : persons) {
			if (person.getCity().equals(city)) {
				Assertions.assertThat(communityEmailDTO.getEmails()).contains(person.getEmail());
				
			} else {
				Assertions.assertThat(communityEmailDTO.getEmails()).doesNotContain(person.getEmail());
			}
		}

		// Vérification des appels de méthodes simulées
		verify(personRepository, times(1)).getPersonByCity(city);
	}
	
	@Test
	public void testCalculateAge() {
		String birthDate = "03/06/1984";

		int age = alertsService.calculateAge(birthDate);

		assertThat(age).isEqualTo(39);
	}

}