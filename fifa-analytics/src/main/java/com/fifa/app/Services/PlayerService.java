package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.DTO.Player;
import com.fifa.app.RestModels.PlayerRest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

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
                    // On log l'absence, mais on continue la symphonie
                    System.out.println("Aucun endpoint pour " + championship + " (404 ignoré).");
                    return Mono.empty(); // <- retourne un Mono vide pour ne pas lever d'exception
                })
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Erreur HTTP " + response.statusCode() + ": " + body)))
                )
                .bodyToFlux(PlayerRest.class)
                .onErrorResume(e -> {
                    // Si une exception survient malgré tout (ex: serveur down), on ignore aussi
                    System.out.println("Erreur lors de l'appel pour " + championship + " : " + e.getMessage());
                    return Flux.empty();
                });
    }


    public List<Player> getBestPlayers() {
        List<Player> players = playerDAO.getAllPlayers();
        players.sort(Comparator
                .comparingInt((Player p) -> p.getPlayerStatistics().getScoredGoals()).reversed()
                .thenComparingInt(p -> p.getPlayerStatistics().getPlayingTime().getValue()));
        return players;
    }

    public List<Player> saveAll(List<Player> players) {
        return playerDAO.saveAll(players);
    }
}
