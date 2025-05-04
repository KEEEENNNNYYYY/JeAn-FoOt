package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Player;
import com.fifa.app.Entities.PlayerCriteria;
import com.fifa.app.Services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<Player>> getAll(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer ageMinimum,
        @RequestParam(required = false) Integer ageMaximum,
        @RequestParam(required = false) String clubName
    ) {
        PlayerCriteria criteria = new PlayerCriteria();
        criteria.setName(name);
        criteria.setAgeMinimum(ageMinimum);
        criteria.setAgeMaximum(ageMaximum);
        criteria.setClubName(clubName);

        List<Player> players = playerService.findAll(criteria);
        return ResponseEntity.ok(players);
    }


    @PutMapping
    public ResponseEntity<List<Player>> createOrUpdate(@RequestBody List<Player> players) {
        List<Player> updatedPlayers = playerService.createOrUpdatePlayers(players);
        return ResponseEntity.ok(updatedPlayers);
    }
}
