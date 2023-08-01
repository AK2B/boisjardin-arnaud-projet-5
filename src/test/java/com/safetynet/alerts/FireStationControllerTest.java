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

import com.safetynet.alerts.controller.FireStationController;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;

@WebMvcTest(FireStationController.class)
@ExtendWith(SpringExtension.class)
public class FireStationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FireStationService fireStationService;

	@InjectMocks
	private FireStationController fireStationController;

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
