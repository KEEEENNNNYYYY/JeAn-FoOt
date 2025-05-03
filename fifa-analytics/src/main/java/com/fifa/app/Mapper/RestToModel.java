package com.fifa.app.Mapper;

import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.Player;
import com.fifa.app.RestModels.ClubRest;
import com.fifa.app.RestModels.PlayerRest;

public class RestToModel {

    public static Player mapToPlayer(PlayerRest rest){
        Player player = new Player();
        player.setId(rest.getId());
        player.setName(rest.getName());
        player.setNumber(rest.getNumber());
        player.setPosition(rest.getPosition());
        player.setAge(rest.getAge());
        player.setClub(rest.getClub());
        player.setNationality(rest.getNationality());
        return player;
    }

    public static Club apToClub(ClubRest clubRest){
        Club club = new Club();
        club.setId(clubRest.getId());
        club.setName(clubRest.getName());
        club.setYearCreation(clubRest.getYearCreation());
        club.setStadium(clubRest.getStadium());
        club.setCoach(clubRest.getCoach());
        club.setAcronym(clubRest.getAcronym());
        return club;
    }
}
