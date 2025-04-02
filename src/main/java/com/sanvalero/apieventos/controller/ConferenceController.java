package com.sanvalero.apieventos.controller;

import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.dto.ConferenceInDTO;
import com.sanvalero.apieventos.service.ConferenceService;
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
@RequestMapping("/conferences")
public class ConferenceController {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceController.class);

    @Autowired
    private ConferenceService conferenceService;

    @GetMapping
    public ResponseEntity<List<Conference>> getAllConferences(@RequestParam(required = false) Integer minCapacity,
                                                    @RequestParam(required = false) Integer maxCapacity,
                                                    @RequestParam(required = false) Boolean isOnline) {
        if (minCapacity != null || maxCapacity != null || isOnline != null) {
            logger.info("Filtering conferences by minCapacity: {}, maxCapacity: {}, isOnline: {}", minCapacity, maxCapacity, isOnline);
            List<Conference> conferences = conferenceService.getConferencesByFilters(minCapacity, maxCapacity, isOnline);
            return new ResponseEntity<>(conferences, HttpStatus.OK);
        } else {
            logger.info("Retrieving all conferences without filters");
            List<Conference> conferences = conferenceService.getAllConferences();
            return new ResponseEntity<>(conferences, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conference> getConferenceById(@PathVariable Long id) {
        logger.info("Retrieving conference with id: {}", id);
        return conferenceService.getConferenceById(id)
                .map(conference -> new ResponseEntity<>(conference, HttpStatus.OK))
                .orElseThrow(() -> new EntityNotFoundException("Conference not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Conference> createConference(@Valid @RequestBody ConferenceInDTO conferenceInDTO) {
        logger.info("Creating new conference: {}", conferenceInDTO);
        Conference createdConference = conferenceService.createConference(conferenceInDTO);
        return new ResponseEntity<>(createdConference, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conference> updateConference(@PathVariable Long id, @Valid @RequestBody ConferenceInDTO conferenceInDTO) {
        logger.info("Updating conference with id: {} to {}", id, conferenceInDTO);
        Conference updatedConference = conferenceService.updateConference(id, conferenceInDTO);
        if (updatedConference != null) {
            return new ResponseEntity<>(updatedConference, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Conference> partialUpdateConference(@PathVariable Long id, @RequestBody ConferenceInDTO conferenceInDTO) {
        logger.info("Partially updating conference with id: {} to {}", id, conferenceInDTO);
        Conference updatedConference = conferenceService.partialUpdateConference(id, conferenceInDTO);
        if (updatedConference != null) {
            return new ResponseEntity<>(updatedConference, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConference(@PathVariable Long id) {
        logger.info("Deleting conference with id: {}", id);
        conferenceService.deleteConference(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<List<Conference>> getConferencesByPersonId(@PathVariable Long id) {
        logger.info("Retrieving conferences for person with id: {}", id);
        List<Conference> conferences = conferenceService.getConferencesByPersonId(id);
        return new ResponseEntity<>(conferences, HttpStatus.OK);
    }

    @GetMapping("/organizers/{id}")
    public ResponseEntity<List<Conference>> getConferencesByOrganizerId(@PathVariable Long id) {
        logger.info("Retrieving conferences for organizer with id: {}", id);
        List<Conference> conferences = conferenceService.getConferencesByOrganizerId(id);
        return new ResponseEntity<>(conferences, HttpStatus.OK);
    }

    // Error handling methods
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException enfe) {
        logger.error("EntityNotFoundException: {}", enfe.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), enfe.getClass().getSimpleName(), enfe.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException dive) {
        logger.error("DataIntegrityViolationException: {}", dive.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), dive.getClass().getSimpleName(), "Error found in the conferences request");
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
