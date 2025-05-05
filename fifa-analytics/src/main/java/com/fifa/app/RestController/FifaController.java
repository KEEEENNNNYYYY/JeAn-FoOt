package com.fifa.app.RestController;

import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.Player;
import com.fifa.app.Enum.DurationUnit;
import com.fifa.app.Services.ChampionshipService;
import com.fifa.app.Services.ClubService;
import com.fifa.app.Services.PlayerService;
import com.fifa.app.Services.SynchronizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class FifaController {

    private final ClubService clubService;
    private final ChampionshipService championshipService;
    private SynchronizationService synchronizationService;

    private PlayerService playerService;

    @GetMapping
    public String index() {
        return "Fifa Analytics";
    }

    @PostMapping("synchronization")
    public ResponseEntity<String> synchronisation(){
        synchronizationService.synchronize().subscribe();
        return ResponseEntity.ok("Synchronization completed");
    }

    @GetMapping("bestPlayers")
    public ResponseEntity<List<Player>> bestPlayer(
            @RequestParam(defaultValue = "100") Integer top,
            @RequestParam(defaultValue = "MINUTE") DurationUnit playingTimeUnit,
            @RequestParam(defaultValue = "2023") Integer seasonYear){
        return ResponseEntity.ok(playerService.getBestPlayers(top,playingTimeUnit,seasonYear));
    }

    @GetMapping("bestClubs")
    public ResponseEntity<List<Club>> bestClub(@RequestParam(defaultValue = "100") Integer top,@RequestParam(defaultValue = "2023") Integer seasonYear){
        return ResponseEntity.ok(clubService.getBestClubs(top,seasonYear));
    }

//    @GetMapping("championshipRankings")
//    public ResponseEntity<Object> championshipRanking(@RequestParam(defaultValue = "10") Integer top, @RequestParam(defaultValue = "2023") Integer season){
//        return ResponseEntity.ok(championshipService.getChampionshipRanks(season,top));
//    }
}
