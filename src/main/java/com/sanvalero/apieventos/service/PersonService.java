package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Attendance;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.AttendanceRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.repository.PersonRepository;

import org.modelmapper.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<PersonOutDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        List<PersonOutDTO> personOutDTOs = new ArrayList<>();
        for (Person person : persons) {
            PersonOutDTO personOutDTO = modelMapper.map(person, PersonOutDTO.class);
            personOutDTOs.add(personOutDTO);
        }
        return personOutDTOs;
    }

    public List<PersonOutDTO> getPersonsByFilters(String firstName, Integer age, Boolean verified) {
        List<Person> persons = new ArrayList<>();
        if (firstName != null && age == null && verified == null) {
            persons = personRepository.findByFirstName(firstName);
        } else if (firstName == null && age != null && verified == null) {
            persons = personRepository.findByAge(age);
        } else if (firstName == null && age == null && verified != null) {
            persons = personRepository.findByVerified(verified);
        } else if (firstName != null && age != null && verified == null) {
            persons = personRepository.findByFirstNameAndAge(firstName, age);
        } else if (firstName != null && age == null && verified != null) {
            persons = personRepository.findByFirstNameAndVerified(firstName, verified);
        } else if (firstName == null && age != null && verified != null) {
            persons = personRepository.findByAgeAndVerified(age, verified);
        } else if (firstName != null && age != null && verified != null) {
            persons = personRepository.findByFirstNameAndAgeAndVerified(firstName, age, verified);
        } else {
            persons = personRepository.findAll();
        }
        List<PersonOutDTO> personOutDTOs = new ArrayList<>();
        for (Person person : persons) {
            PersonOutDTO personOutDTO = modelMapper.map(person, PersonOutDTO.class);
            personOutDTOs.add(personOutDTO);
        }
        return personOutDTOs;
    }

    public Optional<PersonOutDTO> getPersonById(Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            PersonOutDTO personOutDTO = modelMapper.map(person.get(), PersonOutDTO.class);
            return Optional.of(personOutDTO);
        } else {
            return Optional.empty();
        }
    }

    public PersonOutDTO createPerson(Person person) {
        Person createdPerson = personRepository.save(person);
        return modelMapper.map(createdPerson, PersonOutDTO.class);
    }

    public PersonOutDTO updatePerson(Long id, Person person) throws EntityNotFoundException {
        if (personRepository.existsById(id)) {
            person.setId(id);
            Person updatedPerson = personRepository.save(person);
            return modelMapper.map(updatedPerson, PersonOutDTO.class);
        } else {
            throw new EntityNotFoundException("Person not found with id: " + id);
        }
    }

    public PersonOutDTO partialUpdatePerson(Long id, Person person) throws EntityNotFoundException, IllegalArgumentException {
        Optional<Person> existingPerson = personRepository.findById(id);
        if (existingPerson.isPresent()) {
            Person updatedPerson = existingPerson.get();
            if (person.getFirstName() != null) {
                if (person.getFirstName().isEmpty()) {
                    throw new IllegalArgumentException("First name cannot be empty");
                }
                updatedPerson.setFirstName(person.getFirstName());
            }
            if (person.getLastName() != null) {
                if (person.getLastName().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                updatedPerson.setLastName(person.getLastName());
            }
            if (person.getEmail() != null) {
                if (person.getEmail().isEmpty() || !person.getEmail().contains("@")) {
                    throw new IllegalArgumentException("Email cannot be empty");
                }
                updatedPerson.setEmail(person.getEmail());
            }
            if (person.getPass() != null) {
                if (person.getPass().isEmpty()) {
                    throw new IllegalArgumentException("Password cannot be empty");
                }
                updatedPerson.setPass(person.getPass());
            }
            if (person.getAge() != null) {
                if (person.getAge() < 0) {
                    throw new IllegalArgumentException("Age cannot be negative");
                }
                updatedPerson.setAge(person.getAge());
            }
            if (person.getHeight() != null) {
                if (person.getHeight() < 0) {
                    throw new IllegalArgumentException("Height cannot be negative");
                }
                updatedPerson.setHeight(person.getHeight());
            }
            if (person.getInterests() != null) {
                updatedPerson.setInterests(person.getInterests());
            }
            if (person.getBirthDate() != null) {
                if (person.getBirthDate().isAfter(java.time.LocalDate.now())) {
                    throw new IllegalArgumentException("Birth date cannot be in the future");
                }
                updatedPerson.setBirthDate(person.getBirthDate());
            }
            if (person.getVerified() != null) {
                updatedPerson.setVerified(person.getVerified());
            }
            personRepository.save(updatedPerson);
            return modelMapper.map(updatedPerson, PersonOutDTO.class);
        } else {
            throw new EntityNotFoundException("Person not found with id: " + id);
        }
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public List<PersonOutDTO> getPersonsByConferenceId(Long conferenceId) {
        // Check if the conference ID is valid (not null and greater than 0) and exists in the database
        if (conferenceId == null || conferenceId <= 0) {
            throw new IllegalArgumentException("Invalid conference ID: " + conferenceId);
        }
        if (!conferenceRepository.existsById(conferenceId)) {
            throw new EntityNotFoundException("Conference not found with id: " + conferenceId);
        }
        // Get all attendances for the conference
        List<Attendance> attendances = attendanceRepository.findByConferenceId(conferenceId);
        // Create a list to hold the person IDs from the attendances
        List<Long> personsIds = new ArrayList<>();
        List<PersonOutDTO> personOutDTOs = new ArrayList<>();
        for (Attendance attendance : attendances) {
            // Get the person ID from each attendance and find the corresponding person
            Long personId = attendance.getPerson().getId();
            if (!personsIds.contains(personId)) {
                personsIds.add(personId);
                personOutDTOs.add(modelMapper.map(attendance.getPerson(), PersonOutDTO.class));
            }
        }
        return personOutDTOs;
    }

}
