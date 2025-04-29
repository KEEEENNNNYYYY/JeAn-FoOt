package com.fifa.app.Services;

import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.Entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerDAO playerDAO;

    public List<Player> findAll() {
        return playerDAO.findAll();}
}
