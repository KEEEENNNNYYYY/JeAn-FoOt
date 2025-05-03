package com.fifa.app.Services;

import com.fifa.app.DTO.*;
import com.fifa.app.Enum.Championship;
import com.fifa.app.Enum.Position;
import com.fifa.app.Mapper.RestToModel;
import com.fifa.app.RestModels.PlayerRest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@AllArgsConstructor
@Service
public class SynchronizationService {

    private PlayerService playerService;
    private PlayerStatisticsService playerStatisticsService;
    private ClubService clubService;
    private SeasonService seasonService;
    private ClubStatisticsService clubStatisticsService;

    public Mono<Void> synchronize() {
        return Flux.fromArray(Championship.values())
                .flatMap(championship ->
                        seasonService.getSeasons(championship.name())
                                .flatMap(season ->
                                        playerService.getPlayers(championship.name())
                                                .map(RestToModel::mapToPlayer)
                                                .flatMap(player -> //probleme a partir d'ici
                                                        playerStatisticsService.getPlayerStatistics(championship.name(), player.getId(), season.getYear())
                                                                .map(statistics -> {
                                                                    System.out.println(statistics);
                                                                    player.setPlayerStatistics(statistics);
                                                                    System.out.println(player);
                                                                    return player;
                                                                })
                                                )
                                )
                )
                .collectList()
                .doOnNext(playerService::saveAll) // ou flatMap si saveAll est réactif
                .then();
    }
}
