package com.fifa.app.DTO;

import lombok.Data;

@Data
public class ClubStatistics {
    private Club club;
    private Integer rankingPoints;
    private Integer scoredGoals;
    private Integer concededGoals;
    private Integer differenceGoals;
    private Integer cleanSheetNumber;
}
