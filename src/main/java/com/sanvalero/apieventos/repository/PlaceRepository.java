package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Place;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {
    List<Place> findAll();
    Optional<Place> findById(Long id);
}
