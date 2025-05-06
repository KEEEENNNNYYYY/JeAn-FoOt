package com.fifa.app.DTO;

import com.fifa.app.Enum.DurationUnit;
import lombok.Data;

@Data
public class PlayingTime {
    private Double value;
    private DurationUnit durationUnit;
}
