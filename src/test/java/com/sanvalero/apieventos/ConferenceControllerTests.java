package com.sanvalero.apieventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanvalero.apieventos.controller.ConferenceController;
import com.sanvalero.apieventos.dto.ConferenceInDTO;
import com.sanvalero.apieventos.dto.ConferenceOutDTO;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.exception.ErrorResponse;
import com.sanvalero.apieventos.service.ConferenceService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

@WebMvcTest(ConferenceController.class)
public class ConferenceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ConferenceService conferenceService;

    // getAllConferences
    @Test
    public void testGetAllConferencesNoFiltersReturnOK() throws Exception {
        // Create mock data
        List<ConferenceOutDTO> mockConferences = List.of(
            new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null),
            new ConferenceOutDTO(2L, "Business Summit", 200, 1000.0, false, LocalDate.now(), null, null)
        );
        // Mock service method
        when(conferenceService.getAllConferences()).thenReturn(mockConferences);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<ConferenceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<ConferenceOutDTO> conferencesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferencesResponse);
        assertEquals(2, conferencesResponse.size());
        assertEquals("Tech Conference", conferencesResponse.getFirst().getName());
    }

    @Test
    public void testGetAllConferencesWithFiltersReturnOK() throws Exception {
        // Create mock data
        List<ConferenceOutDTO> mockConferences = List.of(
            new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null),
            new ConferenceOutDTO(2L, "Business Summit", 200, 1000.0, true, LocalDate.now(), null, null)
        );
        // Mock service method
        when(conferenceService.getConferencesByFilters(100, 200, true)).thenReturn(mockConferences);
        // Perform the GET request with filters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences")
                .param("minCapacity", "100")
                .param("maxCapacity", "200")
                .param("isOnline", "true"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<ConferenceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<ConferenceOutDTO> conferencesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferencesResponse);
        assertEquals(2, conferencesResponse.size());
        assertEquals("Tech Conference", conferencesResponse.getFirst().getName());
    }

    @Test
    public void testGetAllConferencesReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid parameters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences")
                .param("minCapacity", "invalid"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // getConferenceById
    @Test
    public void testGetConferenceByIdReturnOK() throws Exception {
        // Create mock data
        ConferenceOutDTO mockConference = new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null);
        // Mock service method
        when(conferenceService.getConferenceById(1L)).thenReturn(Optional.of(mockConference));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to ConferenceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        ConferenceOutDTO conferenceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferenceResponse);
        assertEquals("Tech Conference", conferenceResponse.getName());
    }

    @Test
    public void testGetConferenceByIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.getConferenceById(1L)).thenThrow(new EntityNotFoundException("Conference not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetConferenceByIdReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid ID
        mockMvc.perform(MockMvcRequestBuilders.get("/conferences/invalid"))
            .andExpect(status().isInternalServerError());
    }

    // createConference
    @Test
    public void testCreateConferenceReturnCreated() throws Exception {
        // Create mock data
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("Tech Conference", 100, 1000.0, true, LocalDate.now(), 1L, 1L);
        ConferenceOutDTO mockConferenceDTO = new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null);
        // Mock service method
        when(conferenceService.createConference(any())).thenReturn(mockConferenceDTO);
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/conferences")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isCreated())
            .andReturn();
        // Convert response to ConferenceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        ConferenceOutDTO conferenceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferenceResponse);
        assertEquals("Tech Conference", conferenceResponse.getName());
    }

    @Test
    public void testCreateConferenceWithInvalidDataReturnBadRequest() throws Exception {
        // Create mock data
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("Tech Conference", -100, 1000.0, true, LocalDate.now(), 1L, 1L);
        // Perform the POST request with invalid data (negative capacity)
        mockMvc.perform(MockMvcRequestBuilders.post("/conferences")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateConferenceReturnInternalServerError() throws Exception {
        // Create mock data
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("Tech Conference", 100, 1000.0, true, LocalDate.now(), 1L, 28L);
        // Mock service method
        when(conferenceService.createConference(any())).thenThrow(new EntityNotFoundException("Organizer not found with id: 28"));
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/conferences")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    // updateConference
    @Test
    public void testUpdateConferenceReturnAccepted() throws Exception {
        // Create mock data
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("Tech Conference", 100, 1000.0, true, LocalDate.now(), 1L, 1L);
        ConferenceOutDTO mockConferenceOutDTO = new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null);
        // Mock service method
        when(conferenceService.updateConference(anyLong(), any())).thenReturn(mockConferenceOutDTO);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/conferences/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to ConferenceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        ConferenceOutDTO conferenceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferenceResponse);
        assertEquals("Tech Conference", conferenceResponse.getName());
    }

    @Test
    public void testUpdateConferenceReturnNotFound() throws Exception {
        // Mock service method to throw exception
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("Tech Conference", 100, 1000.0, true, LocalDate.now(), 1L, 1L);
        when(conferenceService.updateConference(anyLong(), any())).thenThrow(new EntityNotFoundException("Conference not found with id: 1"));
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/conferences/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
        assertEquals("Conference not found with id: 1", errorResponse.getMessage());
    }

    @Test
    public void testUpdateConferenceReturnBadRequest() throws Exception {
        // Create mock data
        ConferenceInDTO mockConferenceInDTO = new ConferenceInDTO("", -1, 1000.0, true, LocalDate.now(), 1L, 1L);
        // Perform the PUT request with invalid data
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/conferences/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockConferenceInDTO)))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    // partialUpdateConference
    @Test
    public void testPartialUpdateConferenceReturnAccepted() throws Exception {
        // Create mock data
        ConferenceOutDTO mockConference = new ConferenceOutDTO(1L, "Partially Updated", 100, 1000.0, true, LocalDate.now(), null, null);
        // Mock service method
        when(conferenceService.partialUpdateConference(anyLong(), any())).thenReturn(mockConference);
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/conferences/1")
                .contentType("application/json")
                .content("{\"name\":\"Partially Updated\"}"))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to ConferenceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        ConferenceOutDTO conferenceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferenceResponse);
        assertEquals("Partially Updated", conferenceResponse.getName());
    }

    @Test
    public void testPartialUpdateConferenceReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.partialUpdateConference(anyLong(), any())).thenThrow(new EntityNotFoundException("Conference not found"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/conferences/1")
                .contentType("application/json")
                .content("{\"name\":\"Partially Updated\"}"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testPartialUpdateConferenceReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.partialUpdateConference(anyLong(), any())).thenThrow(new IllegalArgumentException("Capacity must be greater than 0"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/conferences/1")
                .contentType("application/json")
                .content("{\"capacity\":-1}"))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    // deleteConference
    @Test
    public void testDeleteConferenceReturnAccepted() throws Exception {
        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/conferences/1"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void testDeleteConferenceReturnInternalServerError() throws Exception {
        // Mock service method to throw exception
        doThrow(new RuntimeException("Database error")).when(conferenceService).deleteConference(1L);
        // Perform the DELETE request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/conferences/1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // getConferencesByPersonId
    @Test
    public void testGetConferencesByPersonIdReturnOK() throws Exception {
        // Create mock data
        List<ConferenceOutDTO> mockConferences = List.of(
            new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, null)
        );
        // Mock service method
        when(conferenceService.getConferencesByPersonId(1L)).thenReturn(mockConferences);
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/persons/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<ConferenceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<ConferenceOutDTO> conferencesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferencesResponse);
        assertEquals(1, conferencesResponse.size());
        assertEquals("Tech Conference", conferencesResponse.getFirst().getName());
    }

    @Test
    public void testGetConferencesByPersonIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.getConferencesByPersonId(1L)).thenThrow(new EntityNotFoundException("Person not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/persons/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetConferencesByPersonIdReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.getConferencesByPersonId(-1L)).thenThrow(new IllegalArgumentException("Invalid person ID: -1"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/persons/-1"))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    // getConferencesByOrganizerId
    @Test
    public void testGetConferencesByOrganizerIdReturnOK() throws Exception {
        // Create mock data
        PersonOutDTO mockOrganizer = new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.es", 28, 1.75, "Tech", LocalDate.of(1996, 4, 29), true);
        List<ConferenceOutDTO> mockConferences = List.of(
            new ConferenceOutDTO(1L, "Tech Conference", 100, 1000.0, true, LocalDate.now(), null, mockOrganizer),
            new ConferenceOutDTO(2L, "Business Summit", 200, 1000.0, false, LocalDate.now(), null, mockOrganizer)
        );
        // Mock service method
        when(conferenceService.getConferencesByOrganizerId(1L)).thenReturn(mockConferences);
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/organizers/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<ConferenceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<ConferenceOutDTO> conferencesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(conferencesResponse);
        assertEquals(2, conferencesResponse.size());
        assertEquals("Tech Conference", conferencesResponse.getFirst().getName());
    }

    @Test
    public void testGetConferencesByOrganizerIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.getConferencesByOrganizerId(1L)).thenThrow(new EntityNotFoundException("Organizer not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/organizers/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetConferencesByOrganizerIdReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(conferenceService.getConferencesByOrganizerId(-1L)).thenThrow(new IllegalArgumentException("Invalid organizer ID: -1"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/conferences/organizers/-1"))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }
}