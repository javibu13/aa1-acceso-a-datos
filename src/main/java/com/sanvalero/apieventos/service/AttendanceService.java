package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Attendance;
import com.sanvalero.apieventos.repository.AttendanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(Long id, Attendance attendance) {
        if (attendanceRepository.existsById(id)) {
            attendance.setId(id);
            return attendanceRepository.save(attendance);
        } else {
            return null;
        }
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
