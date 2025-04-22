package com.sanvalero.apieventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanvalero.apieventos.controller.PlaceController;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.exception.ErrorResponse;
import com.sanvalero.apieventos.service.PlaceService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

@WebMvcTest(PlaceController.class)
public class PlaceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PlaceService placeService;

    // getAllPlaces
    @Test
    public void testGetAllPlacesNoParametersReturnOK() throws Exception {
        // Create mock data
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), false, "Whiteboard, Chairs"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock service method
        when(placeService.getAllPlaces()).thenReturn(mockPlaces);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<Place>
        String jsonResponse = response.getResponse().getContentAsString();
        List<Place> placesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placesResponse);
        assertEquals(3, placesResponse.size());
        assertEquals("Place 1", placesResponse.getFirst().getName());
    }

    @Test
    public void testGetAllPlacesWithParametersReturnOK() throws Exception {
        // Create mock data
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), true, "Whiteboard, Chairs"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock service method
        when(placeService.getPlacesByFilters(0, 400, true)).thenReturn(mockPlaces);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places")
            .param("minCapacity", "0")
            .param("maxCapacity", "400")
            .param("hasParking", "true"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<Place>
        String jsonResponse = response.getResponse().getContentAsString();
        List<Place> placesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placesResponse);
        assertEquals(3, placesResponse.size());
        assertEquals("Place 1", placesResponse.getFirst().getName());
    }

    @Test
    public void testGetAllPlacesWithParametersReturnInternalServerError() throws Exception {
        // Create mock data
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), true, "Whiteboard, Chairs"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock service method
        when(placeService.getPlacesByFilters(0, 400, true)).thenReturn(mockPlaces);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places")
            .param("minCapacity", "0")
            .param("maxCapacity", "a")  // Invalid parameter to trigger error
            .param("hasParking", "true"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
    }

    // getPlaceById
    @Test
    public void testGetPlaceByIdReturnOK() throws Exception {
        // Create mock data
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method
        when(placeService.getPlaceById(1L)).thenReturn(Optional.of(mockPlace));
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to Place
        String jsonResponse = response.getResponse().getContentAsString();
        Place placeResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placeResponse);
        assertEquals("Place 1", placeResponse.getName());
    }

    @Test
    public void testGetPlaceByIdReturnInternalServerError() throws Exception {
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places/a"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
        assertEquals("An unexpected internal error occurred", errorResponse.getMessage());
    }

    @Test
    public void testGetPlaceByIdReturnNotFound() throws Exception {
        // Mock service method to return empty Optional
        when(placeService.getPlaceById(1L)).thenReturn(Optional.empty());
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/places/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
        assertEquals("Place not found", errorResponse.getMessage());
    }

    // createPlace
    @Test
    public void testCreatePlaceReturnCreated() throws Exception {
        // Create mock data
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method
        when(placeService.createPlace(mockPlace)).thenReturn(mockPlace);
        // Perform the POST request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/places")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isCreated())
            .andReturn();
        // Convert response to Place
        String jsonResponse = response.getResponse().getContentAsString();
        Place placeResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placeResponse);
        assertEquals(1L, placeResponse.getId());
        assertEquals("Place 1", placeResponse.getName());
    }

    @Test
    public void testCreatePlaceReturnBadRequest() throws Exception {
        // Create mock data with invalid capacity
        Place mockPlace = new Place(1L, "Place 1", "Address 1", -100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Perform the POST request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/places")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isBadRequest())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    @Test
    public void testCreatePlaceReturnInternalServerError() throws Exception {
        // Create mock data with invalid capacity
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method to throw exception simulating database connection error
        when(placeService.createPlace(mockPlace)).thenThrow(new RuntimeException("Database connection error"));
        // Perform the POST request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/places")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isInternalServerError())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
        assertEquals("An unexpected internal error occurred", errorResponse.getMessage());
    }

    // updatePlace
    @Test
    public void testUpdatePlaceReturnAccepted() throws Exception {
        // Create mock data
        Long placeId = 1L;
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method
        when(placeService.updatePlace(placeId, mockPlace)).thenReturn(mockPlace);
        // Perform the PUT request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to Place
        String jsonResponse = response.getResponse().getContentAsString();
        Place placeResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placeResponse);
        assertEquals(1L, placeResponse.getId());
        assertEquals("Place 1", placeResponse.getName());
    }

    @Test
    public void testUpdatePlaceReturnNotFound() throws Exception {
        // Create mock data
        Long placeId = 1L;
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method to throw exception simulating entity not found
        when(placeService.updatePlace(placeId, mockPlace)).thenThrow(new EntityNotFoundException("Place not found"));
        // Perform the PUT request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isNotFound())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
        assertEquals("Place not found", errorResponse.getMessage());
    }

    @Test
    public void testUpdatePlaceReturnBadRequest() throws Exception {
        // Create mock data with invalid capacity
        Place mockPlace = new Place(1L, "Place 1", "Address 1", -100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Perform the PUT request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isBadRequest())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    // partialUpdatePlace
    @Test
    public void testPartialUpdatePlaceReturnAccepted() throws Exception {
        // Create mock data
        Long placeId = 1L;
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method
        when(placeService.partialUpdatePlace(placeId, mockPlace)).thenReturn(mockPlace);
        // Perform the PATCH request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to Place
        String jsonResponse = response.getResponse().getContentAsString();
        Place placeResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(placeResponse);
        assertEquals(1L, placeResponse.getId());
        assertEquals("Place 1", placeResponse.getName());
    }

    @Test
    public void testPartialUpdatePlaceReturnNotFound() throws Exception {
        // Create mock data
        Long placeId = 1L;
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method to throw exception simulating entity not found
        when(placeService.partialUpdatePlace(placeId, mockPlace)).thenThrow(new EntityNotFoundException("Place not found"));
        // Perform the PATCH request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isNotFound())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
        assertEquals("Place not found", errorResponse.getMessage());
    }

    @Test
    public void testPartialUpdatePlaceReturnBadRequest() throws Exception {
        // Create mock data with invalid capacity
        Place mockPlace = new Place(1L, "Place 1", "Address 1", -100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock service method to throw exception simulating illegal argument
        when(placeService.partialUpdatePlace(1L, mockPlace)).thenThrow(new IllegalArgumentException("Capacity must be greater than 0"));
        // Perform the PATCH request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/places/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockPlace)))
            .andExpect(status().isBadRequest())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    // deletePlace
    @Test
    public void testDeletePlaceReturnAccepted() throws Exception {
        // Perform the DELETE request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/places/1"))
            .andExpect(status().isAccepted())
            .andReturn();
        // Verify the response status
        assertEquals(202, response.getResponse().getStatus());
    }

    @Test
    public void testDeletePlaceReturnInternalServerError() throws Exception {
        // Mock service method to throw exception simulating database connection error
        doThrow(new RuntimeException("Database connection error")).when(placeService).deletePlace(1L);
        // Perform the DELETE request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/places/1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }
}
