package com.sanvalero.apieventos.repository;

import com.sanvalero.apieventos.domain.Attendance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
    // Method to find all attendances
    List<Attendance> findAll();
    // Custom methods to find attendances by different attributes
    @Query(value = "SELECT * FROM attendance WHERE seat_number >= :minSeatNumber", nativeQuery = true)
    List<Attendance> findBySeatNumberGreaterThanEqual(@Param("minSeatNumber") Integer minSeatNumber);
    @Query(value = "SELECT * FROM attendance WHERE seat_number <= :maxSeatNumber", nativeQuery = true)
    List<Attendance> findBySeatNumberLessThanEqual(@Param("maxSeatNumber") Integer maxSeatNumber);
    @Query(value = "SELECT * FROM attendance WHERE checked_in = :isCheckedIn", nativeQuery = true)
    List<Attendance> findByCheckedIn(@Param("isCheckedIn") Boolean isCheckedIn);
    List<Attendance> findBySeatNumberBetween(Integer minSeatNumber, Integer maxSeatNumber);
    @Query("SELECT a FROM attendance a WHERE a.seatNumber >= :minSeatNumber AND a.checkedIn = :isCheckedIn")
    List<Attendance> findBySeatNumberGreaterThanEqualAndCheckedIn(@Param("minSeatNumber") Integer minSeatNumber, @Param("isCheckedIn") Boolean isCheckedIn);
    @Query("SELECT a FROM attendance a WHERE a.seatNumber <= :maxSeatNumber AND a.checkedIn = :isCheckedIn")
    List<Attendance> findBySeatNumberLessThanEqualAndCheckedIn(@Param("maxSeatNumber") Integer maxSeatNumber, @Param("isCheckedIn") Boolean isCheckedIn);
    @Query("SELECT a FROM attendance a WHERE a.seatNumber BETWEEN :minSeatNumber AND :maxSeatNumber AND a.checkedIn = :isCheckedIn")
    List<Attendance> findBySeatNumberBetweenAndCheckedIn(@Param("minSeatNumber") Integer minSeatNumber, @Param("maxSeatNumber") Integer maxSeatNumber, @Param("isCheckedIn") Boolean isCheckedIn);
    // Method to find an attendance by ID
    Optional<Attendance> findById(Long id);
    // Method to find all attendances by conference ID
    List<Attendance> findByConferenceId(Long conferenceId);
    // Method to find all attendances by person ID
    List<Attendance> findByPersonId(Long personId);
}
