package com.sanvalero.apieventos.service;

import com.sanvalero.apieventos.domain.Activity;
import com.sanvalero.apieventos.repository.ActivityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity updateActivity(Long id, Activity activity) {
        if (activityRepository.existsById(id)) {
            activity.setId(id);
            return activityRepository.save(activity);
        } else {
            return null;
        }
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
}
