package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
// import com.sanvalero.apieventos.domain.Person;
// import com.sanvalero.apieventos.domain.Conference;

import lombok.AllArgsConstructor;
import lombok.Data;
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
    private String ticketCode;

    private Integer seatNumber;

    @Column
    private Double ticketPrice;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean checkedIn;

    @Column(name = "registration_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registrationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id", nullable = false, referencedColumnName = "id")
    private Person person;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conference_id", nullable = false, referencedColumnName = "id")
    private Conference conference;
}
