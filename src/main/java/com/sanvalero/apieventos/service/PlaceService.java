package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Place;
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

    public Place partialUpdatePlace(Long id, Place place) throws EntityNotFoundException, IllegalArgumentException {
        Optional<Place> existingPlace = placeRepository.findById(id);
        if (existingPlace.isPresent()) {
            Place updatedPlace = existingPlace.get();
            if (place.getName() != null) {
                if (place.getName().isEmpty()) {
                    throw new IllegalArgumentException("Name cannot be empty");
                }
                updatedPlace.setName(place.getName());
            }
            if (place.getAddress() != null) {
                if (place.getAddress().isEmpty()) {
                    throw new IllegalArgumentException("Address cannot be empty");
                }
                updatedPlace.setAddress(place.getAddress());
            }
            if (place.getCapacity() != null) {
                if (place.getCapacity() <= 0) {
                    throw new IllegalArgumentException("Capacity must be greater than 0");
                }
                updatedPlace.setCapacity(place.getCapacity());
            }
            if (place.getArea() != null) {
                if (place.getArea() <= 0) {
                    throw new IllegalArgumentException("Area must be greater than 0");
                }
                updatedPlace.setArea(place.getArea());
            }
            if (place.getInaugurationDate() != null) {
                updatedPlace.setInaugurationDate(place.getInaugurationDate());
            }
            if (place.getHasParking() != null) {
                updatedPlace.setHasParking(place.getHasParking());
            }
            if (place.getEquipment() != null) {
                updatedPlace.setEquipment(place.getEquipment());
            }
            return placeRepository.save(updatedPlace);
        } else {
            throw new EntityNotFoundException("Place not found with id: " + id);
        }
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}
