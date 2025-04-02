package com.sanvalero.apieventos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.sanvalero.apieventos.domain.Place;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceOutDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private Double budget;
    private Boolean online;
    private LocalDate startDate;
    private Place place;
    private PersonOutDTO organizer;
}