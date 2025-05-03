package com.fifa.app.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fifa.app.Enum.Position;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Player{
    private String id;
    private String name;
    private Integer number;
    private Position position;
    private String nationality;
    private Integer age;
    private Club club;
    private PlayerStatistics playerStatistics;
}
