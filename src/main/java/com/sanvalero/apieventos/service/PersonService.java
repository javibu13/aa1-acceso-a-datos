package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.dto.PersonOutDTO;
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

    public PersonOutDTO updatePerson(Long id, Person person) {
        if (personRepository.existsById(id)) {
            person.setId(id);
            Person updatedPerson = personRepository.save(person);
            return modelMapper.map(updatedPerson, PersonOutDTO.class);
        } else {
            throw new EntityNotFoundException("Person not found with id: " + id);
        }
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

}
