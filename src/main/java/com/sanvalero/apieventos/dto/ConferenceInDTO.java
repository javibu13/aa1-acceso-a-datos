package com.sanvalero.apieventos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceInDTO {
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private Integer capacity;

    @Min(value = 0, message = "Budget must be greater than or equal to 0")
    private Double budget;

    private Boolean online;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "Place ID cannot be null")
    @Min(value = 1, message = "Place ID must be greater than or equal to 1")
    private Long place;

    @NotNull(message = "Organizer ID cannot be null")
    @Min(value = 1, message = "Organizer ID must be greater than or equal to 1")
    private Long organizer;
}