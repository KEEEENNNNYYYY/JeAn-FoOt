package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.ClubStatistics;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClubStatisticsService {
    private ChampionshipClient championshipClient;

    public List<ClubStatistics> getClubStatistics(Integer season){
        Mono<List<ClubStatistics>> listMono = championshipClient.getWebClient()
                .get()
                .uri("clubs/statistics/"+season)
                .retrieve()
                .bodyToFlux(ClubStatistics.class)
                .collectList();
        return listMono.block();
    }
}
