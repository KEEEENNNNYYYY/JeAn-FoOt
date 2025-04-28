package com.fifa.app.RestControllers;

import com.fifa.app.DTO.ClubStatDTO;
import com.fifa.app.Services.ClubStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/club_stats")
@RequiredArgsConstructor
public class ClubStatController {

    private final ClubStatService service;

    @GetMapping
    public ResponseEntity<List<ClubStatDTO>> getAll(){
        List<ClubStatDTO> stats = service.findAll();
        return ResponseEntity.ok(stats);
    }
}
