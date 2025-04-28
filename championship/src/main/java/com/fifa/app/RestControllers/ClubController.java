package com.fifa.app.RestControllers;

import com.fifa.app.DTO.ClubDTO;
import com.fifa.app.Services.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("{id}")
    public ResponseEntity<ClubDTO> getById(@PathVariable int id){
        ClubDTO club = service.findById();
        return ResponseEntity.ok(club);
    }
}
