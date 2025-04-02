package com.sanvalero.apieventos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInDTO {
    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Min(value = 0, message = "Duration must be greater than or equal to 0")
    private Integer duration;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    private Boolean open;

    @NotNull(message = "Schedule cannot be null")
    private LocalDateTime schedule;

    @NotNull(message = "Conference cannot be null")
    private Long conference;
}
