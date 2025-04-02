package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Attendance;
import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.dto.AttendanceInDTO;
import com.sanvalero.apieventos.dto.AttendanceOutDTO;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.AttendanceRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.repository.PersonRepository;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendancesByFilters(Integer minSeatNumber, Integer maxSeatNumber, Boolean isCheckedIn) {
        if (minSeatNumber != null && maxSeatNumber == null && isCheckedIn == null) {
            return attendanceRepository.findBySeatNumberGreaterThanEqual(minSeatNumber);
        } else if (minSeatNumber == null && maxSeatNumber != null && isCheckedIn == null) {
            return attendanceRepository.findBySeatNumberLessThanEqual(maxSeatNumber);
        } else if (minSeatNumber == null && maxSeatNumber == null && isCheckedIn != null) {
            return attendanceRepository.findByCheckedIn(isCheckedIn);
        } else if (minSeatNumber != null && maxSeatNumber != null && isCheckedIn == null) {
            return attendanceRepository.findBySeatNumberBetween(minSeatNumber, maxSeatNumber);
        } else if (minSeatNumber != null && maxSeatNumber == null && isCheckedIn != null) {
            return attendanceRepository.findBySeatNumberGreaterThanEqualAndCheckedIn(minSeatNumber, isCheckedIn);
        } else if (minSeatNumber == null && maxSeatNumber != null && isCheckedIn != null) {
            return attendanceRepository.findBySeatNumberLessThanEqualAndCheckedIn(maxSeatNumber, isCheckedIn);
        } else if (minSeatNumber != null && maxSeatNumber != null && isCheckedIn != null) {
            return attendanceRepository.findBySeatNumberBetweenAndCheckedIn(minSeatNumber, maxSeatNumber, isCheckedIn);
        } else {
            // If no filters are applied, return all attendances
            return attendanceRepository.findAll();
        }
    }

    public Optional<AttendanceOutDTO> getAttendanceById(Long id) {
        Optional<Attendance> attendanceOptional = attendanceRepository.findById(id);
        if (attendanceOptional.isPresent()) {
            Attendance attendance = attendanceOptional.get();
            PersonOutDTO personOutDTO = modelMapper.map(attendance.getPerson(), PersonOutDTO.class);
            AttendanceOutDTO attendanceOutDTO = modelMapper.map(attendance, AttendanceOutDTO.class);
            attendanceOutDTO.setPerson(personOutDTO);
            return Optional.of(attendanceOutDTO);
        } else {
            return Optional.empty();
        }
    }

    public AttendanceOutDTO createAttendance(AttendanceInDTO attendanceInDTO) {
        // Check if the conference exists
        Optional<Conference> conference = conferenceRepository.findById(attendanceInDTO.getConference());
        if (conference.isEmpty()) {
            throw new EntityNotFoundException("Conference not found with ID: " + attendanceInDTO.getConference());
        }
        // Check if the person exists
        Optional<Person> person = personRepository.findById(attendanceInDTO.getPerson());
        if (person.isEmpty()) {
            throw new EntityNotFoundException("Person not found with ID: " + attendanceInDTO.getPerson());
        }
        // Map the DTO to the entity
        Attendance attendance = modelMapper.map(attendanceInDTO, Attendance.class);
        attendance.setTicketCode(UUID.randomUUID().toString());
        attendance.setRegistrationDate(LocalDateTime.now());
        attendance.setConference(conference.get());
        attendance.setPerson(person.get());
        Attendance attendanceSaved = attendanceRepository.save(attendance);
        // Map the saved entity to the output DTO
        AttendanceOutDTO attendanceOutDTO = modelMapper.map(attendanceSaved, AttendanceOutDTO.class);
        PersonOutDTO personOutDTO = modelMapper.map(attendanceSaved.getPerson(), PersonOutDTO.class);
        attendanceOutDTO.setPerson(personOutDTO);
        return attendanceOutDTO;
    }

    public AttendanceOutDTO updateAttendance(Long id, AttendanceInDTO attendanceInDTO) {
        if (attendanceRepository.existsById(id)) {
            Attendance attendanceOld = attendanceRepository.findById(id).get();
            // Check if the conference exists
            Optional<Conference> conference = conferenceRepository.findById(attendanceInDTO.getConference());
            if (conference.isEmpty()) {
                throw new EntityNotFoundException("Conference not found with ID: " + attendanceInDTO.getConference());
            }
            // Check if the person exists
            Optional<Person> person = personRepository.findById(attendanceInDTO.getPerson());
            if (person.isEmpty()) {
                throw new EntityNotFoundException("Person not found with ID: " + attendanceInDTO.getPerson());
            }
            // Map the DTO to the entity
            Attendance attendance = modelMapper.map(attendanceInDTO, Attendance.class);
            attendance.setTicketCode(attendanceOld.getTicketCode());
            attendance.setRegistrationDate(attendanceOld.getRegistrationDate());
            attendance.setConference(conference.get());
            attendance.setPerson(person.get());
            attendance.setId(id);
            Attendance attendanceSaved = attendanceRepository.save(attendance);
            // Map the saved entity to the output DTO
            AttendanceOutDTO attendanceOutDTO = modelMapper.map(attendanceSaved, AttendanceOutDTO.class);
            PersonOutDTO personOutDTO = modelMapper.map(attendanceSaved.getPerson(), PersonOutDTO.class);
            attendanceOutDTO.setPerson(personOutDTO);
            return attendanceOutDTO;
        } else {
            throw new EntityNotFoundException("Attendance not found with ID: " + id);
        }
    }

    public AttendanceOutDTO partialUpdateAttendance(Long id, AttendanceInDTO attendanceInDTO) throws EntityNotFoundException, IllegalArgumentException {
        Optional<Attendance> existingAttendance = attendanceRepository.findById(id);
        if (existingAttendance.isPresent()) {
            Attendance attendance = existingAttendance.get();
            if (attendanceInDTO.getSeatNumber() != null) {
                if (attendanceInDTO.getSeatNumber() < 0) {
                    throw new IllegalArgumentException("Seat number cannot be negative");
                }
                attendance.setSeatNumber(attendanceInDTO.getSeatNumber());
            }
            if (attendanceInDTO.getTicketPrice() != null) {
                if (attendanceInDTO.getTicketPrice() < 0) {
                    throw new IllegalArgumentException("Ticket price cannot be negative");
                }
                attendance.setTicketPrice(attendanceInDTO.getTicketPrice());
            }
            if (attendanceInDTO.getCheckedIn() != null) {
                attendance.setCheckedIn(attendanceInDTO.getCheckedIn());
            }
            if (attendanceInDTO.getPerson() != null) {
                Optional<Person> person = personRepository.findById(attendanceInDTO.getPerson());
                if (person.isEmpty()) {
                    throw new EntityNotFoundException("Person not found with ID: " + attendanceInDTO.getPerson());
                }
                attendance.setPerson(person.get());
            }
            if (attendanceInDTO.getConference() != null) {
                Optional<Conference> conference = conferenceRepository.findById(attendanceInDTO.getConference());
                if (conference.isEmpty()) {
                    throw new EntityNotFoundException("Conference not found with ID: " + attendanceInDTO.getConference());
                }
                attendance.setConference(conference.get());
            }
            Attendance attendanceSaved = attendanceRepository.save(attendance);
            AttendanceOutDTO attendanceOutDTO = modelMapper.map(attendanceSaved, AttendanceOutDTO.class);
            PersonOutDTO personOutDTO = modelMapper.map(attendanceSaved.getPerson(), PersonOutDTO.class);
            attendanceOutDTO.setPerson(personOutDTO);
            return attendanceOutDTO;
        } else {
            throw new EntityNotFoundException("Attendance not found with ID: " + id);
        }
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
