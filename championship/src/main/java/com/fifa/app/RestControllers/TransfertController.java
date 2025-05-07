package com.fifa.app.RestControllers;


import com.fifa.app.Entities.Player;
import com.fifa.app.Entities.PlayerCriteria;
import com.fifa.app.Entities.Season;
import com.fifa.app.Entities.Transfert;
import com.fifa.app.Services.TransfertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transfert")
@RequiredArgsConstructor
public class TransfertController {

    private final TransfertService transfertService;

    @GetMapping
    public ResponseEntity<List<Transfert>> getAll() {
        List<Transfert> transfert = transfertService.getAll();
        return ResponseEntity.ok(transfert);
    }
}
