package com.sanvalero.apieventos.dto;

import com.sanvalero.apieventos.domain.Conference;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceOutDTO {
    private Long id;
    private String ticketCode;
    private Integer seatNumber;
    private Double ticketPrice;
    private Boolean checkedIn;
    private LocalDateTime registrationDate;
    private PersonOutDTO person;
    private Conference conference;
}
