package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Match;
import com.fifa.app.Services.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matchMaker")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{seasonYear}")
    public ResponseEntity<?> generateMatches(@PathVariable int seasonYear) {
        try {
            List<Match> matches = matchService.generateSeasonMatches(seasonYear);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
