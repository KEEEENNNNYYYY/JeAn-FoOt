package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Player;
import com.fifa.app.Services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/players")
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<Player>> getAll() {
        List<Player> stats = playerService.findAll();
        return ResponseEntity.ok(stats);
    }
}
