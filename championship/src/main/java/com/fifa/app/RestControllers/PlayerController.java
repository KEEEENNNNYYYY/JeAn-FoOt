package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Player;
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
    public ResponseEntity<List<Player>> getAll() {
        List<Player> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }

    @PutMapping
    public ResponseEntity<List<Player>> createOrUpdate(@RequestBody List<Player> players) {
        List<Player> updatedPlayers = playerService.createOrUpdatePlayers(players);
        return ResponseEntity.ok(updatedPlayers);
    }
}
