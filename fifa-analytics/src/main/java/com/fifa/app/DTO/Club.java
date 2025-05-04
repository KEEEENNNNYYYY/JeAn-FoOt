package com.fifa.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fifa.app.Enum.Championship;
import lombok.Data;

@Data
public class Club {
    private String id;
    private String name;
    private String acronym;
    private Integer yearCreation;
    private String stadium;
    private Coach coach;
    private Integer rankingPoints;
    private Integer scoredGoals;
    private Integer concededGoals;
    private Integer differenceGoals;
    private Integer cleanSheetNumber;
    @JsonIgnore
    private Championship championship;
}
