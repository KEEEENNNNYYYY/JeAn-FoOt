package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.Player;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class SynchronizationService {

    private PlayerService playerService;
    private PlayerStatisticsService playerStatisticsService;
    private ClubService clubService;

    public void synchronize() {
        List<Player> players = playerService.getPlayers();
        players.stream()
                .peek(player -> {
                    player.setPlayerStatistics(playerStatisticsService.getPlayerStatistics(player.getId(),2025));
                });
        List<Club> clubs = clubService.getClubs();
    }
}
