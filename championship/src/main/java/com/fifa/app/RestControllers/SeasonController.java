package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Player;
import com.fifa.app.Entities.Season;
import com.fifa.app.Services.SeasonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seasons")
@AllArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    public ResponseEntity<List<Season>> getAll() {
        List<Season> players = seasonService.getAll();
        return ResponseEntity.ok(players);
    }

}
