package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.DTO.Player;
import com.fifa.app.DTO.PlayerStatistics;
import com.fifa.app.Enum.DurationUnit;
import com.fifa.app.RestModels.PlayerRest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PlayerService {

    private ChampionshipClient championshipClient;
    private PlayerDAO playerDAO;

    public Flux<PlayerRest> getPlayers(String championship) {
        return championshipClient.getWebClient()
                .get()
                .uri("/{championship}/players",championship)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> {
                    System.out.println("Aucun endpoint pour " + championship + " (404 ignoré).");
                    return Mono.empty(); // <- retourne un Mono vide pour ne pas lever d'exception
                })
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Erreur HTTP " + response.statusCode() + ": " + body)))
                )
                .bodyToFlux(PlayerRest.class)
                .onErrorResume(e -> {
                    System.out.println("Erreur lors de l'appel pour " + championship + " : " + e.getMessage());
                    return Flux.empty();
                });
    }

    public List<Player> getBestPlayers(Integer top, DurationUnit playingTimeUnit, Integer seasonYear) {
        return playerDAO.getAllPlayers().stream()
                .filter(player -> player.getPlayerStatistics() != null && !player.getPlayerStatistics().isEmpty())
                .map(player -> {
                    PlayerStatistics stats = player.getPlayerStatistics()
                            .stream()
                            .filter(stat -> stat.getSeason() != null && seasonYear.equals(stat.getSeason()))
                            .findFirst()
                            .orElse(null);
                    System.out.println("Map: " + stats);
                    return Map.entry(player, stats);
                })
                .filter(entry -> entry.getValue() != null && entry.getValue().getPlayingTime().getDurationUnit() == playingTimeUnit)
                .sorted(
                        Comparator.comparingInt((Map.Entry<Player, PlayerStatistics> e) -> e.getValue().getScoredGoals()).reversed()
                                .thenComparingInt(e -> e.getValue().getPlayingTime().getValue())
                )
                .limit(top)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public Mono<List<Player>> saveAll(List<Player> players) {
        return Mono.fromCallable(()->playerDAO.saveAll(players)).subscribeOn(Schedulers.boundedElastic());
    }
}
