package com.fifa.app.RestControllers;

import com.fifa.app.DTO.PlayerStatisticDTO;
import com.fifa.app.Services.PlayerStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerStatisticController {

    private final PlayerStatisticService statisticService;

    @GetMapping("/{id}/statistics/{seasonYear}")
    public ResponseEntity<?> getPlayerStatistic(
        @PathVariable String id,
        @PathVariable String seasonYear
    ) {
        try {
            int year = Integer.parseInt(seasonYear);
            PlayerStatisticDTO stat = statisticService.getStatisticByPlayerAndYear(id, year);

            if (stat == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(stat);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid seasonYear format");
        }
    }
}
