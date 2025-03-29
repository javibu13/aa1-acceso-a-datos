package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
// import com.sanvalero.apieventos.domain.Place;
// import com.sanvalero.apieventos.domain.Person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "conference")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer capacity;

    @Column
    private Double budget;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean online;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "place_id", nullable = false, referencedColumnName = "id")
    private Place place;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organizer_id", nullable = false, referencedColumnName = "id")
    private Person organizer;
}
