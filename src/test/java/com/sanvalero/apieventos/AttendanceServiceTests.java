package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.sanvalero.apieventos.domain.*;
import com.sanvalero.apieventos.dto.*;
import com.sanvalero.apieventos.repository.*;
import com.sanvalero.apieventos.service.AttendanceService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTests {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testGetAllAttendances() {
        // Create test data
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance attendance1 = new Attendance(1L, "CODE1", 10, 50.0, false, LocalDateTime.now(), person, conference);
        Attendance attendance2 = new Attendance(2L, "CODE2", 20, 75.0, true, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findAll()).thenReturn(List.of(attendance1, attendance2));
        when(modelMapper.map(any(Attendance.class), eq(AttendanceOutDTO.class))).thenAnswer(invocation -> {
            Attendance a = invocation.getArgument(0);
            AttendanceOutDTO dto = new AttendanceOutDTO();
            dto.setId(a.getId());
            dto.setTicketCode(a.getTicketCode());
            dto.setSeatNumber(a.getSeatNumber());
            dto.setTicketPrice(a.getTicketPrice());
            dto.setCheckedIn(a.getCheckedIn());
            dto.setRegistrationDate(a.getRegistrationDate());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenReturn(new PersonOutDTO());
        // Call the method to test
        List<AttendanceOutDTO> result = attendanceService.getAllAttendances();
        // Verify the results
        assertEquals(2, result.size());
        verify(attendanceRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Attendance.class), eq(AttendanceOutDTO.class));
    }

    @Test
    public void testGetAttendancesByFiltersMinSeatNumber() {
        // Create test data
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance attendance = new Attendance(1L, "CODE1", 15, 50.0, false, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findBySeatNumberGreaterThanEqual(10)).thenReturn(List.of(attendance));
        when(modelMapper.map(any(Attendance.class), eq(AttendanceOutDTO.class))).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenReturn(new PersonOutDTO());
        // Call the method to test
        List<AttendanceOutDTO> result = attendanceService.getAttendancesByFilters(10, null, null);
        // Verify the results
        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findBySeatNumberGreaterThanEqual(10);
    }

    @Test
    public void testGetAttendancesByFiltersMinSeatNumberMaxSeatNumberCheckedIn() {
        // Create test data
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance attendance = new Attendance(1L, "CODE1", 15, 50.0, true, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findBySeatNumberBetweenAndCheckedIn(10, 20, true)).thenReturn(List.of(attendance));
        when(modelMapper.map(any(Attendance.class), eq(AttendanceOutDTO.class))).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenReturn(new PersonOutDTO());
        // Call the method to test
        List<AttendanceOutDTO> result = attendanceService.getAttendancesByFilters(10, 20, true);
        // Verify the results
        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findBySeatNumberBetweenAndCheckedIn(10, 20, true);
    }

    @Test
    public void testGetAttendanceById() {
        // Create test data
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance attendance = new Attendance(1L, "CODE1", 10, 50.0, false, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(modelMapper.map(any(Attendance.class), eq(AttendanceOutDTO.class))).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenReturn(new PersonOutDTO());
        // Call the method to test
        Optional<AttendanceOutDTO> result = attendanceService.getAttendanceById(1L);
        // Verify the results
        assertTrue(result.isPresent());
        verify(attendanceRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateAttendance() {
        // Create test data
        AttendanceInDTO inDTO = new AttendanceInDTO();
        inDTO.setSeatNumber(10);
        inDTO.setTicketPrice(50.0);
        inDTO.setCheckedIn(false);
        inDTO.setPerson(1L);
        inDTO.setConference(1L);
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance attendance = new Attendance();
        attendance.setTicketCode(UUID.randomUUID().toString());
        attendance.setRegistrationDate(LocalDateTime.now());
        attendance.setSeatNumber(10);
        attendance.setTicketPrice(50.0);
        attendance.setCheckedIn(false);
        attendance.setPerson(person);
        attendance.setConference(conference);
        // Mock repository methods
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(modelMapper.map(inDTO, Attendance.class)).thenReturn(attendance);
        when(attendanceRepository.save(attendance)).thenReturn(attendance);
        when(modelMapper.map(attendance, AttendanceOutDTO.class)).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(person, PersonOutDTO.class)).thenReturn(new PersonOutDTO());
        // Call the method to test
        AttendanceOutDTO result = attendanceService.createAttendance(inDTO);
        // Verify the results
        assertNotNull(result);
        verify(attendanceRepository, times(1)).save(attendance);
    }

    @Test
    public void testCreateAttendanceConferenceNotFound() {
        // Create test data
        AttendanceInDTO inDTO = new AttendanceInDTO();
        inDTO.setConference(1L);
        // Mock repository methods
        when(conferenceRepository.findById(1L)).thenReturn(Optional.empty());
        // Call the method to test and expect an exception
        try {
            attendanceService.createAttendance(inDTO);
            fail("Expected EntityNotFoundException to be thrown");
        } catch (EntityNotFoundException e) {
            // Check the exception message
            assertEquals("Conference not found", e.getMessage());
            // Check the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        // Verify that the repository method was called
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    public void testUpdateAttendance() {
        // Create test data
        AttendanceInDTO inDTO = new AttendanceInDTO();
        inDTO.setSeatNumber(15);
        inDTO.setTicketPrice(75.0);
        inDTO.setCheckedIn(true);
        inDTO.setPerson(1L);
        inDTO.setConference(1L);
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance existingAttendance = new Attendance(1L, "CODE1", 10, 50.0, false, LocalDateTime.now(), person, conference);
        Attendance updatedAttendance = new Attendance(1L, "CODE1", 15, 75.0, true, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existingAttendance));
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(modelMapper.map(inDTO, Attendance.class)).thenReturn(updatedAttendance);
        when(attendanceRepository.save(updatedAttendance)).thenReturn(updatedAttendance);
        when(modelMapper.map(updatedAttendance, AttendanceOutDTO.class)).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(person, PersonOutDTO.class)).thenReturn(new PersonOutDTO());
        // Call the method to test
        AttendanceOutDTO result = attendanceService.updateAttendance(1L, inDTO);
        // Verify the results
        assertNotNull(result);
        verify(attendanceRepository, times(1)).save(updatedAttendance);
    }

    @Test
    public void testPartialUpdateAttendance() {
        // Create test data
        AttendanceInDTO inDTO = new AttendanceInDTO();
        inDTO.setSeatNumber(15);
        inDTO.setCheckedIn(true);
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance existingAttendance = new Attendance(1L, "CODE1", 10, 50.0, false, LocalDateTime.now(), person, conference);
        Attendance updatedAttendance = new Attendance(1L, "CODE1", 15, 50.0, true, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(updatedAttendance);
        when(modelMapper.map(updatedAttendance, AttendanceOutDTO.class)).thenReturn(new AttendanceOutDTO());
        when(modelMapper.map(person, PersonOutDTO.class)).thenReturn(new PersonOutDTO());
        // Call the method to test
        AttendanceOutDTO result = attendanceService.partialUpdateAttendance(1L, inDTO);
        // Verify the results
        assertNotNull(result);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    public void testPartialUpdateAttendanceInvalidData() {
        // Create test data
        AttendanceInDTO inDTO = new AttendanceInDTO();
        inDTO.setSeatNumber(-1); // Invalid seat number
        Person person = new Person(1L, "John", "Doe", "john@test.com", "pass", 30, 1.75, "Tech", LocalDateTime.now().toLocalDate(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Attendance existingAttendance = new Attendance(1L, "CODE1", 10, 50.0, false, LocalDateTime.now(), person, conference);
        // Mock repository methods
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existingAttendance));
        // Call the method to test and expect an exception
        try {
            attendanceService.partialUpdateAttendance(1L, inDTO);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // Check the exception message
            assertEquals("Invalid seat number", e.getMessage());
            // Check the exception type
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        // Verify that the repository method was not called
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    public void testDeleteAttendance() {
        // Not needed for this test case
    }
}