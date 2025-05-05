package com.fifa.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fifa.app.Enum.Championship;
import lombok.Data;

import java.util.List;

@Data
public class Club {
    private String id;
    private String name;
    private String acronym;
    private Integer yearCreation;
    private String stadium;
    private Coach coach;
    private Championship championship;
    private List<ClubStat> clubStats;
}
