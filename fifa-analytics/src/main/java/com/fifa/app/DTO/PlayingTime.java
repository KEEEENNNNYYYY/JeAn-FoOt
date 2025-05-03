package com.fifa.app.DTO;

import com.fifa.app.Enum.DurationUnit;
import lombok.Data;

@Data
public class PlayingTime {
    private Integer value;
    private DurationUnit durationUnit;
}
