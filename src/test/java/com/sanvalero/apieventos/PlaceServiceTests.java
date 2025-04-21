package com.sanvalero.apieventos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sanvalero.apieventos.domain.Place;
import com.sanvalero.apieventos.repository.PlaceRepository;
import com.sanvalero.apieventos.service.PlaceService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTests {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    public void testGetAllPlaces() {
        // Create a list of places to be returned by the mock repository
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), false, "Whiteboard, Chairs"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock the repository to return the list of places
        when(placeRepository.findAll()).thenReturn(mockPlaces);
        // Call the service method
        List<Place> result = placeService.getAllPlaces();
        // Check the size of the result
        assertEquals(3, result.size());
        // Check the first place
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Place 1", result.getFirst().getName());
        assertEquals("Address 1", result.getFirst().getAddress());
        // Check the last place
        assertEquals(3L, result.getLast().getId());
        assertEquals("Place 3", result.getLast().getName());
        assertEquals("Address 3", result.getLast().getAddress());
        // Verify that the methods were called the expected number of times
        verify(placeRepository, times(1)).findAll();
    }

    @Test
    public void testGetPlacesByFiltersMinCapacity() {
        // Create a list of places to be returned by the mock repository
        List<Place> mockPlaces = List.of(
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), false, "Whiteboard, Chairs"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock the repository to return the list of places
        when(placeRepository.findByCapacityGreaterThanEqual(200)).thenReturn(mockPlaces);
        // Call the service method
        List<Place> result = placeService.getPlacesByFilters(200, null, null);
        // Check the size of the result
        assertEquals(2, result.size());
        // Check the first place
        assertEquals(2L, result.getFirst().getId());
        assertEquals("Place 2", result.getFirst().getName());
        assertEquals(200, result.getFirst().getCapacity());
        // Verify that the correct repository method was called
        verify(placeRepository, times(1)).findByCapacityGreaterThanEqual(200);
        verify(placeRepository, times(0)).findByCapacityLessThanEqual(anyInt());
        verify(placeRepository, times(0)).findByHasParking(anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetween(anyInt(), anyInt());
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityLessThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetweenAndHasParking(anyInt(), anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findAll();
    }

    @Test
    public void testGetPlacesByFiltersMaxCapacity() {
        // Create a list of places to be returned by the mock repository
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(2L, "Place 2", "Address 2", 200, 300.0, LocalDate.of(2021, 1, 1), false, "Whiteboard, Chairs")
        );
        // Mock the repository to return the list of places
        when(placeRepository.findByCapacityLessThanEqual(200)).thenReturn(mockPlaces);
        // Call the service method
        List<Place> result = placeService.getPlacesByFilters(null, 200, null);
        // Check the size of the result
        assertEquals(2, result.size());
        // Check the last place
        assertEquals(2L, result.getLast().getId());
        assertEquals("Place 2", result.getLast().getName());
        assertEquals(200, result.getLast().getCapacity());
        // Verify that the correct repository method was called
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqual(anyInt());
        verify(placeRepository, times(1)).findByCapacityLessThanEqual(200);
        verify(placeRepository, times(0)).findByHasParking(anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetween(anyInt(), anyInt());
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityLessThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetweenAndHasParking(anyInt(), anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findAll();
    }

    @Test
    public void testGetPlacesByFiltersHasParking() {
        // Create a list of places to be returned by the mock repository
        List<Place> mockPlaces = List.of(
            new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"),
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock the repository to return the list of places
        when(placeRepository.findByHasParking(true)).thenReturn(mockPlaces);
        // Call the service method
        List<Place> result = placeService.getPlacesByFilters(null, null, true);
        // Check the size of the result
        assertEquals(2, result.size());
        // Check that all places have parking
        assertTrue(result.getFirst().getHasParking());
        assertTrue(result.getLast().getHasParking());
        // Verify that the correct repository method was called
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqual(anyInt());
        verify(placeRepository, times(0)).findByCapacityLessThanEqual(anyInt());
        verify(placeRepository, times(1)).findByHasParking(true);
        verify(placeRepository, times(0)).findByCapacityBetween(anyInt(), anyInt());
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityLessThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetweenAndHasParking(anyInt(), anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findAll();
    }

    @Test
    public void testGetPlacesByFiltersMinCapacityMaxCapacityParking() {
        // Create a list of places to be returned by the mock repository
        List<Place> mockPlaces = List.of(
            new Place(3L, "Place 3", "Address 3", 300, 400.0, LocalDate.of(2022, 1, 1), true, "Microphones, Tables")
        );
        // Mock the repository to return the list of places
        when(placeRepository.findByCapacityBetweenAndHasParking(200, 400, true)).thenReturn(mockPlaces);
        // Call the service method
        List<Place> result = placeService.getPlacesByFilters(200, 400, true);
        // Check the size of the result
        assertEquals(1, result.size());
        // Check the place
        assertEquals(3L, result.getFirst().getId());
        assertEquals(300, result.getFirst().getCapacity());
        assertTrue(result.getFirst().getHasParking());
        // Verify that the correct repository method was called
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqual(anyInt());
        verify(placeRepository, times(0)).findByCapacityLessThanEqual(anyInt());
        verify(placeRepository, times(0)).findByHasParking(anyBoolean());
        verify(placeRepository, times(0)).findByCapacityBetween(anyInt(), anyInt());
        verify(placeRepository, times(0)).findByCapacityGreaterThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(0)).findByCapacityLessThanEqualAndHasParking(anyInt(), anyBoolean());
        verify(placeRepository, times(1)).findByCapacityBetweenAndHasParking(200, 400, true);
        verify(placeRepository, times(0)).findAll();
    }

    @Test
    public void testGetPlaceById() {
        // Create a Optional place to be returned by the mock repository
        Optional<Place> mockPlace = Optional.of(new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System"));
        // Mock the repository to return the place
        when(placeRepository.findById(1L)).thenReturn(mockPlace);
        // Call the service method
        Optional<Place> result = placeService.getPlaceById(1L);
        // Check the result
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Place 1", result.get().getName());
        assertEquals("Address 1", result.get().getAddress());
        verify(placeRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreatePlace() {
        // Create a place to be saved
        Place mockPlace = new Place(1L, "Place 1", "Address 1", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock the repository to return the saved place
        when(placeRepository.save(mockPlace)).thenReturn(mockPlace);
        // Call the service method
        Place result = placeService.createPlace(mockPlace);
        // Check the result
        assertEquals(1L, result.getId());
        assertEquals("Place 1", result.getName());
        assertEquals("Address 1", result.getAddress());
        verify(placeRepository, times(1)).save(mockPlace);
    }

    @Test
    public void testUpdatePlace() {
        // Create the id of the place to be updated
        Long id = 1L;
        // Create a place to be updated
        Place mockPlace = new Place(id, "Place Updated", "Address Updated", 150, 250.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock the repository to return true for existsById
        when(placeRepository.existsById(id)).thenReturn(true);
        // Mock the repository to return the saved place
        when(placeRepository.save(mockPlace)).thenReturn(mockPlace);
        // Call the service method
        Place result = placeService.updatePlace(id, mockPlace);
        // Check the result
        assertEquals(id, result.getId());
        assertEquals("Place Updated", result.getName());
        assertEquals("Address Updated", result.getAddress());
        verify(placeRepository, times(1)).existsById(id);
        verify(placeRepository, times(1)).save(mockPlace);
    }

    @Test
    public void testUpdatePlaceNotFound() {
        // Create the id of the place to be updated
        Long id = 1L;
        // Create a place to be updated
        Place mockPlace = new Place(id, "Place Updated", "Address Updated", 150, 250.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Mock the repository to return false for existsById
        when(placeRepository.existsById(id)).thenReturn(false);
        // Call the service method and verify that it throws the expected exception
        try {
            placeService.updatePlace(id, mockPlace);
        } catch (EntityNotFoundException e) {
            // Check the exception message
            assertEquals("Place not found with id: " + id, e.getMessage());
            // Check the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }
        verify(placeRepository, times(1)).existsById(id);
        verify(placeRepository, times(0)).save(any(Place.class));
    }

    @Test
    public void testPartialUpdatePlace() {
        // Create the id of the place to be partially updated
        Long id = 1L;
        // Create an existing place
        Place existingPlace = new Place(id, "Place Original", "Address Original", 100, 200.0, LocalDate.of(2020, 1, 1), true, "Projector, Sound System");
        // Create a place with partial updates
        Place partialUpdates = new Place();
        partialUpdates.setName("Place Updated");
        partialUpdates.setCapacity(150);
        // Mock the repository to return the existing place
        when(placeRepository.findById(id)).thenReturn(Optional.of(existingPlace));
        // Mock the repository to return the saved place
        when(placeRepository.save(any(Place.class))).thenReturn(existingPlace);
        // Call the service method
        Place result = placeService.partialUpdatePlace(id, partialUpdates);
        // Check the result
        assertEquals(id, result.getId());
        assertEquals("Place Updated", result.getName());
        assertEquals("Address Original", result.getAddress());
        assertEquals(150, result.getCapacity());
        assertEquals(200.0, result.getArea());
        verify(placeRepository, times(1)).findById(id);
        verify(placeRepository, times(1)).save(any(Place.class));
    }

    @Test
    public void testPartialUpdatePlaceNotFound() {
        // Create the id of the place to be partially updated
        Long id = 1L;
        // Create a place with partial updates
        Place partialUpdates = new Place();
        partialUpdates.setName("Place Updated");
        // Mock the repository to return empty optional
        when(placeRepository.findById(id)).thenReturn(Optional.empty());
        // Call the service method and verify that it throws the expected exception
        try {
            placeService.partialUpdatePlace(id, partialUpdates);
        } catch (EntityNotFoundException e) {
            // Check the exception message
            assertEquals("Place not found with id: " + id, e.getMessage());
            // Check the exception type
            assertEquals(EntityNotFoundException.class, e.getClass());
        }        
        verify(placeRepository, times(1)).findById(id);
        verify(placeRepository, times(0)).save(any(Place.class));
    }

    @Test
    public void testDeletePlace() {
        // Not needed for this test case
    }
}