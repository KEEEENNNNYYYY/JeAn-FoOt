package com.fifa.app.Entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Season {
    private String id;
    private int year; // 2024-2025
    private String alias;
    private SeasonStatus status;
}
