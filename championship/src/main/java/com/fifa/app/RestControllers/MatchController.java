package com.fifa.app.RestControllers;

import com.fifa.app.DTO.MatchDisplayDTO;
import com.fifa.app.Entities.Match;
import com.fifa.app.Services.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/matchMaker")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/{seasonYear}")
    public ResponseEntity<?> generateMatches(@PathVariable int seasonYear) {
        try {
            List<MatchDisplayDTO> matches = matchService.generateSeasonMatches(seasonYear);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{seasonYear}")
    public ResponseEntity<List<MatchDisplayDTO>> getMatchesBySeason(
        @PathVariable int seasonYear,
        @RequestParam(required = false) String matchStatus,
        @RequestParam(required = false) String clubPlayingName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime matchAfter,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime matchBeforeOrEquals
    ) {
        List<MatchDisplayDTO> matches = matchService.getMatchesFiltered(seasonYear, matchStatus, clubPlayingName, matchAfter, matchBeforeOrEquals);
        if (matches.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(matches);
        }
        return ResponseEntity.ok(matches);
    }

}
