package com.fifa.app.DAO;

import com.fifa.app.DTO.Player;
import com.fifa.app.DTO.PlayerStatistics;
import com.fifa.app.DTO.PlayingTime;
import com.fifa.app.Enum.DurationUnit;
import com.fifa.app.Enum.Position;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class PlayerDAO {

    private DataConnection dataConnection;
    private ClubDAO clubDAO;

    public List<Player> getAllPlayers(int limit) {
        String query = "SELECT id,name,number,position,nationality," +
                "age,club_id,scored_goals,playing_time,playing_time_unit FROM player" +
                " ORDER BY scored_goals DESC LIMIT ?";

        List<Player> players = new ArrayList<>();
        try (Connection connection = dataConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Player player = mapFromResultSet(resultSet);
                players.add(player);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return players;
    }

    public List<Player> saveAll(List<Player> players) {
        List<Player> savedPlayers = new ArrayList<>();
        System.out.println("try to save players");

        String query = "INSERT INTO players (id, name, number, nationality, position, age, club_id, scored_goals, playing_time, playing_time_unit) " +
                "VALUES (?::UUID, ?, ?, ?, ?, ?, ?::UUID, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "name = EXCLUDED.name, " +
                "number = EXCLUDED.number, " +
                "nationality = EXCLUDED.nationality, " +
                "position = EXCLUDED.position, " +
                "age = EXCLUDED.age, " +
                "club_id = EXCLUDED.club_id, " +
                "scored_goals = EXCLUDED.scored_goals, " +
                "playing_time = EXCLUDED.playing_time, " +
                "playing_time_unit = EXCLUDED.playing_time_unit " +
                "RETURNING id,name,number,nationality,position,age,club_id,scored_goals,playing_time,playing_time_unit";
        players.forEach(player -> {
            try(Connection connection = dataConnection.getConnection()){

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, player.getId());
                preparedStatement.setString(2, player.getName());
                preparedStatement.setInt(3,player.getNumber());
                preparedStatement.setString(4,player.getNationality());
                preparedStatement.setObject(5, player.getPosition().name(),Types.OTHER);
                preparedStatement.setInt(6,player.getAge());
                preparedStatement.setString(7,player.getClub().getId());
                preparedStatement.setInt(8,player.getPlayerStatistics().getScoredGoals());
                preparedStatement.setInt(9,player.getPlayerStatistics().getPlayingTime().getValue());
                preparedStatement.setObject(10, player.getPlayerStatistics().getPlayingTime().getDurationUnit(),Types.OTHER);
                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    Player p = mapFromResultSet(resultSet);
                    System.out.println(p);
                    savedPlayers.add(p);
                    System.out.println("saved 1 player");
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return savedPlayers;
    }

    private Player mapFromResultSet(ResultSet resultSet) throws SQLException {
        Player player = new Player();
        player.setId(resultSet.getString("id"));
        player.setName(resultSet.getString("name"));
        player.setNumber(resultSet.getInt("number"));
        player.setPosition(Position.valueOf(resultSet.getString("position")));
        player.setNationality(resultSet.getString("nationality"));
        player.setAge(resultSet.getInt("age"));
        player.setClub(clubDAO.getClub(resultSet.getString("club_id")));
        PlayerStatistics playerStatistics = new PlayerStatistics();
        playerStatistics.setScoredGoals(resultSet.getInt("scored_goals"));
        PlayingTime playingTime = new PlayingTime();
        playingTime.setValue(resultSet.getInt("playing_time"));
        playingTime.setDurationUnit(DurationUnit.valueOf((resultSet.getString("playing_time_unit"))));
        playerStatistics.setPlayingTime(playingTime);
        player.setPlayerStatistics(playerStatistics);
        return player;
    }
}
