package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.sanvalero.apieventos.domain.Attendance;
import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.AttendanceRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.repository.PersonRepository;
import com.sanvalero.apieventos.service.PersonService;

@ExtendWith(MockitoExtension.class)
class PersonServiceTests {

	@InjectMocks
	private PersonService personService;

	@Mock
	private PersonRepository personRepository;

	@Mock
	private AttendanceRepository attendanceRepository;

	@Mock
	private ConferenceRepository conferenceRepository;

	@Mock
    private ModelMapper modelMapper;

	@Test
	public void testGetAllPersons() {
		// Create a list of persons to be returned by the mock repository
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
			new Person(2L, "Montse", "García", "montse@garcia.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true),
			new Person(3L, "Luna", "Sanz", "luna@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 6, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findAll()).thenReturn(mockPersons);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getAllPersons();
		// Check the size of the result
		assertEquals(3, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		// Check the last person
		assertEquals(3, result.getLast().getId());
		assertEquals("Luna", result.getLast().getFirstName());
		assertEquals("Sanz", result.getLast().getLastName());
		// Verify that the methods were called the expected number of times
		verify(personRepository, times(1)).findAll();
		verify(modelMapper, times(3)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testGetPersonsByFiltersFirstName() {
		// Create a list of persons to be returned by the mock repository
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findByFirstName("Javier")).thenReturn(mockPersons);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getPersonsByFilters("Javier", null, null);
		// Check the size of the result
		assertEquals(1, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		// Verify that the methods were called the expected number of times
		verify(personRepository, times(1)).findByFirstName(anyString());
		verify(personRepository, times(0)).findByAge(anyInt());
		verify(personRepository, times(0)).findByVerified(anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAge(anyString(), anyInt());
		verify(personRepository, times(0)).findByFirstNameAndVerified(anyString(), anyBoolean());
		verify(personRepository, times(0)).findByAgeAndVerified(anyInt(), anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAgeAndVerified(anyString(), anyInt(), anyBoolean());
		verify(personRepository, times(0)).findAll();
		verify(modelMapper, times(1)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testGetPersonsByFiltersAge() {
		// Create a list of persons to be returned by the mock repository
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
			new Person(2L, "Montse", "García", "montse@garcia.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findByAge(28)).thenReturn(mockPersons);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getPersonsByFilters(null, 28, null);
		// Check the size of the result
		assertEquals(2, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		assertEquals(28, result.getFirst().getAge());
		// Check the last person
		assertEquals(2, result.getLast().getId());
		assertEquals("Montse", result.getLast().getFirstName());
		assertEquals("García", result.getLast().getLastName());
		assertEquals(28, result.getLast().getAge());
		// Verify that the methods were called the expected number of times
		verify(personRepository, times(0)).findByFirstName(anyString());
		verify(personRepository, times(1)).findByAge(anyInt());
		verify(personRepository, times(0)).findByVerified(anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAge(anyString(), anyInt());
		verify(personRepository, times(0)).findByFirstNameAndVerified(anyString(), anyBoolean());
		verify(personRepository, times(0)).findByAgeAndVerified(anyInt(), anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAgeAndVerified(anyString(), anyInt(), anyBoolean());
		verify(personRepository, times(0)).findAll();
		verify(modelMapper, times(2)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testGetPersonsByFiltersVerified() {
		// Create a list of persons to be returned by the mock repository
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
			new Person(2L, "Montse", "García", "montse@garcia.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true),
			new Person(3L, "Luna", "Sanz", "luna@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 6, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findByVerified(true)).thenReturn(mockPersons);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getPersonsByFilters(null, null, true);
		// Check the size of the result
		assertEquals(3, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		assertEquals(true, result.getFirst().getVerified());
		// Check the last person
		assertEquals(3, result.getLast().getId());
		assertEquals("Luna", result.getLast().getFirstName());
		assertEquals("Sanz", result.getLast().getLastName());
		assertEquals(true, result.getLast().getVerified());
		// Verify that the methods were called the expected number of times
		verify(personRepository, times(0)).findByFirstName(anyString());
		verify(personRepository, times(0)).findByAge(anyInt());
		verify(personRepository, times(1)).findByVerified(anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAge(anyString(), anyInt());
		verify(personRepository, times(0)).findByFirstNameAndVerified(anyString(), anyBoolean());
		verify(personRepository, times(0)).findByAgeAndVerified(anyInt(), anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAgeAndVerified(anyString(), anyInt(), anyBoolean());
		verify(personRepository, times(0)).findAll();
		verify(modelMapper, times(3)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testGetPersonsByFiltersFirstNameAgeVerified() {
		// Create a list of persons to be returned by the mock repository
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findByFirstNameAndAgeAndVerified("Javier", 28, true)).thenReturn(mockPersons);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getPersonsByFilters("Javier", 28, true);
		// Check the size of the result
		assertEquals(1, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		assertEquals(28, result.getFirst().getAge());
		assertEquals(true, result.getFirst().getVerified());
		// Verify that the methods were called the expected number of times
		verify(personRepository, times(0)).findByFirstName(anyString());
		verify(personRepository, times(0)).findByAge(anyInt());
		verify(personRepository, times(0)).findByVerified(anyBoolean());
		verify(personRepository, times(0)).findByFirstNameAndAge(anyString(), anyInt());
		verify(personRepository, times(0)).findByFirstNameAndVerified(anyString(), anyBoolean());
		verify(personRepository, times(0)).findByAgeAndVerified(anyInt(), anyBoolean());
		verify(personRepository, times(1)).findByFirstNameAndAgeAndVerified(anyString(), anyInt(), anyBoolean());
		verify(personRepository, times(0)).findAll();
		verify(modelMapper, times(1)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testGetPersonById() {
		// Create a Optional person to be returned by the mock repository
		Optional<Person> mockPerson = Optional.of(new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true));
		// Mock the repository to return the person
		when(personRepository.findById(1L)).thenReturn(mockPerson);
		// Mock the modelMapper conversion
		when(modelMapper.map(mockPerson.get(), PersonOutDTO.class)).thenReturn(new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true));

		// Call the service method
		Optional<PersonOutDTO> result = personService.getPersonById(1L);
		// Check the result
		assertEquals(true, result.isPresent());
		assertEquals(1, result.get().getId());
		assertEquals("Javier", result.get().getFirstName());
		assertEquals("Sanz", result.get().getLastName());
		verify(personRepository, times(1)).findById(anyLong());
		verify(modelMapper, times(1)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testCreatePerson() {
		// Create a person to be saved
		Person mockPerson = new Person(1L, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
		// Mock the repository to return the saved person
		when(personRepository.save(mockPerson)).thenReturn(mockPerson);
		// Mock the modelMapper conversion
		when(modelMapper.map(mockPerson, PersonOutDTO.class)).thenReturn(new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true));
		// Call the service method
		PersonOutDTO result = personService.createPerson(mockPerson);
		// Check the result
		assertEquals(1, result.getId());
		assertEquals("Javier", result.getFirstName());
		assertEquals("Sanz", result.getLastName());
		verify(personRepository, times(1)).save(mockPerson);
		verify(modelMapper, times(1)).map(mockPerson, PersonOutDTO.class);
	}

	@Test
	public void testUpdatePerson() throws Exception {
		// Create the id of the person to be updated
		Long id = 1L;
		// Create a person to be updated
		Person mockPerson = new Person(id, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
		// Mock the repository to return true for existsById
		when(personRepository.existsById(id)).thenReturn(true);
		// Mock the repository to return the saved person
		when(personRepository.save(mockPerson)).thenReturn(mockPerson);
		// Mock the modelMapper conversion
		when(modelMapper.map(mockPerson, PersonOutDTO.class)).thenReturn(new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true));
		// Call the service method
		PersonOutDTO result = personService.updatePerson(id, mockPerson);
		// Check the result
		assertEquals(id, result.getId());
		assertEquals("Javier", result.getFirstName());
		assertEquals("Sanz", result.getLastName());
		verify(personRepository, times(1)).existsById(id);
		verify(personRepository, times(1)).existsById(anyLong());
		verify(personRepository, times(1)).save(mockPerson);
		verify(modelMapper, times(1)).map(mockPerson, PersonOutDTO.class);
	}

	@Test
	public void testPartialUpdatePerson() throws Exception {
		// Create the id of the person to be partially updated
		Long id = 1L;
		// Create a person to be partially updated
		Person mockPerson = new Person(id, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
		// Mock the repository to return true for existsById
		when(personRepository.findById(id)).thenReturn(Optional.of(mockPerson));
		// Mock the repository to return the saved person
		when(personRepository.save(mockPerson)).thenReturn(mockPerson);
		// Mock the modelMapper conversion
		when(modelMapper.map(mockPerson, PersonOutDTO.class)).thenReturn(new PersonOutDTO(1L, "Javier", "Sanz", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true));
		// Call the service method
		PersonOutDTO result = personService.partialUpdatePerson(id, mockPerson);
		// Check the result
		assertEquals(id, result.getId());
		assertEquals("Javier", result.getFirstName());
		assertEquals("Sanz", result.getLastName());
		verify(personRepository, times(1)).findById(id);
		verify(personRepository, times(1)).findById(anyLong());
		verify(personRepository, times(1)).save(mockPerson);
		verify(personRepository, times(1)).save(any(Person.class));
		verify(modelMapper, times(1)).map(mockPerson, PersonOutDTO.class);
		verify(modelMapper, times(1)).map(any(Person.class), eq(PersonOutDTO.class));
	}

	@Test
	public void testDeletePerson() {
		// Not needed for this test case
	}

	@Test
	public void testGetPersonsByConferenceId() {
		// Create the organizer of the conference
		Person organizer = new Person(1L, "Javier", "Sanz", "0f3fde0103dd44077c040215a2fabd09a097aecc", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true);
		// Create the place of the conference
		Place place = new Place(1L, "Place 1", "Address 1", 1000, 2000.0, LocalDate.of(1996, 4, 29), true, "Equipment 1, Equipment 2, etc");
		// Create the mock conference
		Conference conference = new Conference(1L, "Conference 1", 100, 2000.0, true, LocalDate.of(2025, 4, 29), place, organizer);
		// Create a list of persons to be the attendees of the conference
		List<Person> mockPersons = List.of(
			new Person(1L, "Javier", "Sanz", "0f3fde0103dd44077c040215a2fabd09a097aecc", "javier@sanz.com", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
			new Person(2L, "Montse", "García", "0f3fde0103dd44077c040215a2fabd09a097aecc", "montse@garcia.com", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true)
		);
		// Create a list of attendances to be returned by the mock repository
		List<Attendance> mockAttendances = List.of(
			new Attendance(1L, "TEST-CODE-1", 1, 5.0, false, LocalDateTime.now(), mockPersons.getFirst(), conference),
			new Attendance(2L, "TEST-CODE-2", 2, 5.0, false, LocalDateTime.now(), mockPersons.getLast(), conference)
		);
		// Mock the repository to return the list of persons
		when(conferenceRepository.existsById(conference.getId())).thenReturn(true);
		when(attendanceRepository.findByConferenceId(conference.getId())).thenReturn(mockAttendances);
		// Generic mock for any Person to PersonOutDTO conversion
		when(modelMapper.map(any(Person.class), eq(PersonOutDTO.class))).thenAnswer(invocation -> {
			Person person = invocation.getArgument(0);
			PersonOutDTO dto = new PersonOutDTO();
			dto.setId(person.getId());
			dto.setFirstName(person.getFirstName());
			dto.setLastName(person.getLastName());
			dto.setEmail(person.getEmail());
			dto.setAge(person.getAge());
			dto.setHeight(person.getHeight());
			dto.setInterests(person.getInterests());
			dto.setBirthDate(person.getBirthDate());
			dto.setVerified(person.getVerified());
			return dto;
		});
		// Call the service method
		List<PersonOutDTO> result = personService.getPersonsByConferenceId(conference.getId());
		// Check the size of the result
		assertEquals(2, result.size());
		// Check the first person
		assertEquals(1, result.getFirst().getId());
		assertEquals("Javier", result.getFirst().getFirstName());
		assertEquals("Sanz", result.getFirst().getLastName());
		// Check the last person
		assertEquals(2, result.getLast().getId());
		assertEquals("Montse", result.getLast().getFirstName());
		assertEquals("García", result.getLast().getLastName());
		// Verify that the methods were called the expected number of times
		verify(conferenceRepository, times(1)).existsById(conference.getId());
		verify(attendanceRepository, times(1)).findByConferenceId(conference.getId());
		verify(modelMapper, times(2)).map(any(Person.class), eq(PersonOutDTO.class));
	}
}
