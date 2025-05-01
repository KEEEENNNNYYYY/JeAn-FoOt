package com.fifa.app.DTO;

import com.fifa.app.Enum.Status;
import lombok.Data;

@Data
public class Season {
    private String id;
    private Integer year;
    private String alias;
    private Status status;
}
