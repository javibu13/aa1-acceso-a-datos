package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.PersonRepository;
import com.sanvalero.apieventos.service.PersonService;

@ExtendWith(MockitoExtension.class)
class PersonServiceTests {

	@InjectMocks
	private PersonService personService;

	@Mock
	private PersonRepository personRepository;

	@Mock
    private ModelMapper modelMapper;

	@Test
	public void testGetAllPersons() {
		// Create a list of persons to be returned by the mock repository
		List<Person> persons = List.of(
			new Person((long) 1, "Javier", "Sanz", "javier@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true),
			new Person((long) 2, "Montse", "García", "montse@garcia.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 6, 11), true),
			new Person((long) 3, "Luna", "Sanz", "luna@sanz.com", "0f3fde0103dd44077c040215a2fabd09a097aecc", 28, 1.75, "Technology", LocalDate.of(1996, 4, 29), true)
		);
		// Mock the repository to return the list of persons
		when(personRepository.findAll()).thenReturn(persons);
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

}
