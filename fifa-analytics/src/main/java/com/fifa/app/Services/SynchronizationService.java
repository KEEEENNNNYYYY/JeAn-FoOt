package com.fifa.app.Services;

import com.fifa.app.DTO.*;
import com.fifa.app.Enum.Championship;
import com.fifa.app.Enum.Position;
import com.fifa.app.Mapper.RestToModel;
import com.fifa.app.RestModels.ClubRest;
import com.fifa.app.RestModels.PlayerRest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SynchronizationService {

    private PlayerService playerService;
    private PlayerStatisticsService playerStatisticsService;
    private ClubService clubService;
    private SeasonService seasonService;

    public Mono<Void> synchronize() {
        return Flux.fromArray(Championship.values())
                .flatMapSequential(championship ->
                        seasonService.getSeasons(championship.name())
                                .collectList()
                                .flatMapMany(seasons ->
                                        // Collecte de tous les clubs (réactive)
                                        Flux.fromIterable(seasons)
                                                .flatMap(season ->
                                                        clubService.getClubStatistics(championship.name(), season.getYear())
                                                )
                                                .collectList()
                                                .flatMapMany(clubService::saveAll)
                                                .thenMany(
                                                        // Une fois les clubs enregistrés, on passe aux joueurs
                                                        playerService.getPlayers(championship.name())
                                                                .flatMap(playerRest -> {
                                                                    Player player = RestToModel.mapToPlayer(playerRest);
                                                                    return seasonService.getSeasons(championship.name())
                                                                            .next()
                                                                            .flatMap(season ->
                                                                                    playerStatisticsService.getPlayerStatistics(
                                                                                                    championship.name(),
                                                                                                    player.getId(),
                                                                                                    season.getYear()
                                                                                            ).defaultIfEmpty(new PlayerStatistics())
                                                                                            .map(stats -> {
                                                                                                player.setPlayerStatistics(stats);
                                                                                                return player;
                                                                                            })
                                                                            );
                                                                })
                                                )
                                                .collectList()
                                                .flatMap(players -> Mono.fromCallable(() -> playerService.saveAll(players))
                                                        .subscribeOn(Schedulers.boundedElastic()))
                                )
                )
                .then();
    }

}
