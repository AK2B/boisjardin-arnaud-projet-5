package com.safetynet.alerts;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.safetynet.alerts.controller.PersonInfoController;
import com.safetynet.alerts.model.InfoPerson;
import com.safetynet.alerts.model.PersonInfo;
import com.safetynet.alerts.service.AlertsService;

@WebMvcTest(controllers = { PersonInfoController.class, AlertsService.class })
@ExtendWith(SpringExtension.class)
public class PersonInfoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AlertsService alertsService;

	@Test
	public void testGetPersonInfo() throws Exception {
		String firstName = "John";
		String lastName = "Boyd";

		// Créer une liste de PersonInfo
		List<PersonInfo> personInfos = new ArrayList<>();
		personInfos.add(new PersonInfo(new InfoPerson(firstName, lastName, "1509 Culver St", "jaboyd@email.com"), 39,
				Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), Arrays.asList("nillacilan")));

		// Définir le comportement du service de l'application mocké
		when(alertsService.getPersonInfo(firstName, lastName)).thenReturn(personInfos);

		// Exécuter la requête GET pour obtenir les informations sur la personne
		mockMvc.perform(MockMvcRequestBuilders.get("/personInfo").param("firstName", firstName)
				.param("lastName", lastName).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].infoPerson.firstName").value(firstName))
				.andExpect(jsonPath("$[0].infoPerson.lastName").value(lastName))
				.andExpect(jsonPath("$[0].age").value(39))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].medications[0]").value("aznol:350mg"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].medications[1]").value("hydrapermazol:100mg"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].allergies[0]").value("nillacilan"));
	}
	
	@Test
    public void testGetPersonInfo_InvalidParameters_BadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/personInfo")
                .param("firstName", "")
                .param("lastName", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetPersonInfo_ParametersNotFound_BadRequest() throws Exception {
        String firstName = "John";
        String lastName = "Boyd";
        
        when(alertsService.getPersonInfo(firstName, lastName)).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
