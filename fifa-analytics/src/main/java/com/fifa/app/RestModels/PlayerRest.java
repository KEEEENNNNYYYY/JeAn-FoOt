package com.fifa.app.RestModels;

import com.fifa.app.DTO.Club;
import com.fifa.app.Enum.Position;
import lombok.Data;

@Data
public class PlayerRest {
    private String id;
    private String name;
    private Integer number;
    private Position position;
    private String nationality;
    private Integer age;
    private Club club;
}
