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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.safetynet.alerts.controller.FireController;
import com.safetynet.alerts.model.Fire;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.PersonFire;
import com.safetynet.alerts.service.AlertsService;

@WebMvcTest (controllers = { FireController.class, AlertsService.class })
@ExtendWith(SpringExtension.class)
public class FireControllerTest {
	
    @Autowired
    private MockMvc mockMvc;
    
	@MockBean
	private AlertsService alertsService;
	
    @Test
    public void testGetFireInformation() throws Exception {
        String address = "1509 Culver St";

        // Créer une liste de personnes associées à l'adresse
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", address, "Culver", "97451", "841-874-6512", "jaboyd@email.com"));

        // Créer un objet FireStation pour l'adresse
        FireStation fireStation = new FireStation(address, 3);

        // Créer une liste de PersonFire
        List<PersonFire> personFires = new ArrayList<>();
        personFires.add(new PersonFire("John", "Boyd", "841-874-6512", 39, Arrays.asList("aznol:350mg","hydrapermazol:100mg"), Arrays.asList("nillacilan")));

        // Créer un objet Fire avec la liste de PersonFire et le numéro de la caserne de pompiers
        Fire fire = new Fire(personFires, fireStation.getStation());

        // Définir le comportement du service de l'application mocké
        when(alertsService.getFireInformation(address)).thenReturn(fire);

        // Exécuter la requête GET pour obtenir les détails de l'incendie
        mockMvc.perform(MockMvcRequestBuilders.get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personFires[0].firstName").value("John"))
                .andExpect(jsonPath("$.personFires[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.personFires[0].phone").value("841-874-6512"))
                .andExpect(jsonPath("$.personFires[0].age").value("39"))
                .andExpect(jsonPath("$.personFires[0].medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.personFires[0].medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.personFires[0].allergies[0]").value("nillacilan"))
                .andExpect(jsonPath("$.fireStationNumber").value("3"));
        
		verify(alertsService, times(1)).getFireInformation(address);
    }
    
    @Test
    public void testGetFireDetailsWithEmptyAddress() throws Exception {
        String address = ""; 
        
        mockMvc.perform(MockMvcRequestBuilders.get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetFireDetailsWithAddressNotFound() throws Exception {
        String address = "invalid_address"; 

        mockMvc.perform(MockMvcRequestBuilders.get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
}
