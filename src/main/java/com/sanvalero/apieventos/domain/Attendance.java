package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Ticket code cannot be null")
    @NotBlank(message = "Ticket code cannot be blank")
    private String ticketCode;

    @Column(name = "seat_number")
    @Min(value = 0, message = "Seat number cannot be negative")
    private Integer seatNumber;

    @Column(name = "ticket_price")
    @Min(value = 0, message = "Ticket price cannot be negative")
    private Double ticketPrice;

    @Column(name = "checked_in", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean checkedIn;

    @Column(name = "registration_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registrationDate;

    @ManyToOne
    @NotNull(message = "Person cannot be null")
    @JoinColumn(name = "person_id", nullable = false, referencedColumnName = "id")
    private Person person;

    @ManyToOne
    @NotNull(message = "Conference cannot be null")
    @JoinColumn(name = "conference_id", nullable = false, referencedColumnName = "id")
    private Conference conference;
}
