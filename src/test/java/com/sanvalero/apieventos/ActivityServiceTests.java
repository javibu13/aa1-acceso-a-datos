package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.sanvalero.apieventos.domain.Activity;
import com.sanvalero.apieventos.domain.Conference;
import com.sanvalero.apieventos.domain.Person;
import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.dto.ActivityInDTO;
import com.sanvalero.apieventos.repository.ActivityRepository;
import com.sanvalero.apieventos.repository.ConferenceRepository;
import com.sanvalero.apieventos.service.ActivityService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTests {

    @InjectMocks
    private ActivityService activityService;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetAllActivities() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity1 = new Activity(1L, "Activity 1", 60, 50.0, true, LocalDateTime.now(), conference);
        Activity activity2 = new Activity(2L, "Activity 2", 90, 75.0, false, LocalDateTime.now().plusHours(2), conference);
        // Mock the repository method
        when(activityRepository.findAll()).thenReturn(List.of(activity1, activity2));
        // Call the method to test
        List<Activity> result = activityService.getAllActivities();
        // Verify the result
        assertEquals(2, result.size());
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void testGetActivitiesByFiltersMinDuration() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Long Activity", 120, 100.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findByDurationGreaterThanEqual(100)).thenReturn(List.of(activity));
        // Call the method to test
        List<Activity> result = activityService.getActivitiesByFilters(100, null, null);
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(120, result.getFirst().getDuration());
        verify(activityRepository, times(1)).findByDurationGreaterThanEqual(100);
    }

    @Test
    void testGetActivitiesByFiltersMaxDuration() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Short Activity", 30, 25.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findByDurationLessThanEqual(60)).thenReturn(List.of(activity));
        // Call the method to test
        List<Activity> result = activityService.getActivitiesByFilters(null, 60, null);
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(30, result.getFirst().getDuration());
        verify(activityRepository, times(1)).findByDurationLessThanEqual(60);
    }

    @Test
    void testGetActivitiesByFiltersIsOpen() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Open Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findByOpen(true)).thenReturn(List.of(activity));
        // Call the method to test
        List<Activity> result = activityService.getActivitiesByFilters(null, null, true);
        // Verify the result
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getOpen());
        verify(activityRepository, times(1)).findByOpen(true);
    }

    @Test
    void testGetActivitiesByFiltersMinDurationMaxDurationIsOpen() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Filtered Activity", 90, 75.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findByDurationBetweenAndOpen(60, 120, true)).thenReturn(List.of(activity));
        // Call the method to test
        List<Activity> result = activityService.getActivitiesByFilters(60, 120, true);
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(90, result.getFirst().getDuration());
        assertTrue(result.getFirst().getOpen());
        verify(activityRepository, times(1)).findByDurationBetweenAndOpen(60, 120, true);
    }

    @Test
    void testGetActivityById() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Test Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        // Call the method to test
        Optional<Activity> result = activityService.getActivityById(1L);
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals("Test Activity", result.get().getTitle());
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateActivity() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO("New Activity", 60, 50.0, true, LocalDateTime.now(), 1L);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "New Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));
        when(modelMapper.map(inDTO, Activity.class)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        // Call the method to test
        Activity result = activityService.createActivity(inDTO);
        // Verify the result
        assertNotNull(result);
        assertEquals("New Activity", result.getTitle());
        assertEquals(60, result.getDuration());
        verify(activityRepository, times(1)).save(activity);
    }

    @Test
    void testCreateActivityConferenceNotFound() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO();
        inDTO.setConference(1L);
        // Mock the repository method to return an empty Optional
        when(conferenceRepository.findById(1L)).thenReturn(Optional.empty());
        // Call the method to test and expect an exception
        try {
            activityService.createActivity(inDTO);
        } catch (EntityNotFoundException e) {
            // Verify the exception message
            assertEquals("Conference not found with ID: 1", e.getMessage());
            // Verify the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    void testUpdateActivity() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO();
        inDTO.setTitle("Updated Activity");
        inDTO.setDuration(90);
        inDTO.setPrice(75.0);
        inDTO.setOpen(false);
        inDTO.setSchedule(LocalDateTime.now().plusHours(2));
        inDTO.setConference(1L);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Updated Activity", 90, 75.0, false, LocalDateTime.now().plusHours(2), conference);
        // Mock the repository methods
        when(activityRepository.existsById(1L)).thenReturn(true);
        when(conferenceRepository.findById(1L)).thenReturn(Optional.of(conference));
        when(modelMapper.map(inDTO, Activity.class)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        // Call the method to test
        Activity result = activityService.updateActivity(1L, inDTO);
        // Verify the result
        assertNotNull(result);
        assertEquals("Updated Activity", result.getTitle());
        assertEquals(90, result.getDuration());
        verify(activityRepository, times(1)).save(activity);
    }

    @Test
    void testUpdateActivityNotFound() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO();
        inDTO.setConference(1L);
        // Mock the repository method
        when(activityRepository.existsById(1L)).thenReturn(false);
        // Call the method to test and expect an exception
        try {
            activityService.updateActivity(1L, inDTO);
        } catch (EntityNotFoundException e) {
            // Verify the exception message
            assertEquals("Activity not found with ID: 1", e.getMessage());
            // Verify the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        // Verify that the conference repository was not called
        verify(conferenceRepository, never()).findById(anyLong());
    }

    @Test
    void testPartialUpdateActivity() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO();
        inDTO.setTitle("Partially Updated");
        inDTO.setDuration(75);
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity existingActivity = new Activity(1L, "Original Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        Activity updatedActivity = new Activity(1L, "Partially Updated", 75, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository methods
        when(activityRepository.findById(1L)).thenReturn(Optional.of(existingActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(updatedActivity);
        // Call the method to test
        Activity result = activityService.partialUpdateActivity(1L, inDTO);
        // Verify the result
        assertNotNull(result);
        assertEquals("Partially Updated", result.getTitle());
        assertEquals(75, result.getDuration());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void testPartialUpdateActivityInvalidData() {
        // Create data for the test
        ActivityInDTO inDTO = new ActivityInDTO();
        inDTO.setDuration(-10); // Invalid negative duration
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity existingActivity = new Activity(1L, "Original Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository method
        when(activityRepository.findById(1L)).thenReturn(Optional.of(existingActivity));
        // Call the method to test and expect an exception
        try {
            activityService.partialUpdateActivity(1L, inDTO);
        } catch (IllegalArgumentException e) {
            // Verify the exception message
            assertEquals("Duration must be greater than 0", e.getMessage());
            // Verify the exception type
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        // Verify that the save method was not called
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void testDeleteActivity() {
        // Not needed for this test case
    }

    @Test
    void testGetActivitiesByConferenceId() {
        // Create data for the test
        Person organizer = new Person(1L, "Organizer", "Test", "org@test.com", "pass", 30, 1.75, "Tech", LocalDate.now(), true);
        Place place = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.now(), true, "Equipment");
        Conference conference = new Conference(1L, "Test Conference", 100, 1000.0, false, LocalDateTime.now().toLocalDate(), place, organizer);
        Activity activity = new Activity(1L, "Conference Activity", 60, 50.0, true, LocalDateTime.now(), conference);
        // Mock the repository methods        
        when(conferenceRepository.existsById(1L)).thenReturn(true);
        when(activityRepository.findByConferenceId(1L)).thenReturn(List.of(activity));
        // Call the method to test
        List<Activity> result = activityService.getActivitiesByConferenceId(1L);
        // Verify the result
        assertEquals(1, result.size());
        assertEquals("Conference Activity", result.getFirst().getTitle());
        verify(activityRepository, times(1)).findByConferenceId(1L);
    }

    @Test
    void testGetActivitiesByConferenceIdNotFound() {
        // Mock the repository method to return false for conference existence check
        when(conferenceRepository.existsById(1L)).thenReturn(false);
        // Call the method to test and expect an exception
        try {
            activityService.getActivitiesByConferenceId(1L);
        } catch (EntityNotFoundException e) {
            // Verify the exception message
            assertEquals("Conference not found with ID: 1", e.getMessage());
            // Verify the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        // Verify that the activity repository was not called
        verify(activityRepository, never()).findByConferenceId(anyLong());
    }
}