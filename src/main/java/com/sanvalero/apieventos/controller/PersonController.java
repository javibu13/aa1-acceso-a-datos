package com.sanvalero.apieventos.controller;

import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.service.PersonService;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.exception.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonOutDTO>> getAllPersons(@RequestParam(required = false) String firstName, 
                                                            @RequestParam(required = false) Integer age,
                                                            @RequestParam(required = false) Boolean verified) {
        if (firstName != null || age != null || verified != null) {
            logger.info("Filtering persons by firstName: {}, age: {}, verified: {}", firstName, age, verified);
            List<PersonOutDTO> persons = personService.getPersonsByFilters(firstName, age, verified);
            return new ResponseEntity<>(persons, HttpStatus.OK);
        } else {
            logger.info("Retrieving all persons without filters");
            List<PersonOutDTO> persons = personService.getAllPersons();
            return new ResponseEntity<>(persons, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonOutDTO> getPersonById(@PathVariable Long id) {
        logger.info("Retrieving person with id: {}", id);
        return personService.getPersonById(id)
                .map(person -> new ResponseEntity<>(person, HttpStatus.OK))
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<PersonOutDTO> createPerson(@Valid @RequestBody Person person) {
        logger.info("Creating new person: {}", person);
        PersonOutDTO createdPerson = personService.createPerson(person);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonOutDTO> updatePerson(@PathVariable Long id, @Valid @RequestBody Person person) {
        logger.info("Updating person with id: {} to {}", id, person);
        PersonOutDTO updatedPerson = personService.updatePerson(id, person);
        if (updatedPerson != null) {
            return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonOutDTO> partialUpdatePerson(@PathVariable Long id, @RequestBody Person person) {
        logger.info("Partially updating person with id: {} to {}", id, person);
        PersonOutDTO updatedPerson = personService.partialUpdatePerson(id, person);
        if (updatedPerson != null) {
            return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        logger.info("Deleting person with id: {}", id);
        personService.deletePerson(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/conferences/{conferenceId}")
    public ResponseEntity<List<PersonOutDTO>> getPersonsByConferenceId(@PathVariable Long conferenceId) {
        logger.info("Retrieving persons for conference with id: {}", conferenceId);
        List<PersonOutDTO> persons = personService.getPersonsByConferenceId(conferenceId);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    // Error handling methods
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException enfe) {
        logger.error("EntityNotFoundException: {}", enfe.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), enfe.getClass().getSimpleName(), "Person not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException dive) {
        logger.error("DataIntegrityViolationException: {}", dive.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), dive.getClass().getSimpleName(), "Error found in the persons request");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException iae) {
        logger.error("IllegalArgumentException: {}", iae.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), iae.getClass().getSimpleName(), iae.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        logger.error("MethodArgumentNotValidException: {}", manve.getMessage());
        Map<String, String> errors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
            errorMessages.add(message);
        });
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), manve.getClass().getSimpleName(), errorMessages.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Exception: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getClass().getSimpleName(), "An unexpected internal error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
