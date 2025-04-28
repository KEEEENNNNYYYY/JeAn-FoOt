package com.example.demo.RestControllers;


import com.example.demo.DTO.ClubDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAll(){
        List<ClubDTO> clubs = service.findAll();
        return ResponseEntity.ok(clubs);
    }

    @GetMapping
    public ResponseEntity<ClubDTO> getById(){
        ClubDTO club = service.findById();
        return ResponseEntity.ok(club);
    }
}
