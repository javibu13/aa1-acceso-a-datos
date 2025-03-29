package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
// import com.sanvalero.apieventos.domain.Conference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer duration;

    @Column
    private Double price;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean open;

    @Column(nullable = false)
    private LocalDateTime schedule;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "conference_id", nullable = false, referencedColumnName = "id")
    private Conference conference;
}
