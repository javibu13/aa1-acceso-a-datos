package com.sanvalero.apieventos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanvalero.apieventos.controller.AttendanceController;
import com.sanvalero.apieventos.dto.AttendanceInDTO;
import com.sanvalero.apieventos.dto.AttendanceOutDTO;
import com.sanvalero.apieventos.exception.ErrorResponse;
import com.sanvalero.apieventos.service.AttendanceService;

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

@WebMvcTest(AttendanceController.class)
public class AttendanceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AttendanceService attendanceService;

    // getAllAttendances
    @Test
    public void testGetAllAttendancesNoFiltersReturnOK() throws Exception {
        // Create mock data
        List<AttendanceOutDTO> mockAttendances = List.of(
            new AttendanceOutDTO(1L, "TICKET-CODE-1", 1, 2.50, false, LocalDateTime.now(), null, null),
            new AttendanceOutDTO(2L, "TICKET-CODE-2", 2, 2.50, false, LocalDateTime.now(), null, null)
        );
        // Mock service method
        when(attendanceService.getAllAttendances()).thenReturn(mockAttendances);
        // Perform the GET request and verify the response
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<AttendanceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<AttendanceOutDTO> attendancesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendancesResponse);
        assertEquals(2, attendancesResponse.size());
        assertEquals(1, attendancesResponse.getFirst().getSeatNumber());
    }

    @Test
    public void testGetAllAttendancesWithFiltersReturnOK() throws Exception {
        // Create mock data
        List<AttendanceOutDTO> mockAttendances = List.of(
            new AttendanceOutDTO(1L, "TICKET-CODE-1", 101, 2.50, true, LocalDateTime.now(), null, null)
        );
        // Mock service method
        when(attendanceService.getAttendancesByFilters(100, 200, true)).thenReturn(mockAttendances);
        // Perform the GET request with filters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances")
                .param("minSeatNumber", "100")
                .param("maxSeatNumber", "200")
                .param("isCheckedIn", "true"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to List<AttendanceOutDTO>
        String jsonResponse = response.getResponse().getContentAsString();
        List<AttendanceOutDTO> attendancesResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendancesResponse);
        assertEquals(1, attendancesResponse.size());
        assertEquals(101, attendancesResponse.getFirst().getSeatNumber());
    }

    @Test
    public void testGetAllAttendancesReturnInternalServerError() throws Exception {
        // Perform the GET request with invalid parameters
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances")
                .param("minSeatNumber", "invalid"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // getAttendanceById
    @Test
    public void testGetAttendanceByIdReturnOK() throws Exception {
        // Create mock data
        AttendanceOutDTO mockAttendance = new AttendanceOutDTO(1L, "TICKET-CODE-1", 101, 2.50, false, LocalDateTime.now(), null, null);
        // Mock service method
        when(attendanceService.getAttendanceById(1L)).thenReturn(Optional.of(mockAttendance));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances/1"))
            .andExpect(status().isOk())
            .andReturn();
        // Convert response to AttendanceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        AttendanceOutDTO attendanceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendanceResponse);
        assertEquals(101, attendanceResponse.getSeatNumber());
    }

    @Test
    public void testGetAttendanceByIdReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(attendanceService.getAttendanceById(1L)).thenThrow(new EntityNotFoundException("Attendance not found"));
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances/1"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testGetAttendanceByIdReturnInternalServerError() throws Exception {
        // Perform the GET request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/attendances/1.1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("MethodArgumentTypeMismatchException", errorResponse.getError());
    }

    // createAttendance
    @Test
    public void testCreateAttendanceReturnCreated() throws Exception {
        // Create mock data
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(101, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        AttendanceOutDTO mockAttendanceOutDTO = new AttendanceOutDTO(1L, "TICKET-CODE-1", 101, 2.50, false, LocalDateTime.now(), null, null);
        // Mock service method
        when(attendanceService.createAttendance(any())).thenReturn(mockAttendanceOutDTO);
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/attendances")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isCreated())
            .andReturn();
        // Convert response to AttendanceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        AttendanceOutDTO attendanceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendanceResponse);
        assertEquals(101, attendanceResponse.getSeatNumber());
    }

    @Test
    public void testCreateAttendanceReturnBadRequest() throws Exception {
        // Create mock data
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(-101, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        // Perform the POST request with invalid data (negative seat number)
        mockMvc.perform(MockMvcRequestBuilders.post("/attendances")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAttendanceReturnInternalServerError() throws Exception {
        // Create mock data
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(1, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        // Mock service method
        when(attendanceService.createAttendance(any())).thenThrow(new RuntimeException("Database error"));
        // Perform the POST request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/attendances")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }

    // updateAttendance
    @Test
    public void testUpdateAttendanceReturnAccepted() throws Exception {
        // Create mock data
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(102, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        AttendanceOutDTO mockAttendanceOutDTO = new AttendanceOutDTO(1L, "TICKET-CODE-1", 102, 2.50, false, LocalDateTime.now().plusDays(1), null, null);
        // Mock service method
        when(attendanceService.updateAttendance(anyLong(), any())).thenReturn(mockAttendanceOutDTO);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/attendances/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to AttendanceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        AttendanceOutDTO attendanceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendanceResponse);
        assertEquals(102, attendanceResponse.getSeatNumber());
    }

    @Test
    public void testUpdateAttendanceReturnNotFound() throws Exception {
        // Mock service method to throw exception
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(1, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        when(attendanceService.updateAttendance(anyLong(), any())).thenThrow(new EntityNotFoundException("Attendance not found"));
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/attendances/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testUpdateAttendanceReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        AttendanceInDTO mockAttendanceInDTO = new AttendanceInDTO(-1, 2.50, false, LocalDateTime.now().plusDays(1), 1L, 1L);
        // Perform the PUT request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/attendances/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mockAttendanceInDTO)))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("MethodArgumentNotValidException", errorResponse.getError());
    }

    // partialUpdateAttendance
    @Test
    public void testPartialUpdateAttendanceReturnAccepted() throws Exception {
        // Create mock data
        AttendanceOutDTO mockAttendance = new AttendanceOutDTO(1L, "TICKET-CODE-1", 1, 2.50, true, LocalDateTime.now().plusDays(1), null, null);
        // Mock service method
        when(attendanceService.partialUpdateAttendance(anyLong(), any())).thenReturn(mockAttendance);
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/attendances/1")
                .contentType("application/json")
                .content("{\"checkedIn\":true}"))
            .andExpect(status().isAccepted())
            .andReturn();
        // Convert response to AttendanceOutDTO
        String jsonResponse = response.getResponse().getContentAsString();
        AttendanceOutDTO attendanceResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        // Verify the response data
        assertNotNull(attendanceResponse);
        assertEquals(true, attendanceResponse.getCheckedIn());
    }

    @Test
    public void testPartialUpdateAttendanceReturnNotFound() throws Exception {
        // Mock service method to throw exception
        when(attendanceService.partialUpdateAttendance(anyLong(), any())).thenThrow(new EntityNotFoundException("Attendance not found"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/attendances/1")
                .contentType("application/json")
                .content("{\"seatNumber\":1}"))
            .andExpect(status().isNotFound())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(404, errorResponse.getStatus());
        assertEquals("EntityNotFoundException", errorResponse.getError());
    }

    @Test
    public void testPartialUpdateAttendanceReturnBadRequest() throws Exception {
        // Mock service method to throw exception
        when(attendanceService.partialUpdateAttendance(anyLong(), any())).thenThrow(new IllegalArgumentException("Seat number cannot be negative"));
        // Perform the PATCH request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/attendances/1")
                .contentType("application/json")
                .content("{\"seatNumber\":-1}"))
            .andExpect(status().isBadRequest())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(400, errorResponse.getStatus());
        assertEquals("IllegalArgumentException", errorResponse.getError());
    }

    // deleteAttendance
    @Test
    public void testDeleteAttendanceReturnAccepted() throws Exception {
        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/attendances/1"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void testDeleteAttendanceReturnInternalServerError() throws Exception {
        // Mock service method to throw exception
        doThrow(new RuntimeException("Database error")).when(attendanceService).deleteAttendance(1L);
        // Perform the DELETE request
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/attendances/1"))
            .andExpect(status().isInternalServerError())
            .andReturn();
        // Verify error response
        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});
        assertEquals(500, errorResponse.getStatus());
        assertEquals("RuntimeException", errorResponse.getError());
    }
}