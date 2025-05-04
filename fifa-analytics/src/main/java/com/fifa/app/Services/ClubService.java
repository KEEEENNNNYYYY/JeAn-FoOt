package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DAO.ClubDAO;
import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.ClubStat;
import com.fifa.app.DTO.Player;
import com.fifa.app.DTO.Season;
import com.fifa.app.RestModels.ClubRest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Data
public class ClubService {

    private final ClubDAO clubDAO;
    private ChampionshipClient championshipClient;
    private PlayerDAO playerDAO;

    public Flux<ClubRest> getClubs(String championship) {
        return championshipClient.getWebClient()
                .get()
                .uri("/{championship}/clubs",championship)
                .retrieve()
                .bodyToFlux(ClubRest.class);
    }

    public Flux<ClubRest> getClubStatistics(String championship,Integer season){
        return championshipClient.getWebClient()
                .get()
                .uri("/{championship}/clubs/statistics/{season}",championship,season)
                .retrieve()
                .bodyToFlux(ClubRest.class)
                .doOnNext(clubRest -> clubRest.setChampionshipName(championship));
    }

    public Flux<Club> saveAll(List<Club> clubs) {
        return Flux.fromIterable(clubDAO.saveAll(clubs));
    }

    public List<Club> getBestClubs(Integer top, Integer seasonYear) {
        return clubDAO.getAll().stream()
                .filter(club -> club.getClubStats() != null)
                .map(club -> Map.entry(club, getStatForYear(club, seasonYear)))
                .filter(entry -> entry.getValue() != null)
                .sorted(
                        Comparator.comparing((Map.Entry<Club, ClubStat> e) -> e.getValue().getRankingPoints(), Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(e -> e.getValue().getDifferenceGoals(), Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(e -> e.getValue().getScoredGoals(), Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(e -> e.getValue().getCleanSheetNumber(), Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .limit(top)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private ClubStat getStatForYear(Club club, Integer seasonYear) {
        return club.getClubStats().stream()
                .filter(stat -> stat.getSeason() != null && seasonYear.equals(stat.getSeason().getYear()))
                .findFirst()
                .orElse(null);
    }

}
