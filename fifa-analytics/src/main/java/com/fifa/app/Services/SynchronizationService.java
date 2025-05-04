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
import java.util.Collections;
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
                .flatMap(championship ->
                        seasonService.getSeasons(championship.name())
                                .collectList()
                                .flatMap(seasons -> seasonService.saveAll(seasons)
                                        .thenMany(Flux.fromIterable(seasons))
                                        .flatMap(season ->
                                                clubService.getClubStatistics(championship.name(), season.getYear())
                                                        .map(RestToModel::mapToClub)
                                                        .collectList()
                                                        .flatMap(clubs -> clubService.saveAll(clubs).then())
                                        )
                                        .then()
                                        .then(playerService.getPlayers(championship.name())
                                                .map(RestToModel::mapToPlayer)
                                                .collectList()
                                                .flatMap(players ->
                                                        playerService.saveAll(players)
                                                                        .then(
                                                        Flux.fromIterable(players)

                                                                .flatMap(player ->
                                                                        seasonService.getSeasons(championship.name())
                                                                                .next()
                                                                                .flatMap(season ->
                                                                                        playerStatisticsService.getPlayerStatistics(
                                                                                                championship.name(),
                                                                                                player.getId(),
                                                                                                season.getYear()
                                                                                        )
                                                                                )
                                                                )
                                                                .collectList()
                                                                .flatMap(playerStatisticsService::saveAll)) // Lambda instead of method reference
                                                ))
                                        )
                                )
                                .then();
    }
}
