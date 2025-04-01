package com.sanvalero.apieventos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
// import com.sanvalero.apieventos.domain.Conference;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Column(nullable = false)
    @Min(value = 0, message = "Duration must be greater than or equal to 0")
    private Integer duration;

    @Column
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean open;

    @Column(nullable = false)
    @NotNull(message = "Schedule cannot be null")
    private LocalDateTime schedule;

    @ManyToOne
    @NotNull(message = "Conference cannot be null")
    @JoinColumn(name = "conference_id", nullable = false, referencedColumnName = "id")
    private Conference conference;
}
