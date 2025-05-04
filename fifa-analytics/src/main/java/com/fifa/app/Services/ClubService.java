package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.ClubDAO;
import com.fifa.app.DTO.Club;
import com.fifa.app.Enum.Championship;
import com.fifa.app.RestModels.ClubRest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
@Data
public class ClubService {

    private final ClubDAO clubDAO;
    private ChampionshipClient championshipClient;

    public Flux<ClubRest> getClubs(String championship) {
        return championshipClient.getWebClient()
                .get()
                .uri("/{championship}/clubs",championship)
                .retrieve()
                .bodyToFlux(ClubRest.class);
    }

    public Flux<Club> getClubStatistics(String championship,Integer season){
        return championshipClient.getWebClient()
                .get()
                .uri("/{championship}/clubs/statistics/{season}",championship,season)
                .retrieve()
                .bodyToFlux(Club.class);
    }

    public Flux<Club> saveAll(List<Club> clubs) {
        return Flux.fromIterable(clubDAO.saveAll(clubs));
    }
}
