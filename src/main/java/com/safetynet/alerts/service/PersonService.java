package com.safetynet.alerts.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

@Service
public class PersonService {

	private final PersonRepository personRepository;

	@Autowired
	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public List<Person> getAllPersons() {
		return personRepository.getAllPersons();
	}

	public Person getPersonById(String id) {
		return getAllPersons().stream().filter(person -> person.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Person> getPersonByAddress(String address) {
		return getAllPersons().stream().filter(person -> person.getAddress().equals(address))
				.collect(Collectors.toList());
	}

	public List<Person> getPersonByCity(String city) {
		return getAllPersons().stream().filter(person -> person.getCity().equals(city)).collect(Collectors.toList());
	}

	public void addPerson(Person person) {
		personRepository.addPerson(person);
	}

	public void updatePerson(Person person) {
		personRepository.updatePerson(person);
	}

	public void deletePerson(String firstName, String lastName) {
		personRepository.deletePerson(firstName, lastName);
	}
}
