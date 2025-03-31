package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    List<Conference> findAll();
    Optional<Conference> findById(Long id);
}
