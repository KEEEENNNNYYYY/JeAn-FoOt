package com.fifa.app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PlayerStatistics {
    private Player player;
    private Integer scoredGoals;
    private PlayingTime playingTime;
    private Integer season;
}
