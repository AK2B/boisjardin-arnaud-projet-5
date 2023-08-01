package com.safetynet.alerts;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.safetynet.alerts.controller.FloodController;
import com.safetynet.alerts.model.Flood;
import com.safetynet.alerts.model.PersonFlood;
import com.safetynet.alerts.service.AlertsService;

@WebMvcTest(controllers = { FloodController.class, AlertsService.class })
@ExtendWith(SpringExtension.class)
public class FloodControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AlertsService alertsService;

	@Test
	public void testGetFloodStations() throws Exception {
		// Numéros de station pour laquel nous voulons les informations de crue
		Integer stationNumbers = 1;

		// Créer une liste de Flood
		List<Flood> floodStations = new ArrayList<>();
		List<PersonFlood> persons1 = Arrays.asList(
				new PersonFlood("Jonanathan", "Marrack", "841-874-6513", 34, new ArrayList<>(), new ArrayList<>()));

		floodStations.add(new Flood("29 15th St", persons1));

		List<PersonFlood> persons2 = Arrays.asList(
				new PersonFlood("Peter", "Duncan", "841-874-6512", 22, new ArrayList<>(), Arrays.asList("shellfish")));

		floodStations.add(new Flood("644 Gershwin Cir", persons2));

		// Définir le comportement du service de l'application mocké
		when(alertsService.getFloodStations(stationNumbers)).thenReturn(floodStations);

		// Exécuter la requête GET pour les informations sur les stations de crue
		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations").param("stations", "1")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].address").value("29 15th St"))
				.andExpect(jsonPath("$[0].persons[0].firstName").value("Jonanathan"))
				.andExpect(jsonPath("$[0].persons[0].medications").isEmpty())
				.andExpect(jsonPath("$[0].persons[0].allergies").isEmpty())
				.andExpect(jsonPath("$[1].address").value("644 Gershwin Cir"))
				.andExpect(jsonPath("$[1].persons[0].firstName").value("Peter"))
				.andExpect(jsonPath("$[1].persons[0].medications").isEmpty())
				.andExpect(jsonPath("$[1].persons[0].allergies[0]").value("shellfish"));

		verify(alertsService, times(1)).getFloodStations(stationNumbers);
	}

	@Test
	public void testGetFloodStations_StationNumberNotFound() throws Exception {
		int invalidStationNumber = 6;
		
		when(alertsService.getFloodStations(invalidStationNumber)).thenReturn(null);
		 
		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations")
				.param("stations", String.valueOf(invalidStationNumber)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetFloodStations_EmptyStationNumber() throws Exception {
		String stationNumber = "";

		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations").param("stations", stationNumber)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetFloodStations_NullStationNumber() throws Exception {
		String stationNumber = null;

		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations").param("stations", stationNumber)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetFloodStations_InvalidStationNumber() throws Exception {
		String stationNumber = "abc";

		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations").param("stations", stationNumber)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

}
