package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    // Method to find all persons
    List<Person> findAll();
    // Custom methods to find persons by different attributes
    List<Person> findByFirstName(String firstName);
    List<Person> findByAge(Integer age);
    List<Person> findByVerified(Boolean verified);
    List<Person> findByFirstNameAndAge(String firstName, Integer age);
    List<Person> findByFirstNameAndVerified(String firstName, Boolean verified);
    List<Person> findByAgeAndVerified(Integer age, Boolean verified);
    List<Person> findByFirstNameAndAgeAndVerified(String firstName, Integer age, Boolean verified);
    // Method to find a person by ID
    Optional<Person> findById(Long id);
}
