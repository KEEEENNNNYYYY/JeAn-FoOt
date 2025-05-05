package com.fifa.app.Services;

import com.fifa.app.Configuration.ChampionshipClient;
import com.fifa.app.DTO.ChampionshipRank;
import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.Season;
import com.fifa.app.Enum.Championship;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChampionshipService {

    private ClubService clubService;

//    public List<ChampionshipRank> getChampionshipRanks(Integer season, Integer limit) {
//        List<String> championships = Arrays.stream(Championship.values()).map(Enum::toString).toList();
//        return championships.stream()
//                .map(championship -> {
//                    List<Club> clubs = clubService
//                            .getClubStatistics(championship, season)
//                            .toStream()
//                            .toList();
//
//                    if (clubs.isEmpty()) {
//                        return new ChampionshipRank(championship, 0);
//                    }
//
//                    List<Integer> differences = clubs.stream()
//                            .map(Club::getDifferenceGoals)
//                            .filter(Objects::nonNull)
//                            .filter(diff -> diff >= 0)
//                            .sorted()
//                            .toList();
//
//                    if (differences.isEmpty()) {
//                        return new ChampionshipRank(championship, 0);
//                    }
//
//                    double median = median(differences);
//
//                    return new ChampionshipRank(championship, median);
//                })
//                .sorted(Comparator.comparingDouble(ChampionshipRank::getMedian))
//                .collect(Collectors.toList())
//                .reversed();
//    }

    private double median(List<Integer> sortedValues) {
        int size = sortedValues.size();
        if (size == 0) return 0;
        if (size % 2 == 1) {
            return sortedValues.get(size / 2);
        } else {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        }
    }



}
