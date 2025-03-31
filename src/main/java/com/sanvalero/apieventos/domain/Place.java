package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(nullable = false, length = 200)
    @NotNull(message = "Address cannot be null")
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Column
    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private Integer capacity;

    @Column
    @Min(value = 0, message = "Area must be greater than or equal to 0")
    private Double area;

    @Column(name = "inauguration_date")
    private LocalDate inaugurationDate;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean hasParking;

    @Column
    private String equipment;
}
