package com.fifa.app.DAO;

import com.fifa.app.Entities.*;
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
        String query = """
            SELECT
                p.id AS player_id,
                p.name,
                p.age,
                p.number,
                p.player_position,
                p.nationality,
                c.id AS club_id,
                c.name AS club_name,
                c.acronym,
                c.year_creation,
                c.stadium,
                co.id AS coach_id,
                co.name AS coach_name,
                co.nationality AS coach_nationality
            FROM players p
            LEFT JOIN club_player cp ON p.id = cp.player_id
            LEFT JOIN club c ON cp.club_id = c.id
            LEFT JOIN coach co ON c.coach_id = co.id;
        """;

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

    private Player mapFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getObject("player_id").toString());
        player.setName(rs.getString("name"));
        player.setNumber(rs.getInt("number"));
        player.setPosition(Position.valueOf(rs.getString("player_position")));
        player.setNationality(Nationality.valueOf(rs.getString("nationality")));
        player.setAge(rs.getInt("age"));

        // Mapping du club et du coach
        String clubId = rs.getString("club_id");
        if (clubId != null) {
            Club club = new Club();
            club.setId(clubId);
            club.setName(rs.getString("club_name"));
            club.setAcronym(rs.getString("acronym"));
            club.setYearCreation(rs.getInt("year_creation"));
            club.setStadium(rs.getString("stadium"));

            String coachId = rs.getString("coach_id");
            if (coachId != null) {
                Coach coach = new Coach();
                coach.setId(coachId);
                coach.setName(rs.getString("coach_name"));
                coach.setNationality(Nationality.valueOf(rs.getString("coach_nationality")));
                club.setCoach(coach);
            }

            player.setClub(club);
        } else {
            player.setClub(null);
        }

        return player;
    }
}
