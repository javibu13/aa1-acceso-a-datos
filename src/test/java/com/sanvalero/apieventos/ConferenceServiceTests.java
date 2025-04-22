package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.sanvalero.apieventos.domain.*;
import com.sanvalero.apieventos.dto.*;
import com.sanvalero.apieventos.repository.*;
import com.sanvalero.apieventos.service.ConferenceService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConferenceServiceTests {

    @InjectMocks
    private ConferenceService conferenceService;

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testGetAllConferences() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference1 = new Conference(1L, "Conf 1", 100, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        Conference conference2 = new Conference(2L, "Conf 2", 200, 2000.0, true, LocalDate.now().plusDays(2), place, organizer);
        // Mock the repository and model mapper
        when(conferenceRepository.findAll()).thenReturn(List.of(conference1, conference2));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getAllConferences();
        // Check the result
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        verify(conferenceRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Conference.class), eq(ConferenceOutDTO.class));
        verify(modelMapper, times(2)).map(any(Person.class), eq(PersonOutDTO.class));
    }

    @Test
    public void testGetConferencesByFiltersMinCapacity() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 200, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mocking the repository and model mapper
        when(conferenceRepository.findByCapacityGreaterThanEqual(200)).thenReturn(List.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByFilters(200, null, null);
        // Check the result
        assertEquals(1, result.size());
        // Check the first conference
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        verify(conferenceRepository, times(1)).findByCapacityGreaterThanEqual(200);
    }

    @Test
    public void testGetConferencesByFiltersMaxCapacity() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 200, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mocking the repository and model mapper
        when(conferenceRepository.findByCapacityLessThanEqual(200)).thenReturn(List.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByFilters(null, 200, null);
        // Check the result
        assertEquals(1, result.size());
        // Check the first conference
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        verify(conferenceRepository, times(1)).findByCapacityLessThanEqual(200);
    }

    @Test
    public void testGetConferencesByFiltersOnline() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 200, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mocking the repository and model mapper
        when(conferenceRepository.findByOnline(false)).thenReturn(List.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByFilters(null, null, false);
        // Check the result
        assertEquals(1, result.size());
        // Check the first conference
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        verify(conferenceRepository, times(1)).findByOnline(false);
    }

    @Test
    public void testGetConferencesByFiltersMinCapacityMaxCapacityOnline() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 200, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mocking the repository and model mapper
        when(conferenceRepository.findByCapacityBetweenAndOnline(100, 300, false)).thenReturn(List.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByFilters(100, 300, false);
        // Check the result
        assertEquals(1, result.size());
        // Check the first conference
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        verify(conferenceRepository, times(1)).findByCapacityBetweenAndOnline(100, 300, false);
    }

    @Test
    public void testGetConferenceById() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 100, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mock the repository and model mapper
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        Optional<ConferenceOutDTO> result = conferenceService.getConferenceById(1L);
        // Check the result
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(conferenceRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetConferenceByIdNotFound() {
        // Mock the repository to return an empty Optional
        when(conferenceRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method to test and expect an exception
        try {
            conferenceService.getConferenceById(1L);
        } catch (EntityNotFoundException e) {
            // Check the exception message
            assertEquals("Conference not found with id: 1", e.getMessage());
            // Check the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        // Verify that the repository was called
        verify(conferenceRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateConference() {
        // Create test data
        ConferenceInDTO inDTO = new ConferenceInDTO("New Conf", 100, 1000.0, false, LocalDate.now().plusDays(1), 1L, 1L);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "New Conf", 100, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mock the repository and model mapper
        when(personRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        // General mock for modelMapper
        when(modelMapper.map(inDTO, Conference.class)).thenReturn(conference);
        when(conferenceRepository.save(conference)).thenReturn(conference);
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        ConferenceOutDTO result = conferenceService.createConference(inDTO);
        // Check the result
        assertNotNull(result);
        assertEquals("New Conf", result.getName());
        assertEquals(100, result.getCapacity());
        verify(personRepository, times(1)).findById(1L);
        verify(placeRepository, times(1)).findById(1L);
        verify(conferenceRepository, times(1)).save(conference);
    }

    @Test
    public void testCreateConferenceOrganizerNotFound() {
        // Create test data
        ConferenceInDTO inDTO = new ConferenceInDTO();
        inDTO.setOrganizer(1L);
        // Mock the repository to return an empty Optional for the organizer        
        when(personRepository.findById(1L)).thenReturn(Optional.empty());
        // Call the method to test and expect an exception
        try {
            conferenceService.createConference(inDTO);
        } catch (EntityNotFoundException e) {
            // Check the exception message
            assertEquals("Organizer not found with id: 1", e.getMessage());
            // Check the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        // Verify that the repository was called
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateConference() {
        // Create test data
        ConferenceInDTO inDTO = new ConferenceInDTO("Updated Conf", 150, 1500.0, true, LocalDate.now().plusDays(2), 1L, 1L);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Updated Conf", 150, 1500.0, true, LocalDate.now().plusDays(2), place, organizer);
        // Mock the repository and model mapper
        when(conferenceRepository.existsById(1L)).thenReturn(true);
        when(personRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(modelMapper.map(inDTO, Conference.class)).thenReturn(conference);
        when(conferenceRepository.save(conference)).thenReturn(conference);
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        ConferenceOutDTO result = conferenceService.updateConference(1L, inDTO);
        // Check the result
        assertNotNull(result);
        verify(personRepository, times(1)).findById(1L);
        verify(placeRepository, times(1)).findById(1L);
        verify(conferenceRepository, times(1)).save(any(Conference.class));
    }

    @Test
    public void testPartialUpdateConference() {
        // Create test data
        ConferenceInDTO inDTO = new ConferenceInDTO("Updated Conf", 150, 1500.0, true, LocalDate.now().plusDays(2), 1L, 1L);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Updated Conf", 150, 1500.0, true, LocalDate.now().plusDays(2), place, organizer);
        // Mock the repository and model mapper
        when(conferenceRepository.existsById(1L)).thenReturn(true);
        when(personRepository.findById(1L)).thenReturn(Optional.of(organizer));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(modelMapper.map(inDTO, Conference.class)).thenReturn(conference);
        when(conferenceRepository.save(conference)).thenReturn(conference);
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        ConferenceOutDTO result = conferenceService.updateConference(1L, inDTO);
        // Check the result
        assertNotNull(result);
        verify(conferenceRepository, times(1)).save(any(Conference.class));
    }

    @Test
    public void testDeleteConference() {
        // Not needed for this test case
    }

    @Test
    public void testGetConferencesByPersonId() {
        // Create test data
        Person person = new Person(1L, "Attendee", "Test", "att@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Person organizer = new Person(2L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 100, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        Attendance attendance = new Attendance(1L, "CODE123", 1, 5.0, false, null, person, conference);
        // Mock the repository and model mapper
        when(personRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.findByPersonId(1L)).thenReturn(List.of(attendance));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByPersonId(1L);
        // Check the result
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("org@test.com", result.getFirst().getOrganizer().getEmail());
        verify(attendanceRepository, times(1)).findByPersonId(1L);
    }

    @Test
    public void testGetConferencesByOrganizerId() {
        // Create test data
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Conf 1", 100, 1000.0, false, LocalDate.now().plusDays(1), place, organizer);
        // Mock the repository and model mapper
        when(personRepository.existsById(1L)).thenReturn(true);
        when(conferenceRepository.findByOrganizerId(1L)).thenReturn(List.of(conference));
        // General mock for modelMapper
        when(modelMapper.map(any(Conference.class), eq(ConferenceOutDTO.class))).thenAnswer(invocation -> {
            Conference c = invocation.getArgument(0);
            ConferenceOutDTO dto = new ConferenceOutDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setCapacity(c.getCapacity());
            dto.setBudget(c.getBudget());
            dto.setOnline(c.getOnline());
            dto.setStartDate(c.getStartDate());
            dto.setPlace(c.getPlace());
            return dto;
        });
        when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            PersonOutDTO dto = new PersonOutDTO();
            dto.setId(p.getId());
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setEmail(p.getEmail());
            dto.setAge(p.getAge());
            dto.setHeight(p.getHeight());
            dto.setInterests(p.getInterests());
            dto.setBirthDate(p.getBirthDate());
            dto.setVerified(p.getVerified());
            return dto;
            });
        // Call the method to test
        List<ConferenceOutDTO> result = conferenceService.getConferencesByOrganizerId(1L);
        // Check the result
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Conf 1", result.getFirst().getName());
        assertEquals("org@test.com", result.getFirst().getOrganizer().getEmail());
        verify(conferenceRepository, times(1)).findByOrganizerId(1L);
    }
}