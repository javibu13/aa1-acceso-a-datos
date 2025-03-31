package com.sanvalero.apieventos.controller;

import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.service.PlaceService;
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
@RequestMapping("/places")
public class PlaceController {
    private static final Logger logger = LoggerFactory.getLogger(PlaceController.class);

    @Autowired
    private PlaceService placeService;

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces(@RequestParam(required = false) Integer minCapacity,
                                                    @RequestParam(required = false) Integer maxCapacity,
                                                    @RequestParam(required = false) Boolean hasParking) {
        if (minCapacity != null || maxCapacity != null || hasParking != null) {
            logger.info("Filtering places by minCapacity: {}, maxCapacity: {}, hasParking: {}", minCapacity, maxCapacity, hasParking);
            List<Place> places = placeService.getPlacesByFilters(minCapacity, maxCapacity, hasParking);
            return new ResponseEntity<>(places, HttpStatus.OK);
        } else {
            logger.info("Retrieving all places without filters");
            List<Place> places = placeService.getAllPlaces();
            return new ResponseEntity<>(places, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        logger.info("Retrieving place with id: {}", id);
        return placeService.getPlaceById(id)
                .map(place -> new ResponseEntity<>(place, HttpStatus.OK))
                .orElseThrow(() -> new EntityNotFoundException("Place not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@Valid @RequestBody Place place) {
        logger.info("Creating new place: {}", place);
        Place createdPlace = placeService.createPlace(place);
        return new ResponseEntity<>(createdPlace, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @Valid @RequestBody Place place) {
        logger.info("Updating place with id: {} to {}", id, place);
        Place updatedPlace = placeService.updatePlace(id, place);
        if (updatedPlace != null) {
            return new ResponseEntity<>(updatedPlace, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        logger.info("Deleting place with id: {}", id);
        placeService.deletePlace(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Error handling methods
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException enfe) {
        logger.error("EntityNotFoundException: {}", enfe.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), enfe.getClass().getSimpleName(), "Place not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException dive) {
        logger.error("DataIntegrityViolationException: {}", dive.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), dive.getClass().getSimpleName(), "Error found in the places request");
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
