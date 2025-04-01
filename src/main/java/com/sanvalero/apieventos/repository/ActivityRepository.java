package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Activity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    // Method to find all activities
    List<Activity> findAll();
    // Custom methods to find activities by different attributes
    List<Activity> findByDurationGreaterThanEqual(Integer minDuration);
    List<Activity> findByDurationLessThanEqual(Integer maxDuration);
    List<Activity> findByOpen(Boolean isOpen);
    List<Activity> findByDurationBetween(Integer minDuration, Integer maxDuration);
    List<Activity> findByDurationGreaterThanEqualAndOpen(Integer minDuration, Boolean isOpen);
    List<Activity> findByDurationLessThanEqualAndOpen(Integer maxDuration, Boolean isOpen);
    List<Activity> findByDurationBetweenAndOpen(Integer minDuration, Integer maxDuration, Boolean isOpen);
    // Method to find an activity by ID
    Optional<Activity> findById(Long id);
}
