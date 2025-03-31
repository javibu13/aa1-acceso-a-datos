package com.sanvalero.apieventos.domain;

// import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "first_name")
    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    private String pass;

    @Column(nullable = false)
    @NotNull(message = "Age cannot be null")
    private Integer age;

    @Column
    @Min(value = 0, message = "Height must be greater than or equal to 0")
    @Max(value = 3, message = "Height must be less than or equal to 3")
    private Double height;

    @Column
    private String interests;

    @Column(name = "birth_date")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verified;
}
