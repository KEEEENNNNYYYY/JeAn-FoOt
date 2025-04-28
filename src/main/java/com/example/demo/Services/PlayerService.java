package com.example.demo.Services;


import com.example.demo.Entities.Player;
import com.example.demo.TESTDAO.PlayerDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PlayerService {

    private PlayerDAO playerDAO;

    public List<Player> findAll(){
        List<Player> playerList = new ArrayList<>();
        playerList.addAll(playerDAO.findAll());
        return playerList;
    }
}
