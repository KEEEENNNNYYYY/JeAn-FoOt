package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Season;
import com.fifa.app.Entities.SeasonStatus;
import com.fifa.app.Services.SeasonService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<List<Season>> createOrUpdate(@RequestBody List<Season> season) {
        List<Season> updatedPlayers = seasonService.createSeason(season);
        return ResponseEntity.ok(updatedPlayers);
    }

    @PutMapping("/{seasonYear}/status")
    public ResponseEntity<Season> updateSeasonStatus(
        @PathVariable int seasonYear,
        @RequestBody SeasonStatus request
    ) {
        try {
            Season updatedSeason = seasonService.updateSeasonStatus(seasonYear, SeasonStatus.valueOf(request.name()));
            return ResponseEntity.ok(updatedSeason);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
