package com.safetynet.alerts;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.safetynet.alerts.controller.FireStationController;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.FireStationCoverageDTO;
import com.safetynet.alerts.service.AlertsService;
import com.safetynet.alerts.service.FireStationService;

@WebMvcTest(FireStationController.class)
@ExtendWith(SpringExtension.class)
public class FireStationControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FireStationService fireStationService;

	@MockBean
	private AlertsService alertsService;

	@InjectMocks
	private FireStationController fireStationController;

	@Test
	public void testGetFireStationCoverage() throws Exception {

		int fireStationNumber = 3;

		// Créer une liste de FireStationCoverageDTO
		List<FireStationCoverageDTO> fireStationCoverageDTOs = new ArrayList<>();
		fireStationCoverageDTOs.add(new FireStationCoverageDTO("1509 Culver St", 3, 2, new ArrayList<>()));

		// Définir le comportement du service de l'application mocké
		when(alertsService.getFireStationCoverage(fireStationNumber)).thenReturn(fireStationCoverageDTOs);

		// Exécuter la requête GET pour obtenir la couverture de la caserne de pompiers
		mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
				.param("stationNumber", String.valueOf(fireStationNumber)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].address").value("1509 Culver St"))
				.andExpect(jsonPath("$[0].numAdults").value(3)).andExpect(jsonPath("$[0].numChildren").value(2));

		verify(alertsService, times(1)).getFireStationCoverage(fireStationNumber);
	}

	@Test
	public void testGetFireStationCoverage_EmptyStationNumber() throws Exception {
		String fireStationNumberStr = "";

		mockMvc.perform(MockMvcRequestBuilders.get("/firestation").param("stationNumber", fireStationNumberStr)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetFireStationCoverage_NullStationNumber() throws Exception {
		String fireStationNumberStr = null;

		mockMvc.perform(MockMvcRequestBuilders.get("/firestation").param("stationNumber", fireStationNumberStr)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetFireStationCoverage_InvalidStationNumber() throws Exception {
		String fireStationNumberStr = "abc";

		mockMvc.perform(MockMvcRequestBuilders.get("/firestation").param("stationNumber", fireStationNumberStr)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetFireStationCoverage_InternalServerError() throws Exception {
		int fireStationNumber = 3;

		doAnswer(invocation -> {
			throw new Exception("Test Internal Server Error");
		}).when(alertsService).getFireStationCoverage(fireStationNumber);

		mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
				.param("stationNumber", String.valueOf(fireStationNumber)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	@Test
	public void testGetFireStationCoverage_FireStationNotFound() throws Exception {
		int invalidStationNumber = 6;
		
		when(alertsService.getFireStationCoverage(invalidStationNumber)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
				.param("stationNumber", String.valueOf(invalidStationNumber)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	@Test
	public void testGetFireStationByAddress() throws Exception {
		String address = "29 15th St";

		FireStation fireStation = new FireStation(address, 2);

		when(fireStationService.getFireStationByAddress(address)).thenReturn(fireStation);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/firestation/{address}", address).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.address").value(address))
				.andExpect(MockMvcResultMatchers.jsonPath("$.station").value("2"));
	}

	@Test
	public void testGetFireStationByAddress_NotFound() throws Exception {
		String address = "Non-existent Address";

		when(fireStationService.getFireStationByAddress(address)).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.get("/firestation/{address}", address).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testAddFireStation() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/firestation").contentType(MediaType.APPLICATION_JSON)
				.content("{\"address\":\"Address 1\",\"station\":1}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void testUpdateFireStation() throws Exception {
		String address = "1509 Culver St";

		FireStation existingFireStation = new FireStation(address, 3);

		when(fireStationService.getFireStationByAddress(address)).thenReturn(existingFireStation);

		mockMvc.perform(MockMvcRequestBuilders.put("/firestation/{address}", address)
				.contentType(MediaType.APPLICATION_JSON).content("{\"address\":\"1509 Culver St\",\"station\":2}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUpdateFireStation_NotFound() throws Exception {
		String address = "Non-existent Address";

		when(fireStationService.getFireStationByAddress(address)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/firestation/{address}", address)
				.contentType(MediaType.APPLICATION_JSON).content("{\"address\":\"Address 1\",\"station\":2}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testDeleteFireStation() throws Exception {
		String address = "1509 Culver St";

		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation/{address}", address)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}
}
