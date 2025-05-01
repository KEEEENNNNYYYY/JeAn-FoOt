package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.Player;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PlayerService {

    private ChampionshipClient championshipClient;

    public List<Player> getPlayers() {
        Mono<List<Player>> playerList = championshipClient.getWebClient().get()
                .uri("players")
                .retrieve()
                .bodyToFlux(Player.class)
                .collectList();
        return playerList.block();
    }
}
