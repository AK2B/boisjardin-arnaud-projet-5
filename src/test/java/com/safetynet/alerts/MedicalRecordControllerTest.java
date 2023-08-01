package com.safetynet.alerts;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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

import com.safetynet.alerts.controller.MedicalRecordController;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;



@WebMvcTest(MedicalRecordController.class)
@ExtendWith(SpringExtension.class)
public class MedicalRecordControllerTest {
	@MockBean
	private MedicalRecordService medicalRecordService;

	@InjectMocks
	private MedicalRecordController medicalRecordController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetMedicalRecordByFullName() throws Exception {
		MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984",
				Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), Arrays.asList("nillacilan"));

		when(medicalRecordService.getMedicalRecordByFullName("John", "Boyd")).thenReturn(medicalRecord);

		mockMvc.perform(MockMvcRequestBuilders.get("/medicalrecord/{firstName}/{lastName}", "John", "Boyd")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Boyd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.birthdate").value("03/06/1984"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.medications.length()").value(2))
				.andExpect(MockMvcResultMatchers.jsonPath("$.medications[0]").value("aznol:350mg"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.medications[1]").value("hydrapermazol:100mg"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.allergies.length()").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.allergies[0]").value("nillacilan"));

		verify(medicalRecordService, times(1)).getMedicalRecordByFullName("John", "Boyd");
	}

	@Test
	public void testGetMedicalRecordByFullName_NotFound() throws Exception {
		String firstName = "Non-existent";
		String lastName = "Person";

		when(medicalRecordService.getMedicalRecordByFullName(firstName, lastName)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.get("/medicalrecord/{firstName}/{lastName}", firstName, lastName)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testAddMedicalRecord() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecord").contentType(MediaType.APPLICATION_JSON).content(
				"{\"firstName\":\"John\",\"lastName\":\"Boyd\",\"birthdate\":\"2000-01-01\",\"medications\":[\"Medication 1\",\"Medication 2\"],\"allergies\":[\"Allergy 1\",\"Allergy 2\"]}"))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void testUpdateMedicalRecord() throws Exception {
		String firstName = "John";
		String lastName = "Boyd";
		MedicalRecord existingMedicalRecord = new MedicalRecord(firstName, lastName, "03/06/1984",
				Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), Arrays.asList("nillacilan"));

		when(medicalRecordService.getMedicalRecordByFullName(firstName, lastName)).thenReturn(existingMedicalRecord);

		mockMvc.perform(MockMvcRequestBuilders.put("/medicalrecord/{firstName}/{lastName}", firstName, lastName)
				.contentType(MediaType.APPLICATION_JSON).content(
						"{\"firstName\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"03/06/1984\",\"medications\":[\"Medication 3\",\"Medication 4\"],\"allergies\":[\"Allergy 3\",\"Allergy 4\"]}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUpdateMedicalRecord_NotFound() throws Exception {
		String firstName = "Non-existent";
		String lastName = "Person";

		when(medicalRecordService.getMedicalRecordByFullName(firstName, lastName)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/medicalrecord/{firstName}/{lastName}", firstName, lastName)
				.contentType(MediaType.APPLICATION_JSON).content(
						"{\"firstName\":\"Non-existent\",\"lastName\":\"Person\",\"birthdate\":\"2000-01-01\",\"medications\":[\"Medication 1\",\"Medication 2\"],\"allergies\":[\"Allergy 1\",\"Allergy 2\"]}"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testDeleteMedicalRecord() throws Exception {
		String firstName = "John";
		String lastName = "Boyd";

		mockMvc.perform(MockMvcRequestBuilders.delete("/medicalrecord/{firstName}/{lastName}", firstName, lastName)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

}
