package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.repository.ConferenceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConferenceService {
    @Autowired
    private ConferenceRepository conferenceRepository;

    public List<Conference> getAllConferences() {
        return conferenceRepository.findAll();
    }

    public Optional<Conference> getConferenceById(Long id) {
        return conferenceRepository.findById(id);
    }

    public Conference createConference(Conference conference) {
        return conferenceRepository.save(conference);
    }

    public Conference updateConference(Long id, Conference conference) {
        if (conferenceRepository.existsById(id)) {
            conference.setId(id);
            return conferenceRepository.save(conference);
        } else {
            return null;
        }
    }

    public void deleteConference(Long id) {
        conferenceRepository.deleteById(id);
    }
}
