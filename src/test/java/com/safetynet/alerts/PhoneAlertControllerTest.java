package com.safetynet.alerts;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.safetynet.alerts.controller.PhoneAlertController;
import com.safetynet.alerts.model.PhoneAlert;
import com.safetynet.alerts.service.AlertsService;

@WebMvcTest(controllers = { PhoneAlertController.class, AlertsService.class })
@ExtendWith(SpringExtension.class)
public class PhoneAlertControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AlertsService alertsService;

	@Test
	public void testGetPhoneAlert() throws Exception {
		// Numéro de caserne de pompiers pour lequel nous voulons obtenir les numéros de
		// téléphone
		int fireStationNumber = 1;

		// Créer une liste de numéros de téléphone
		List<String> phoneNumbers = Arrays.asList("841-874-651", "841-874-8547");

		// Créer un objet PhoneAlert avec les numéros de téléphone
		PhoneAlert phoneAlert = new PhoneAlert();
		phoneAlert.setPhoneNumbers(phoneNumbers);

		// Définir le comportement du service de l'application mocké
		when(alertsService.getPhoneAlert(fireStationNumber)).thenReturn(phoneAlert);

		// Exécuter la requête GET pour obtenir les numéros de téléphone de la caserne
		// de pompiers
		mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert").param("firestation", "1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.phoneNumbers[0]").value("841-874-651"))
				.andExpect(jsonPath("$.phoneNumbers[1]").value("841-874-8547"));

		verify(alertsService, times(1)).getPhoneAlert(fireStationNumber);
	}

	@Test
	public void testGetPhoneAlert_InvalidParameters_BadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert").param("firestation", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetPhoneAlert_ParametersNotFound_NotFound() throws Exception {
		String firestationNumberStr = "1";
		int firestationNumber = Integer.parseInt(firestationNumberStr);

		when(alertsService.getPhoneAlert(firestationNumber)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert").param("firestation", firestationNumberStr))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetPhoneAlert_InvalidParameter_BadRequest() throws Exception {
		String firestationNumberStr = "abc";

		mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert").param("firestation", firestationNumberStr))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
