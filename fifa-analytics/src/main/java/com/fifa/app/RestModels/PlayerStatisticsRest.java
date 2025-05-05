package com.fifa.app.RestModels;

import com.fifa.app.DTO.PlayingTime;
import lombok.Data;

@Data
public class PlayerStatisticsRest {
    private String playerId;
  private Integer seasonYear;
  private Integer scoredGoals;
  private PlayingTime playingTime;
}
