package com.example.demo.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Club {
    private int id;
    private String name;
    private LocalDate createDate;
    private String acronym;
    private String country_id; // MDG pour madagascar ohatra ou jsp


}
