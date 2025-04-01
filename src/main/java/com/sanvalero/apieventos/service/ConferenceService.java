package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.dto.ConferenceInDTO;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.repository.PersonRepository;
import com.sanvalero.apieventos.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(ConferenceService.class);

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Conference> getAllConferences() {
        return conferenceRepository.findAll();
    }

    public List<Conference> getConferencesByFilters(Integer minCapacity, Integer maxCapacity, Boolean isOnline) {
        if (minCapacity != null && maxCapacity == null && isOnline == null) {
            return conferenceRepository.findByCapacityGreaterThanEqual(minCapacity);
        } else if (minCapacity == null && maxCapacity != null && isOnline == null) {
            return conferenceRepository.findByCapacityLessThanEqual(maxCapacity);
        } else if (minCapacity == null && maxCapacity == null && isOnline != null) {
            return conferenceRepository.findByOnline(isOnline);
        } else if (minCapacity != null && maxCapacity != null && isOnline == null) {
            return conferenceRepository.findByCapacityBetween(minCapacity, maxCapacity);
        } else if (minCapacity != null && maxCapacity == null && isOnline != null) {
            return conferenceRepository.findByCapacityGreaterThanEqualAndOnline(minCapacity, isOnline);
        } else if (minCapacity == null && maxCapacity != null && isOnline != null) {
            return conferenceRepository.findByCapacityLessThanEqualAndOnline(maxCapacity, isOnline);
        } else if (minCapacity != null && maxCapacity != null && isOnline != null) {
            return conferenceRepository.findByCapacityBetweenAndOnline(minCapacity, maxCapacity, isOnline);
        } else {
            // If no filters are applied, return all conferences
            return conferenceRepository.findAll();
        }
    }

    public Optional<Conference> getConferenceById(Long id) {
        return conferenceRepository.findById(id);
    }

    public Conference createConference(ConferenceInDTO conferenceInDTO) throws EntityNotFoundException {
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
        return conferenceRepository.save(conference);
    }

    public Conference updateConference(Long id, ConferenceInDTO conferenceInDTO) throws EntityNotFoundException {
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
            return conferenceRepository.save(conference);
        } else {
            throw new EntityNotFoundException("Conference not found with id: " + id);
        }
    }

    public void deleteConference(Long id) {
        conferenceRepository.deleteById(id);
    }
}
