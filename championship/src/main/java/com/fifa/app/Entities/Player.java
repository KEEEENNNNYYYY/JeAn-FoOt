package com.fifa.app.Entities;

import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Country;
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
    private Country country;
    private Club club;
    private int number;

}
