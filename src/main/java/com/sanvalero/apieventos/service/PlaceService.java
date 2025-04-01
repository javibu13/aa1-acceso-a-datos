package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public List<Place> getPlacesByFilters(Integer minCapacity, Integer maxCapacity, Boolean hasParking) {
        if (minCapacity != null && maxCapacity == null && hasParking == null) {
            return placeRepository.findByCapacityGreaterThanEqual(minCapacity);
        } else if (minCapacity == null && maxCapacity != null && hasParking == null) {
            return placeRepository.findByCapacityLessThanEqual(maxCapacity);
        } else if (minCapacity == null && maxCapacity == null && hasParking != null) {
            return placeRepository.findByHasParking(hasParking);
        } else if (minCapacity != null && maxCapacity != null && hasParking == null) {
            return placeRepository.findByCapacityBetween(minCapacity, maxCapacity);
        } else if (minCapacity != null && maxCapacity == null && hasParking != null) {
            return placeRepository.findByCapacityGreaterThanEqualAndHasParking(minCapacity, hasParking);
        } else if (minCapacity == null && maxCapacity != null && hasParking != null) {
            return placeRepository.findByCapacityLessThanEqualAndHasParking(maxCapacity, hasParking);
        } else if (minCapacity != null && maxCapacity != null && hasParking != null) {
            return placeRepository.findByCapacityBetweenAndHasParking(minCapacity, maxCapacity, hasParking);
        } else {
            // If no filters are applied, return all places
            return placeRepository.findAll();
        }
    }

    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }

    public Place createPlace(Place place) {
        return placeRepository.save(place);
    }

    public Place updatePlace(Long id, Place place) throws EntityNotFoundException {
        if (placeRepository.existsById(id)) {
            place.setId(id);
            return placeRepository.save(place);
        } else {
            throw new EntityNotFoundException("Place not found with id: " + id);
        }
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}
