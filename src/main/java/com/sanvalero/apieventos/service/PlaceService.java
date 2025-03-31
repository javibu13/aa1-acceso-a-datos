package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.repository.PlaceRepository;

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

    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }

    public Place createPlace(Place place) {
        return placeRepository.save(place);
    }

    public Place updatePlace(Long id, Place place) {
        if (placeRepository.existsById(id)) {
            place.setId(id);
            return placeRepository.save(place);
        } else {
            return null;
        }
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}
