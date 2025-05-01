package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.ClubStatistics;
import com.fifa.app.DTO.Player;
import com.fifa.app.DTO.Season;
import com.fifa.app.Enum.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class SynchronizationService {

    private PlayerService playerService;
    private PlayerStatisticsService playerStatisticsService;
    private ClubService clubService;
    private SeasonService seasonService;
    private ClubStatisticsService clubStatisticsService;

    public void synchronize() {
        List<Season> seasons = seasonService.getSeasons();
        Season activeSeason = seasons.stream()
                .filter(season -> season.getStatus()!= Status.NOT_STARTED)
                .max(Comparator.comparing(Season::getYear))
                .orElse(null);

        List<Player> players = playerService.getPlayers();
        players.stream()
                .peek(player -> {
                    assert activeSeason != null;
                    player.setPlayerStatistics(playerStatisticsService.getPlayerStatistics(player.getId(),activeSeason.getYear()));
                });
        List<Club> clubs = clubService.getClubs();
        List<ClubStatistics> clubStatistics = clubStatisticsService.getClubStatistics(activeSeason.getYear());
    }
}
