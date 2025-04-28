package com.example.demo.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@Getter
@Setter
public class Player {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Country nationality;
    private Club club;

}
