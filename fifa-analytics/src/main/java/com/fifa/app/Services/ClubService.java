package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.Club;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Data
public class ClubService {

    private ChampionshipClient championshipClient;

    public Mono<List<Club>> getClubs() {
        return championshipClient.getWebClient()
                .get()
                .uri("clubs")
                .retrieve()
                .bodyToFlux(Club.class)
                .collectList();
    }
}
