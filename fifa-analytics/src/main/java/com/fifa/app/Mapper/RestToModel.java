package com.fifa.app.Mapper;

import com.fifa.app.DAO.PlayerDAO;
import com.fifa.app.DTO.Club;
import com.fifa.app.DTO.ClubStat;
import com.fifa.app.DTO.Player;
import com.fifa.app.DTO.PlayerStatistics;
import com.fifa.app.Enum.Championship;
import com.fifa.app.RestModels.ClubRest;
import com.fifa.app.RestModels.PlayerRest;
import com.fifa.app.RestModels.PlayerStatisticsRest;
import org.springframework.stereotype.Component;

@Component
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

    public static Club mapToClub(ClubRest clubRest){
        Club club = new Club();
        club.setId(clubRest.getId());
        club.setName(clubRest.getName());
        club.setYearCreation(clubRest.getYearCreation());
        club.setStadium(clubRest.getStadium());
        club.setCoach(clubRest.getCoach());
        club.setAcronym(clubRest.getAcronym());
        club.setCoach(clubRest.getCoach());
        club.setChampionship(Championship.valueOf(clubRest.getChampionshipName()));
        ClubStat clubStat = new ClubStat();
        clubStat.setRankingPoints(clubRest.getRankingPoints());
        clubStat.setScoredGoals(clubRest.getScoredGoals());
        clubStat.setConcededGoals(clubRest.getConcededGoals());
        clubStat.setDifferenceGoals(clubRest.getDifferenceGoals());
        clubStat.setCleanSheetNumber(clubRest.getCleanSheetNumber());
        return club;
    }

    public static PlayerStatistics mapToPlayerStatistics(PlayerStatisticsRest playerStatisticsRest) {
        PlayerStatistics playerStatistics = new PlayerStatistics();
        Player player = new Player();
        player.setId(playerStatisticsRest.getPlayerId());
        playerStatistics.setPlayer(player);
        playerStatistics.setScoredGoals(playerStatisticsRest.getScoredGoals());
        playerStatistics.setPlayingTime(playerStatisticsRest.getPlayingTime());
        playerStatistics.setSeason(playerStatisticsRest.getSeasonYear());
        return playerStatistics;
    }
}
