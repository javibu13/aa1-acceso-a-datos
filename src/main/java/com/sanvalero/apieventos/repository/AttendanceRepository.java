package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Attendance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
    List<Attendance> findAll();
    Optional<Attendance> findById(Long id);
}
