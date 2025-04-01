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
@Entity(name = "conference")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column
    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private Integer capacity;

    @Column
    @Min(value = 0, message = "Budget must be greater than or equal to 0")
    private Double budget;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean online;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @ManyToOne
    @NotNull(message = "Place cannot be null")
    @JoinColumn(name = "place_id", nullable = false, referencedColumnName = "id")
    private Place place;

    @ManyToOne
    @NotNull(message = "Organizer cannot be null")
    @JoinColumn(name = "organizer_id", nullable = false, referencedColumnName = "id")
    private Person organizer;
}
