package com.safetynet.alerts.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.safetynet.alerts.config.DataConfig;
import com.safetynet.alerts.model.Person;

@Repository
public class PersonRepository {

    private final List<Person> persons;
    
    @Autowired
    public PersonRepository(DataConfig dataConfig) {
        this.persons = dataConfig.getPersons();
    }

    public List<Person> getAllPersons() {
        return persons;
    }

    public Person getPersonById(String id) {
		return getAllPersons().stream().filter(person -> person.getId().equals(id)).findFirst()
				.orElse(null);
	}

	public List<Person> getPersonByAddress(String address) {
		return getAllPersons().stream().filter(person -> person.getAddress().equals(address))
				.collect(Collectors.toList());
	}

	public List<Person> getPersonByCity(String city) {
		return getAllPersons().stream().filter(person -> person.getCity().equals(city))
				.collect(Collectors.toList());
	}
    
    public void addPerson(Person person) {
        persons.add(person);
    }

    public void updatePerson(Person person) {
        persons.stream()
                .filter(p -> p.getId().equals(person.getId()))
                .findFirst()
                .ifPresent(p -> {
                    int index = persons.indexOf(p);
                    persons.set(index, person);
                });
    }

    public void deletePerson(String firstName, String lastName) {
        persons.removeIf(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName));
    }
}
