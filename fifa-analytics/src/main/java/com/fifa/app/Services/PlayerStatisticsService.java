package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.PlayerStatistics;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Data
@Service
public class PlayerStatisticsService {

    private ChampionshipClient championshipClient;

    public PlayerStatistics getPlayerStatistics(String playerId,Integer season) {
        Mono<PlayerStatistics> playerStatisticsMono = championshipClient.getWebClient()
                .get()
                .uri("players/"+playerId+"/statistics/"+season)
                .retrieve()
                .bodyToMono(PlayerStatistics.class);
        return playerStatisticsMono.block();
    }
}
