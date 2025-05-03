package com.fifa.app.Entities;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Scorer {
    private Player player;
    private int minuteOfGoal;
    private boolean ownGoal;
}
