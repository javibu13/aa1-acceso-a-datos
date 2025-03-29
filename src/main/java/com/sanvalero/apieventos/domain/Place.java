package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "palce")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    private Integer capacity;

    @Column
    private Double area;

    @Column(name = "inauguration_date")
    private LocalDate inaugurationDate;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean hasParking;

    @Lob
    private String equipment;
}
