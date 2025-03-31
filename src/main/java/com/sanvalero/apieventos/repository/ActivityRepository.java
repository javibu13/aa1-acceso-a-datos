package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    List<Activity> findAll();
    Optional<Activity> findById(Long id);
}
