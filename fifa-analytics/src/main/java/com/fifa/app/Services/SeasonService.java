package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.SeasonDAO;
import com.fifa.app.DTO.Season;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class SeasonService {
    private ChampionshipClient championshipClient;
    private SeasonDAO seasonDAO;

    public List<Season> getSeasons() {
        Mono<List<Season>> listMono = championshipClient.getWebClient()
                .get()
                .uri("seasons")
                .retrieve()
                .bodyToFlux(Season.class)
                .collectList();
        return listMono.block();
    }

    public List<Season> getAll() {
        return seasonDAO.getSeasons();
    }
}
