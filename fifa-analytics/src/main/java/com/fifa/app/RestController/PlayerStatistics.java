package com.fifa.app.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/players")
public class PlayerStatistics {

    @GetMapping
    public Object getBestPlayers(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        return new ArrayList<String>().add("Player Statistics");
    }
}
