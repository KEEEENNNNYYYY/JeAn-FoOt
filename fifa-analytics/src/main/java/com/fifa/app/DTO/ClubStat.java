package com.fifa.app.DTO;

import lombok.Data;

@Data
public class ClubStat {
    private Club club;
    private Season season;
    private Integer rankingPoints;
    private Integer scoredGoals;
    private Integer concededGoals;
    private Integer differenceGoals;
    private Integer cleanSheetNumber;
}
