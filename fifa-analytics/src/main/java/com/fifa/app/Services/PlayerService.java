package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.DTO.Player;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class PlayerService {

    private ChampionshipClient championshipClient;
    private PlayerDAO playerDAO;

    public List<Player> getPlayers() {
        Mono<List<Player>> playerList = championshipClient.getWebClient().get()
                .uri("players")
                .retrieve()
                .bodyToFlux(Player.class)
                .collectList();
        return playerList.block();
    }

    public List<Player> getBestPlayers() {
        return playerDAO.getBestPlayers();
    }
}
