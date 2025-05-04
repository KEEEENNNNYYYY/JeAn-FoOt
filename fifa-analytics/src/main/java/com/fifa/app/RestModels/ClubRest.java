package com.fifa.app.RestModels;

import com.fifa.app.DTO.Coach;
import lombok.Data;

@Data
public class ClubRest {
    private String id;
    private String name;
    private String acronym;
    private Integer yearCreation;
    private String stadium;
    private Coach coach;
}
