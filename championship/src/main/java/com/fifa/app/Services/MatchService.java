package com.fifa.app.Services;

import com.fifa.app.DAO.MatchDAO;
import com.fifa.app.DTO.MatchDisplayDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchDAO matchDAO;

    public List<MatchDisplayDTO> generateSeasonMatches(int seasonYear) {
        return matchDAO.createMatchesForSeason(seasonYear);
    }
}
