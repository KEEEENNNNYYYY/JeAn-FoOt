package com.fifa.app.Entities;

import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Country;
import lombok.AllArgsConstructor;
import com.fifa.app.Entities.Nationality;
import com.fifa.app.Entities.Position;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@Getter
@Setter
public class PlayerDTO {
    private String id;
    private String name;
    private int number;
    private Position position;
    private Nationality nationality;
    private Club club;
    private int age;

}