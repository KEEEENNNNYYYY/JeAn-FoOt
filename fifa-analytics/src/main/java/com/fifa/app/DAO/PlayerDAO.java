package com.fifa.app.DAO;

import com.fifa.app.DTO.Player;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class PlayerDAO {

    private DataConnection dataConnection;

    public List<Player> getBestPlayers() {
        List<Player> players = new ArrayList<Player>();
        String query = "SELECT " +
                "p.name, p.scored_goals, " +
                "c.name,c.championship, FROM players AS p";

        try (Connection connection = dataConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Player player = mapFromResultSet(resultSet);
                players.add(player);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    private Player mapFromResultSet(ResultSet resultSet) throws SQLException {
        Player player = new Player();

        return player;
    }
}
