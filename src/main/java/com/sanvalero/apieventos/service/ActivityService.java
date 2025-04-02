package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Activity;
import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.dto.ActivityInDTO;
import com.sanvalero.apieventos.repository.ActivityRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getActivitiesByFilters(Integer minDuration, Integer maxDuration, Boolean isOpen) {
        if (minDuration != null && maxDuration == null && isOpen == null) {
            return activityRepository.findByDurationGreaterThanEqual(minDuration);
        } else if (minDuration == null && maxDuration != null && isOpen == null) {
            return activityRepository.findByDurationLessThanEqual(maxDuration);
        } else if (minDuration == null && maxDuration == null && isOpen != null) {
            return activityRepository.findByOpen(isOpen);
        } else if (minDuration != null && maxDuration != null && isOpen == null) {
            return activityRepository.findByDurationBetween(minDuration, maxDuration);
        } else if (minDuration != null && maxDuration == null && isOpen != null) {
            return activityRepository.findByDurationGreaterThanEqualAndOpen(minDuration, isOpen);
        } else if (minDuration == null && maxDuration != null && isOpen != null) {
            return activityRepository.findByDurationLessThanEqualAndOpen(maxDuration, isOpen);
        } else if (minDuration != null && maxDuration != null && isOpen != null) {
            return activityRepository.findByDurationBetweenAndOpen(minDuration, maxDuration, isOpen);
        } else {
            // If no filters are applied, return all activities
            return activityRepository.findAll();
        }
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity createActivity(ActivityInDTO activityInDTO) {
        // Check if the conference exists
        Optional<Conference> conference = conferenceRepository.findById(activityInDTO.getConference());
        if (conference.isEmpty()) {
            throw new EntityNotFoundException("Conference not found with ID: " + activityInDTO.getConference());
        }
        // Map the DTO to the entity
        Activity activity = modelMapper.map(activityInDTO, Activity.class);
        activity.setConference(conference.get());
        return activityRepository.save(activity);
    }

    public Activity updateActivity(Long id, ActivityInDTO activityInDTO) {
        if (activityRepository.existsById(id)) {
            // Check if the conference exists
            Optional<Conference> conference = conferenceRepository.findById(activityInDTO.getConference());
            if (conference.isEmpty()) {
                throw new EntityNotFoundException("Conference not found with ID: " + activityInDTO.getConference());
            }
            // Map the DTO to the entity
            Activity activity = modelMapper.map(activityInDTO, Activity.class);
            activity.setConference(conference.get());
            activity.setId(id);
            return activityRepository.save(activity);
        } else {
            throw new EntityNotFoundException("Activity not found with ID: " + id);
        }
    }

    public Activity partialUpdateActivity(Long id, ActivityInDTO activityInDTO) throws EntityNotFoundException, IllegalArgumentException {
        Optional<Activity> existingActivity = activityRepository.findById(id);
        if (existingActivity.isPresent()) {
            Activity activity = existingActivity.get();
            // Update only the fields that are present in the DTO
            if (activityInDTO.getTitle() != null) {
                if (activityInDTO.getTitle().isEmpty() ||activityInDTO.getTitle().length() > 255) {
                    throw new IllegalArgumentException("Title cannot exceed 50 characters");
                }
                activity.setTitle(activityInDTO.getTitle());
            }
            if (activityInDTO.getDuration() != null) {
                if (activityInDTO.getDuration() <= 0) {
                    throw new IllegalArgumentException("Duration must be greater than 0");
                }
                activity.setDuration(activityInDTO.getDuration());
            }
            if (activityInDTO.getPrice() != null) {
                if (activityInDTO.getPrice() < 0) {
                    throw new IllegalArgumentException("Price cannot be negative");
                }
                activity.setPrice(activityInDTO.getPrice());
            }
            if (activityInDTO.getOpen() != null) {
                activity.setOpen(activityInDTO.getOpen());
            }
            if (activityInDTO.getSchedule() != null) {
                activity.setSchedule(activityInDTO.getSchedule());
            }
            if (activityInDTO.getConference() != null) {
                // Check if the conference exists
                Optional<Conference> conference = conferenceRepository.findById(activityInDTO.getConference());
                if (conference.isEmpty()) {
                    throw new EntityNotFoundException("Conference not found with ID: " + activityInDTO.getConference());
                }
                activity.setConference(conference.get());
            }
            return activityRepository.save(activity);
        } else {
            throw new EntityNotFoundException("Activity not found with ID: " + id);
        }
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
}
