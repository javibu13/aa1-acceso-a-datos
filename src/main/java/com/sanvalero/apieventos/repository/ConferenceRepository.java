package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    // Method to find all conferences
    List<Conference> findAll();
    // Custom methods to find conferences by different attributes
    List<Conference> findByCapacityGreaterThanEqual(Integer minCapacity);
    List<Conference> findByCapacityLessThanEqual(Integer maxCapacity);
    List<Conference> findByOnline(Boolean isOnline);
    List<Conference> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);
    List<Conference> findByCapacityGreaterThanEqualAndOnline(Integer minCapacity, Boolean isOnline);
    List<Conference> findByCapacityLessThanEqualAndOnline(Integer maxCapacity, Boolean isOnline);
    List<Conference> findByCapacityBetweenAndOnline(Integer minCapacity, Integer maxCapacity, Boolean isOnline);
    // Method to find a conference by ID
    Optional<Conference> findById(Long id);
    // Method to find all conferences by organizer ID
    List<Conference> findByOrganizerId(Long organizerId);
}
