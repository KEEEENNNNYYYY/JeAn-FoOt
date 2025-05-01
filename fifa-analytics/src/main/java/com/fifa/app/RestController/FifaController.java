package com.fifa.app.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FifaController {

    @GetMapping
    public String index() {
        return "Fifa Analytics";
    }

    @PostMapping("synchronization")
    public ResponseEntity<String> synchronisation(){
        return ResponseEntity.ok("Synchronization completed");
    }
}
