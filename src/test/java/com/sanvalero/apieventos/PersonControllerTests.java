package com.sanvalero.apieventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanvalero.apieventos.controller.PersonController;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.exception.ErrorResponse;
import com.sanvalero.apieventos.service.PersonService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PersonController.class)
public class PersonControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PersonService personService;

    // getAllPersons
    @Test
    public void testGetAllPersonsNoParametersReturnOK() throws Exception {
        // Create mock data
        List<PersonOutDTO> mockPersons = List.of(
            new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
            new PersonOutDTO(2L, "Montse", "García", "montse@garcia.com", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true)
        );
        // Mock service method
        when(personService.getAllPersons()).thenReturn(mockPersons);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<PersonOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<PersonOutDTO> personsResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personsResponse);
        assertEquals(2, personsResponse.size());
        assertEquals("Javier", personsResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllPersonsWithFiltersReturnOK() throws Exception {
        // Create mock data
        List<PersonOutDTO> mockPersons = List.of(
            new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
        );
        // Mock service method
        when(personService.getPersonsByFilters("Javier", 28, true)).thenReturn(mockPersons);
        // Perform the GET request with filters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                .param("firstName", "Javier")
                .param("age", "28")
                .param("verified", "true"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<PersonOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<PersonOutDTO> personsResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personsResponse);
        assertEquals(1, personsResponse.size());
        assertEquals("Javier", personsResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllPersonsReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid parameters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons")
                .param("age", "invalid"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // getPersonById
    @Test
    public void testGetPersonByIdReturnOK() throws Exception {
        // Create mock data
        PersonOutDTO mockPerson = new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method
        when(personService.getPersonById(1L)).thenReturn(Optional.of(mockPerson));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to PersonOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        PersonOutDTO personResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personResponse);
        assertEquals("Javier", personResponse.getFirstName());
    }

    @Test
    public void testGetPersonByIdReturnNotFound() throws Exception {
        // Mock service method to return empty
        when(personService.getPersonById(1L)).thenReturn(Optional.empty());
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetPersonByIdReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid ID
        mockMvc.perform(MockMvcRequestBuilders.get("/persons/invalid"))
            .andExpect(status().isInternalServerError());
    }

    // createPerson
    @Test
    public void testCreatePersonReturnCreated() throws Exception {
        // Create mock data
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        PersonOutDTO mockPersonDTO = new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method
        when(personService.createPerson(any())).thenReturn(mockPersonDTO);
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isCreated())
            .andReturn();
        // Convert response to PersonOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        PersonOutDTO personResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personResponse);
        assertEquals("Javier", personResponse.getFirstName());
    }

    @Test
    public void testCreatePersonWithInvalidDataReturnBadRequest() throws Exception {
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 5.0, "Technology", LocalDate.of(1996, 4, 29), true);
        // Perform the POST request with invalid data (missing required fields)
        mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePersonReturnInternalServerError() throws Exception {
        // Create mock data
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method
        when(personService.createPerson(any())).thenThrow(new RuntimeException("Database connection error"));
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Convert response to ErrorResponse
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify error response
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // updatePerson
    @Test
    public void testUpdatePersonReturnAccepted() throws Exception {
        // Create mock data
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        PersonOutDTO mockPersonDTO = new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method
        when(personService.updatePerson(anyLong(), any())).thenReturn(mockPersonDTO);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/persons/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to PersonOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        PersonOutDTO personResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personResponse);
        assertEquals("Javier", personResponse.getFirstName());
    }

    @Test
    public void testUpdatePersonReturnNotFound() throws Exception {
        // Create mock data
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method to throw exception
        when(personService.updatePerson(anyLong(), any())).thenThrow(new EntityNotFoundException("Person not found with id: 1"));
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/persons/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testUpdatePersonReturnBadRequest() throws Exception {
        // Create mock data
        Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "password", 28, 6.0, "Technology", LocalDate.of(1996, 4, 29), true);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/persons/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockPerson)))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Convert response to ErrorResponse
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    // partialUpdatePerson
    @Test
    public void testPartialUpdatePersonReturnAccepted() throws Exception {
        // Create mock data
        PersonOutDTO mockPerson = new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
        // Mock service method
        when(personService.partialUpdatePerson(anyLong(), any())).thenReturn(mockPerson);
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/persons/1")
                .contentType("application/json")
                .content("{\"firstName\":\"Javier\"}"))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to PersonOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        PersonOutDTO personResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personResponse);
        assertEquals("Javier", personResponse.getFirstName());
    }

    @Test
    public void testPartialUpdatePersonReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(personService.partialUpdatePerson(anyLong(), any())).thenThrow(new EntityNotFoundException("Person not found with id: 1"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/persons/1")
                .contentType("application/json")
                .content("{\"firstName\":\"Javier\"}"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testPartialUpdatePersonReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(personService.partialUpdatePerson(anyLong(), any())).thenThrow(new IllegalArgumentException("Age cannot be negative"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/persons/1")
                .contentType("application/json")
                .content("{\"age\":-1}")) // Invalid age
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    // deletePerson
    @Test
    public void testDeletePersonReturnAccepted() throws Exception {
        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/persons/1"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void testDeletePersonReturnInternalServerError() throws Exception {
        // Mock service method to throw exception
        doThrow(new RuntimeException("Database error")).when(personService).deletePerson(1L);
        // Perform the DELETE request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/persons/1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // getPersonsByConferenceId
    @Test
    public void testGetPersonsByConferenceIdReturnOK() throws Exception {
        // Create mock data
        List<PersonOutDTO> mockPersons = List.of(
            new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
        );
        // Mock service method
        when(personService.getPersonsByConferenceId(1L)).thenReturn(mockPersons);
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons/conferences/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<PersonOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<PersonOutDTO> personsResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(personsResponse);
        assertEquals(1, personsResponse.size());
        assertEquals("Javier", personsResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetPersonsByConferenceIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(personService.getPersonsByConferenceId(1L)).thenThrow(new EntityNotFoundException("Conference not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons/conferences/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetPersonsByConferenceIdReturnInternalServerError() throws Exception {
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/persons/conferences/a"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }
}