package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.ClubStatistics;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClubStatisticsService {
    private ChampionshipClient championshipClient;

    public Flux<ClubStatistics> getClubStatistics(String championship, Integer season){
        System.out.println("synchronizing clubs Statistics");
        return championshipClient.getWebClient()
                .get()
                .uri("{championship}/clubs/statistics/{season}",championship,season)
                .retrieve()
                .bodyToFlux(ClubStatistics.class);
    }

    public List<ClubStatistics> saveAll(List<ClubStatistics> clubStatistics) {
        return new ArrayList<>(clubStatistics);
    }
}
