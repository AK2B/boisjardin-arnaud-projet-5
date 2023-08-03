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
import com.safetynet.alerts.model.InfoPersonDTO;
import com.safetynet.alerts.model.PersonInfoDTO;
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
		
		// Créer une liste de PersonInfoDTO
		List<PersonInfoDTO> personInfoDTOs = new ArrayList<>();
		personInfoDTOs.add(new PersonInfoDTO(new InfoPersonDTO("John", "Boyd", "1509 Culver St", "jaboyd@email.com"), 39,
				Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), Arrays.asList("nillacilan")));

		// Définir le comportement du service de l'application mocké
		when(alertsService.getPersonInfoDTO("John", "Boyd")).thenReturn(personInfoDTOs);

		// Exécuter la requête GET pour obtenir les informations sur la personne
		mockMvc.perform(MockMvcRequestBuilders.get("/personInfo").param("firstName", "John")
				.param("lastName", "Boyd").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].infoPersonDTO.firstName").value("John"))
				.andExpect(jsonPath("$[0].infoPersonDTO.lastName").value("Boyd"))
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
        
        when(alertsService.getPersonInfoDTO(firstName, lastName)).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/personInfo")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
