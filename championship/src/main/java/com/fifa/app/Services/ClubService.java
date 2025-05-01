package com.fifa.app.Services;

import com.fifa.app.DAO.ClubDAO;
import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
