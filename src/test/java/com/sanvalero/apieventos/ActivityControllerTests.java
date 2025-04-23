package com.sanvalero.apieventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanvalero.apieventos.controller.ActivityController;
import com.sanvalero.apieventos.domain.Activity;
import com.sanvalero.apieventos.dto.ActivityInDTO;
import com.sanvalero.apieventos.exception.ErrorResponse;
import com.sanvalero.apieventos.service.ActivityService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ActivityController.class)
public class ActivityControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ActivityService activityService;

    // getAllActivities
    @Test
    public void testGetAllActivitiesNoFiltersReturnOK() throws Exception {
        // Create mock data
        List<Activity> mockActivities = List.of(
            new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null),
            new Activity(2L, "Activity Title 2", 60, 10.0, true, LocalDateTime.now(), null)
        );
        // Mock service method
        when(activityService.getAllActivities()).thenReturn(mockActivities);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<Activity>
        String jsonResponse = response.getResponse().getContentAsString();
        List<Activity> activitiesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activitiesResponse);
        assertEquals(2, activitiesResponse.size());
        assertEquals("Activity Title", activitiesResponse.getFirst().getTitle());
    }

    @Test
    public void testGetAllActivitiesWithFiltersReturnOK() throws Exception {
        // Create mock data
        List<Activity> mockActivities = List.of(
            new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null),
            new Activity(2L, "Activity Title 2", 60, 10.0, true, LocalDateTime.now(), null)
        );
        // Mock service method
        when(activityService.getActivitiesByFilters(30, 90, true)).thenReturn(mockActivities);
        // Perform the GET request with filters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities")
                .param("minDuration", "30")
                .param("maxDuration", "90")
                .param("isOpen", "true"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<Activity>
        String jsonResponse = response.getResponse().getContentAsString();
        List<Activity> activitiesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activitiesResponse);
        assertEquals(2, activitiesResponse.size());
        assertEquals("Activity Title", activitiesResponse.getFirst().getTitle());
        assertEquals("Activity Title 2", activitiesResponse.getLast().getTitle());
    }

    @Test
    public void testGetAllActivitiesReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid parameters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities")
                .param("minDuration", "invalid"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // getActivityById
    @Test
    public void testGetActivityByIdReturnOK() throws Exception {
        // Create mock data
        Activity mockActivity = new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null);
        // Mock service method
        when(activityService.getActivityById(1L)).thenReturn(Optional.of(mockActivity));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to Activity
        String jsonResponse = response.getResponse().getContentAsString();
        Activity activityResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activityResponse);
        assertEquals("Activity Title", activityResponse.getTitle());
    }

    @Test
    public void testGetActivityByIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(activityService.getActivityById(1L)).thenThrow(new EntityNotFoundException("Activity not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetActivityByIdReturnInternalServerError() throws Exception {
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/1.1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // createActivity
    @Test
    public void testCreateActivityReturnCreated() throws Exception {
        // Create mock data
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", 60, 10.0, true, LocalDateTime.now(), 1L);
        Activity mockActivity = new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null);
        // Mock service method
        when(activityService.createActivity(any())).thenReturn(mockActivity);
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/activities")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isCreated())
            .andReturn();
        // Convert response to Activity
        String jsonResponse = response.getResponse().getContentAsString();
        Activity activityResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activityResponse);
        assertEquals("Activity Title", activityResponse.getTitle());
    }

    @Test
    public void testCreateActivityReturnBadRequest() throws Exception {
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", 60, 10.0, true, LocalDateTime.now(), null);
        // Perform the POST request with invalid data (negative duration)
        mockMvc.perform(MockMvcRequestBuilders.post("/activities")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateActivityReturnInternalServerError() throws Exception {
        // Create mock data
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", 60, 10.0, true, LocalDateTime.now(), 1L);
        // Mock service method
        when(activityService.createActivity(any())).thenThrow(new RuntimeException("Database error"));
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/activities")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // updateActivity
    @Test
    public void testUpdateActivityReturnAccepted() throws Exception {
        // Create mock data
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", 60, 10.0, true, LocalDateTime.now(), 1L);
        Activity mockActivity = new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null);
        // Mock service method
        when(activityService.updateActivity(anyLong(), any())).thenReturn(mockActivity);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/activities/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to Activity
        String jsonResponse = response.getResponse().getContentAsString();
        Activity activityResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activityResponse);
        assertEquals("Activity Title", activityResponse.getTitle());
    }

    @Test
    public void testUpdateActivityReturnNotFound() throws Exception {
        // Mock service method to throw exception
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", 60, 10.0, true, LocalDateTime.now(), 1L);
        when(activityService.updateActivity(anyLong(), any())).thenThrow(new EntityNotFoundException("Activity not found"));
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/activities/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testUpdateActivityReturnBadRequest() throws Exception {
        // Create mock data with invalid duration
        ActivityInDTO mockActivityInDTO = new ActivityInDTO("Activity Title", -60, 10.0, true, LocalDateTime.now(), 1L);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/activities/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockActivityInDTO)))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    // partialUpdateActivity
    @Test
    public void testPartialUpdateActivityReturnAccepted() throws Exception {
        // Create mock data
        Activity mockActivity = new Activity(1L, "Partially Updated", 60, 10.0, true, LocalDateTime.now(), null);
        // Mock service method
        when(activityService.partialUpdateActivity(anyLong(), any())).thenReturn(mockActivity);
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/activities/1")
                .contentType("application/json")
                .content("{\"title\":\"Partially Updated\"}"))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to Activity
        String jsonResponse = response.getResponse().getContentAsString();
        Activity activityResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activityResponse);
        assertEquals("Partially Updated", activityResponse.getTitle());
    }

    @Test
    public void testPartialUpdateActivityReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(activityService.partialUpdateActivity(anyLong(), any())).thenThrow(new IllegalArgumentException("Duration must be greater than 0"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/activities/1")
                .contentType("application/json")
                .content("{\"duration\":-1}"))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    @Test
    public void testPartialUpdateActivityReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(activityService.partialUpdateActivity(anyLong(), any())).thenThrow(new EntityNotFoundException("Activity not found with ID: 9"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/activities/9")
                .contentType("application/json")
                .content("{\"duration\":1}"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    // deleteActivity
    @Test
    public void testDeleteActivityReturnAccepted() throws Exception {
        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/activities/1"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void testDeleteActivityReturnInternalServerError() throws Exception {
        // Mock service method to throw exception
        doThrow(new RuntimeException("Database error")).when(activityService).deleteActivity(1L);
        // Perform the DELETE request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/activities/1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // getActivitiesByConferenceId
    @Test
    public void testGetActivitiesByConferenceIdReturnOK() throws Exception {
        // Create mock data
        List<Activity> mockActivities = List.of(
            new Activity(1L, "Activity Title", 60, 10.0, true, LocalDateTime.now(), null),
            new Activity(2L, "Activity Title 2", 60, 10.0, true, LocalDateTime.now(), null)
        );
        // Mock service method
        when(activityService.getActivitiesByConferenceId(1L)).thenReturn(mockActivities);
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/conferences/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<Activity>
        String jsonResponse = response.getResponse().getContentAsString();
        List<Activity> activitiesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(activitiesResponse);
        assertEquals(2, activitiesResponse.size());
        assertEquals("Activity Title", activitiesResponse.getFirst().getTitle());
    }

    @Test
    public void testGetActivitiesByConferenceIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(activityService.getActivitiesByConferenceId(1L)).thenThrow(new EntityNotFoundException("Conference not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/conferences/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetActivitiesByConferenceIdReturnInternalServerError() throws Exception {
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/activities/conferences/1.1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }
}