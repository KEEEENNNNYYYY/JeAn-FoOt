package com.fifa.app.RestControllers;

import com.fifa.app.Entities.Club;
import com.fifa.app.Services.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    @GetMapping
    public ResponseEntity<List<Club>> getAll() {
        List<Club> clubs = service.findAll();
        return ResponseEntity.ok(clubs);
    }
    @PutMapping
    public ResponseEntity<List<Club>> createOrUpdateClubs(@RequestBody List<Club> clubs) {
        service.createOrUpdateClubs(clubs);
        return ResponseEntity.ok(clubs);
    }
}
