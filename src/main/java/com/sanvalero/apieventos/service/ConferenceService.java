package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Attendance;
import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.dto.ConferenceInDTO;
import com.sanvalero.apieventos.dto.ConferenceOutDTO;
import com.sanvalero.apieventos.dto.PersonOutDTO;
import com.sanvalero.apieventos.repository.AttendanceRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.repository.PersonRepository;
import com.sanvalero.apieventos.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceService.class);

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ConferenceOutDTO> getAllConferences() {
        // Get all conferences from the repository
        List<Conference> conferences = conferenceRepository.findAll();
        // Create a list to hold the ConferenceOutDTO objects
        List<ConferenceOutDTO> conferenceOutDTOs = new ArrayList<>();
        // Map each Conference object to a ConferenceOutDTO object and add it to the list
        for (Conference conference : conferences) {
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(conference, ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(conference.getOrganizer(), PersonOutDTO.class));
            conferenceOutDTOs.add(conferenceOutDTO);
        }
        return conferenceOutDTOs;
    }

    public List<ConferenceOutDTO> getConferencesByFilters(Integer minCapacity, Integer maxCapacity, Boolean isOnline) {
        List<Conference> conferences;
        if (minCapacity != null && maxCapacity == null && isOnline == null) {
            conferences = conferenceRepository.findByCapacityGreaterThanEqual(minCapacity);
        } else if (minCapacity == null && maxCapacity != null && isOnline == null) {
            conferences = conferenceRepository.findByCapacityLessThanEqual(maxCapacity);
        } else if (minCapacity == null && maxCapacity == null && isOnline != null) {
            conferences = conferenceRepository.findByOnline(isOnline);
        } else if (minCapacity != null && maxCapacity != null && isOnline == null) {
            conferences = conferenceRepository.findByCapacityBetween(minCapacity, maxCapacity);
        } else if (minCapacity != null && maxCapacity == null && isOnline != null) {
            conferences = conferenceRepository.findByCapacityGreaterThanEqualAndOnline(minCapacity, isOnline);
        } else if (minCapacity == null && maxCapacity != null && isOnline != null) {
            conferences = conferenceRepository.findByCapacityLessThanEqualAndOnline(maxCapacity, isOnline);
        } else if (minCapacity != null && maxCapacity != null && isOnline != null) {
            conferences = conferenceRepository.findByCapacityBetweenAndOnline(minCapacity, maxCapacity, isOnline);
        } else {
            // If no filters are applied, return all conferences
            conferences = conferenceRepository.findAll();
        }
        // Create a list to hold the ConferenceOutDTO objects
        List<ConferenceOutDTO> conferenceOutDTOs = new ArrayList<>();
        // Map each Conference object to a ConferenceOutDTO object and add it to the list
        for (Conference conference : conferences) {
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(conference, ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(conference.getOrganizer(), PersonOutDTO.class));
            conferenceOutDTOs.add(conferenceOutDTO);
        }
        return conferenceOutDTOs;
    }

    public Optional<ConferenceOutDTO> getConferenceById(Long id) {
        Optional<Conference> conference = conferenceRepository.findById(id);
        if (conference.isPresent()) {
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(conference.get(), ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(conference.get().getOrganizer(), PersonOutDTO.class));
            return Optional.of(conferenceOutDTO);
        } else {
            throw new EntityNotFoundException("Conference not found with id: " + id);
        }
    }

    public ConferenceOutDTO createConference(ConferenceInDTO conferenceInDTO) throws EntityNotFoundException {
        // Check if the person and place exist before saving the conference
        Optional<Person> organizer = personRepository.findById(conferenceInDTO.getOrganizer());
        if (organizer.isEmpty()) {
            throw new EntityNotFoundException("Organizer not found with id: " + conferenceInDTO.getOrganizer());
        }
        Optional<Place> place = placeRepository.findById(conferenceInDTO.getPlace());
        if (place.isEmpty()) {
            throw new EntityNotFoundException("Place not found with id: " + conferenceInDTO.getPlace());
        }
        // Create a new Conference object and set its properties
        Conference conference = modelMapper.map(conferenceInDTO, Conference.class);
        logger.info("Creating new conference..... {}", conference);
        conference.setOrganizer(organizer.get());
        conference.setPlace(place.get());
        Conference createdConference = conferenceRepository.save(conference);
        // Map the created conference to ConferenceOutDTO and set the organizer
        ConferenceOutDTO conferenceOutDTO = modelMapper.map(createdConference, ConferenceOutDTO.class);
        conferenceOutDTO.setOrganizer(modelMapper.map(createdConference.getOrganizer(), PersonOutDTO.class));
        // Return the created conference as ConferenceOutDTO
        return conferenceOutDTO;
    }

    public ConferenceOutDTO updateConference(Long id, ConferenceInDTO conferenceInDTO) throws EntityNotFoundException {
        if (conferenceRepository.existsById(id)) {
            // Check if the person and place exist before saving the conference
            Optional<Person> organizer = personRepository.findById(conferenceInDTO.getOrganizer());
            if (organizer.isEmpty()) {
                throw new EntityNotFoundException("Organizer not found with id: " + conferenceInDTO.getOrganizer());
            }
            Optional<Place> place = placeRepository.findById(conferenceInDTO.getPlace());
            if (place.isEmpty()) {
                throw new EntityNotFoundException("Place not found with id: " + conferenceInDTO.getPlace());
            }
            // Create a new Conference object and set its properties
            Conference conference = modelMapper.map(conferenceInDTO, Conference.class);
            logger.info("Creating new conference..... {}", conference);
            conference.setOrganizer(organizer.get());
            conference.setPlace(place.get());
            conference.setId(id); // Set the ID of the existing conference
            Conference updatedConference = conferenceRepository.save(conference);
            // Map the updated conference to ConferenceOutDTO and set the organizer
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(updatedConference, ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(updatedConference.getOrganizer(), PersonOutDTO.class));
            // Return the updated conference as ConferenceOutDTO
            return conferenceOutDTO;
        } else {
            throw new EntityNotFoundException("Conference not found with id: " + id);
        }
    }

    public ConferenceOutDTO partialUpdateConference(Long id, ConferenceInDTO conferenceInDTO) throws EntityNotFoundException, IllegalArgumentException {
        Optional<Conference> existingConference = conferenceRepository.findById(id);
        if (existingConference.isPresent()) {
            Conference conference = existingConference.get();
            // Update only the fields that are present in the DTO
            if (conferenceInDTO.getName() != null) {
                if (conferenceInDTO.getName().isEmpty() || conferenceInDTO.getName().length() > 100) {
                    throw new IllegalArgumentException("Name cannot exceed 50 characters");
                }
                conference.setName(conferenceInDTO.getName());
            }
            if (conferenceInDTO.getCapacity() != null) {
                if (conferenceInDTO.getCapacity() <= 0) {
                    throw new IllegalArgumentException("Capacity must be greater than 0");
                }
                conference.setCapacity(conferenceInDTO.getCapacity());
            }
            if (conferenceInDTO.getBudget() != null) {
                if (conferenceInDTO.getBudget() <= 0) {
                    throw new IllegalArgumentException("Budget must be greater than 0");
                }
                conference.setBudget(conferenceInDTO.getBudget());
            }
            if (conferenceInDTO.getOnline() != null) {
                conference.setOnline(conferenceInDTO.getOnline());
            }
            if (conferenceInDTO.getStartDate() != null) {
                conference.setStartDate(conferenceInDTO.getStartDate());
            }
            if (conferenceInDTO.getPlace() != null) {
                Optional<Place> place = placeRepository.findById(conferenceInDTO.getPlace());
                if (place.isEmpty()) {
                    throw new EntityNotFoundException("Place not found with id: " + conferenceInDTO.getPlace());
                }
                conference.setPlace(place.get());
            }
            if (conferenceInDTO.getOrganizer() != null) {
                Optional<Person> organizer = personRepository.findById(conferenceInDTO.getOrganizer());
                if (organizer.isEmpty()) {
                    throw new EntityNotFoundException("Organizer not found with id: " + conferenceInDTO.getOrganizer());
                }
                conference.setOrganizer(organizer.get());
            }
            Conference updatedConference = conferenceRepository.save(conference);
            // Map the updated conference to ConferenceOutDTO and set the organizer
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(updatedConference, ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(updatedConference.getOrganizer(), PersonOutDTO.class));
            // Return the updated conference as ConferenceOutDTO
            return conferenceOutDTO;
        } else {
            throw new EntityNotFoundException("Conference not found with id: " + id);
        }
    }

    public void deleteConference(Long id) {
        conferenceRepository.deleteById(id);
    }

    public List<ConferenceOutDTO> getConferencesByPersonId(Long personId) throws EntityNotFoundException, IllegalArgumentException {
        // Check if the person ID is valid (not null and greater than 0) and exists in the database
        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("Invalid person ID: " + personId);
        }
        if (!personRepository.existsById(personId)) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        // Get all attendances for the personId
        List<Attendance> attendances = attendanceRepository.findByPersonId(personId);
        // Create a list to hold the conference IDs from the attendances
        List<Long> conferencesIds = new ArrayList<>();
        List<Conference> conferences = new ArrayList<>();
        List<ConferenceOutDTO> conferenceOutDTOs = new ArrayList<>();
        for (Attendance attendance : attendances) {
            // Get the person ID from each attendance and find the corresponding person
            Long conferenceId = attendance.getConference().getId();
            if (!conferencesIds.contains(conferenceId)) {
                conferencesIds.add(conferenceId);
                conferences.add(attendance.getConference());
                // Map each Conference object to a ConferenceOutDTO object and add it to the list
                ConferenceOutDTO conferenceOutDTO = modelMapper.map(attendance.getConference(), ConferenceOutDTO.class);
                conferenceOutDTO.setOrganizer(modelMapper.map(attendance.getConference().getOrganizer(), PersonOutDTO.class));
                conferenceOutDTOs.add(conferenceOutDTO);
            }
        }
        return conferenceOutDTOs;
    }

    public List<ConferenceOutDTO> getConferencesByOrganizerId(Long organizerId) throws EntityNotFoundException, IllegalArgumentException {
        // Check if the organizer ID is valid (not null and greater than 0) and exists in the database
        if (organizerId == null || organizerId <= 0) {
            throw new IllegalArgumentException("Invalid organizer ID: " + organizerId);
        }
        if (!personRepository.existsById(organizerId)) {
            throw new EntityNotFoundException("Organizer not found with id: " + organizerId);
        }
        // Get all conferences for the organizerId
        List<Conference> conferences = conferenceRepository.findByOrganizerId(organizerId);
        // Create a list to hold the ConferenceOutDTO objects
        List<ConferenceOutDTO> conferenceOutDTOs = new ArrayList<>();
        // Map each Conference object to a ConferenceOutDTO object and add it to the list
        for (Conference conference : conferences) {
            ConferenceOutDTO conferenceOutDTO = modelMapper.map(conference, ConferenceOutDTO.class);
            conferenceOutDTO.setOrganizer(modelMapper.map(conference.getOrganizer(), PersonOutDTO.class));
            conferenceOutDTOs.add(conferenceOutDTO);
        }
        return conferenceOutDTOs;
    }
}
