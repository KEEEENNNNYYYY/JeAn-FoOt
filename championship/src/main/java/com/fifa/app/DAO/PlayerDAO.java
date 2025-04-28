package com.fifa.app.DAO;

import com.example.demo.Entities.Player;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class PlayerDAO {

    public List<Player> findAll(){
        List<Player> playerList = new ArrayList<>();
        System.out.println("not implemented yet");
        return playerList;
    }
}
