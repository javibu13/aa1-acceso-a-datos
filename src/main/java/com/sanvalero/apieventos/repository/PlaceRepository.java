package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Place;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {
    // Method to find all places
    List<Place> findAll();
    // Custom methods to find places by different attributes
    List<Place> findByCapacityGreaterThanEqual(Integer minCapacity);
    List<Place> findByCapacityLessThanEqual(Integer maxCapacity);
    List<Place> findByHasParking(Boolean hasParking);
    List<Place> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);
    List<Place> findByCapacityGreaterThanEqualAndHasParking(Integer minCapacity, Boolean hasParking);
    List<Place> findByCapacityLessThanEqualAndHasParking(Integer maxCapacity, Boolean hasParking);
    List<Place> findByCapacityBetweenAndHasParking(Integer minCapacity, Integer maxCapacity, Boolean hasParking);
    // Method to find a place by ID
    Optional<Place> findById(Long id);
}
