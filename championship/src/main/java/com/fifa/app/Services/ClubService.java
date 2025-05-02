package com.fifa.app.Services;

import com.fifa.app.DAO.ClubDAO;
import com.fifa.app.DTO.PlayerDTO;
import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubDAO clubDAO;

    public List<Club> findAll() {
        return clubDAO.findAllClubs();
    }
    public List<Club> createOrUpdateClubs(List<Club> club) {
        return clubDAO.createOrUpdateClubs(club);
    }

    public List<PlayerDTO> updateClubPlayers(UUID id, List<PlayerDTO> players) {
        return clubDAO.updateClubPlayers(id,players);
    }


}
