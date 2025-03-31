package com.sanvalero.apieventos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonOutDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private Double height;
    private String interests;
    private LocalDate birthDate;
    private Boolean verified;
}