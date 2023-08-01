package com.safetynet.alerts;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;


@WebMvcTest(PersonController.class)
@ExtendWith(SpringExtension.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PersonService personService;
	
	@InjectMocks
	private PersonController personController;

	@Test
	public void testGetPersonById() throws Exception {
		String id = "boyd-john";
		Person person = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
				"jaboyd@email.com");

		when(personService.getPersonById(id)).thenReturn(person);

		mockMvc.perform(MockMvcRequestBuilders.get("/person/{id}", id).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value("boyd-john"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Boyd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value("1509 Culver St"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Culver"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.zip").value("97451"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("841-874-6512"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("jaboyd@email.com"));
	}

	@Test
	public void testGetPersonById_NotFound() throws Exception {
		String id = "nonexistent-id";

		when(personService.getPersonById(id)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/person/{id}", id).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testAddPerson() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/person").contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"Address 1\",\"city\":\"City 1\",\"zip\":\"12345\",\"phone\":\"1234567890\",\"email\":\"john.doe@example.com\"}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void testUpdatePerson() throws Exception {
		String id = "boyd-john";
		Person existingPerson = new Person("John", "boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
				"jaboyd@email.com");

		when(personService.getPersonById(id)).thenReturn(existingPerson);

		mockMvc.perform(MockMvcRequestBuilders.put("/person/{id}", id).contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"New Address\",\"city\":\"New City\",\"zip\":\"54321\",\"phone\":\"0987654321\",\"email\":\"john.doe@example.com\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUpdatePerson_NotFound() throws Exception {
		String id = "nonexistent-id";

		when(personService.getPersonById(id)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/person/{id}", id).contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"address\":\"New Address\",\"city\":\"New City\",\"zip\":\"54321\",\"phone\":\"0987654321\",\"email\":\"john.doe@example.com\"}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testDeletePerson() throws Exception {
		String firstName = "John";
		String lastName = "Boyd";

		mockMvc.perform(MockMvcRequestBuilders.delete("/person/{firstName}/{lastName}", firstName, lastName)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}
		
}
