package com.sanvalero.apieventos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceInDTO {
    @NotNull(message = "Seat number cannot be null")
    @Min(value = 0, message = "Seat number cannot be negative")
    private Integer seatNumber;

    @Min(value = 0, message = "Ticket price cannot be negative")
    @NotNull(message = "Ticket price cannot be null")
    private Double ticketPrice;

    @NotNull(message = "Checked-in cannot be null")
    private Boolean checkedIn;
    
    @FutureOrPresent(message = "Registration date must be in the present or future")
    private LocalDateTime registrationDate;
    
    @NotNull(message = "Person cannot be null")
    @Min(value = 0, message = "Person ID cannot be negative")
    private Long person;
    
    @NotNull(message = "Conference cannot be null")
    @Min(value = 0, message = "Conference ID cannot be negative")
    private Long conference;
}
