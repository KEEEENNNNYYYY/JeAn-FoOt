package com.fifa.app.RestController;

import com.fifa.app.DTO.Player;
import com.fifa.app.Services.PlayerService;
import com.fifa.app.Services.SynchronizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class FifaController {

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
    public ResponseEntity<List<Player>> bestPlayer(){
        return ResponseEntity.ok(playerService.getBestPlayers());
    }
}
