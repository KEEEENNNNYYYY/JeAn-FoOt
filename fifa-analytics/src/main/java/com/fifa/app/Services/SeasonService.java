package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.SeasonDAO;
import com.fifa.app.DTO.Season;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class SeasonService {
    private ChampionshipClient championshipClient;
    private SeasonDAO seasonDAO;

    public Flux<Season> getSeasons(String championship) {
        return championshipClient.getWebClient()
                .get()
                .uri("{championship}/seasons",championship)
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
                .bodyToFlux(Season.class)
                .onErrorResume(e -> {
                    // Si une exception survient malgré tout (ex: serveur down), on ignore aussi
                    System.out.println("Erreur lors de l'appel pour " + championship + " : " + e.getMessage());
                    return Flux.empty();
                });
    }

    public List<Season> getAll() {
        return seasonDAO.getSeasons();
    }
}
