package com.fifa.app.DAO;

import com.fifa.app.Entities.Nationality;
import com.fifa.app.Entities.Player;
import com.fifa.app.Entities.PlayerCriteria;
import com.fifa.app.Entities.Position;
import com.fifa.app.dataSource.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class PlayerDAO {

    private final DataSource dataSource;

    public List<Player> findAll() {
        List<Player> playerList = new ArrayList<>();
        String query = "SELECT * FROM players";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Player player = mapFromResultSet(resultSet);
                playerList.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des joueurs", e);
        }

        return playerList;
    }

    public List<Player> createOrUpdatePlayers(List<Player> players) {
        List<Player> resultList = new ArrayList<>();

        String upsertQuery = """
        INSERT INTO players (id, name, number, player_position, nationality, age)
        VALUES (?::uuid, ?, ?, ?::\"position\", ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            name = EXCLUDED.name,
            number = EXCLUDED.number,
            player_position = EXCLUDED.player_position,
            nationality = EXCLUDED.nationality,
            age = EXCLUDED.age;
    """;

        try (Connection connection = dataSource.getConnection()) {
            for (Player player : players) {
                try (PreparedStatement stmt = connection.prepareStatement(upsertQuery)) {
                    stmt.setObject(1, player.getId());
                    stmt.setString(2, player.getName());
                    stmt.setInt(3, player.getNumber());
                    stmt.setString(4, player.getPosition().name());
                    stmt.setString(5, player.getNationality().name());
                    stmt.setInt(6, player.getAge());

                    stmt.executeUpdate();
                    resultList.add(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création ou mise à jour des joueurs", e);
        }

        return resultList;
    }


    // Cette méthode doit être implémentée selon ton modèle Player
    private Player mapFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getObject("id").toString());
        player.setName(rs.getString("name"));
        player.setNumber(rs.getInt("number"));
        player.setPosition(Position.valueOf(rs.getString("player_position"))); // enum sous forme de String
        player.setNationality(Nationality.valueOf(rs.getString("nationality")));
        player.setAge(rs.getInt("age"));
        return player;
    }
}
