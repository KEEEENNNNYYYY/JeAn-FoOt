package com.fifa.app.DTO;

import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

@Data
public class PlayingTime {
    private Integer value;
    private DurationUnit durationUnit;
}
