package com.fifa.app.Entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlayerCriteria {
    private String name;
    private Integer ageMinimum;
    private Integer ageMaximum;
    private String clubName;

}
