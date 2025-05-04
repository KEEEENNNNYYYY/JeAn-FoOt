package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.PlayerStatistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Data
@Service
public class PlayerStatisticsService {

    private ChampionshipClient championshipClient;

    public Mono<PlayerStatistics> getPlayerStatistics(String championship, String playerId, Integer season) {
        return championshipClient.getWebClient()
                .get()
                .uri("{championship}/players/{playerId}/statistics/{season}", championship, playerId, season)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> {
                    System.out.println("Aucun endpoint pour " + championship + " (404 ignoré).");
                    return Mono.empty();  // Retourne Mono vide sans lever d'exception
                })
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Erreur HTTP " + response.statusCode() + ": " + body)))
                )
                .bodyToMono(PlayerStatistics.class)
                .onErrorResume(e -> {
                    // Si une exception survient malgré tout, on l'ignore
                    System.out.println("Erreur lors de l'appel pour " + championship + " : " + e.getMessage());
                    return Mono.empty();  // Retourne un Mono vide pour ignorer l'erreur
                });
    }

}
