package com.safetynet.alerts;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.safetynet.alerts.controller.ChildAlertController;
import com.safetynet.alerts.model.Child;
import com.safetynet.alerts.model.ChildAlert;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.AlertsService;

@WebMvcTest( controllers = {ChildAlertController.class , AlertsService.class})
@ExtendWith(SpringExtension.class)
public class ChildAlertControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertsService alertsService;
 
    @Test
    public void testGetChildAlert() throws Exception {
        String address = "1509 Culver St";
        Child child1 = new Child("Tenley", "Boyd", 11);
        Child child2 = new Child("Roger", "Boyd", 5);
        List<Child> children = Arrays.asList(child1, child2);
        Person person1 = new Person("John", "Boyd", address, "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        Person person2 = new Person("Jacob", "Boyd", address, "Culver", "97451", "841-874-6513", "drk@email.com");
        List<Person> householdMembers = Arrays.asList(person1, person2);
        ChildAlert childAlert = new ChildAlert(children, householdMembers);

        when(alertsService.getChildAlert(address)).thenReturn(childAlert);

        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].firstName").value("Tenley"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].lastName").value("Boyd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[0].age").value(11))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[1].firstName").value("Roger"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[1].lastName").value("Boyd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children[1].age").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].lastName").value("Boyd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].address").value(address))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].city").value("Culver"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].zip").value("97451"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].phone").value("841-874-6512"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[0].email").value("jaboyd@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].firstName").value("Jacob"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].lastName").value("Boyd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].address").value(address))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].city").value("Culver"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].zip").value("97451"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].phone").value("841-874-6513"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.householdMembers[1].email").value("drk@email.com"));
        
        verify(alertsService, times(1)).getChildAlert(address);
        
    }
    
    @Test
	public void testGetChildAlert_WithEmptyAddress() throws Exception {
		String address = ""; 
		mockMvc.perform(MockMvcRequestBuilders.get("/childAlert").param("address", address)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetChildAlert_WithInvalidAddress() throws Exception {
		String address = "invalid_address";
		mockMvc.perform(MockMvcRequestBuilders.get("/childAlert").param("address", address)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
