package com.fifa.app.DAO;

import com.fifa.app.Entities.Player;
import com.fifa.app.Entities.PlayerCriteria;
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

    public List<Player> findAll() {
        List<Player> playerList = new ArrayList<>();
        String query = "SELECT * FROM player";

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

    public List<Player> findPlayersByFilters(PlayerCriteria criteria) {
        List<Player> players = new ArrayList<>();

        StringBuilder query = new StringBuilder(
            "SELECT p.*, c.id as club_id, c.name as club_name, c.acronym, c.yearCreation, c.stadium, " +
                "co.name as coach_name, co.nationality as coach_nationality " +
                "FROM player p " +
                "JOIN club c ON p.club_id = c.id " +
                "JOIN coach co ON c.coach_id = co.id " +
                "WHERE 1=1"
        );

        List<Object> parameters = new ArrayList<>();

        if (criteria.getName() != null && !criteria.getName().isEmpty()) {
            query.append(" AND p.name ILIKE ?");
            parameters.add("%" + criteria.getName() + "%");
        }
        if (criteria.getAgeMinimum() != null) {
            query.append(" AND p.age >= ?");
            parameters.add(criteria.getAgeMinimum());
        }
        if (criteria.getAgeMaximum() != null) {
            query.append(" AND p.age <= ?");
            parameters.add(criteria.getAgeMaximum());
        }
        if (criteria.getClubName() != null && !criteria.getClubName().isEmpty()) {
            query.append(" AND c.name ILIKE ?");
            parameters.add("%" + criteria.getClubName() + "%");
        }

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query.toString())
        ) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Player player = mapPlayerWithClub(rs);
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du filtrage des joueurs", e);
        }

        return players;
    }

    public List<Player> createOrUpdatePlayers(List<Player> players) {
        List<Player> resultList = new ArrayList<>();

        String selectQuery = "SELECT COUNT(*) FROM player WHERE id = ?";
        String insertQuery = "INSERT INTO player (id, name, number, position, nationality, age) VALUES (?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE player SET name = ?, number = ?, position = ?, nationality = ?, age = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            for (Player player : players) {
                boolean exists;

                // Vérifier si le joueur existe
                try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, player.getId());
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        rs.next();
                        exists = rs.getInt(1) > 0;
                    }
                }

                if (exists) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, player.getName());
                        updateStmt.setInt(2, player.getNumber());
                        updateStmt.setString(3, player.getPosition());
                        updateStmt.setString(4, player.getNationality());
                        updateStmt.setInt(5, player.getAge());
                        updateStmt.setString(6, player.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, player.getId());
                        insertStmt.setString(2, player.getName());
                        insertStmt.setInt(3, player.getNumber());
                        insertStmt.setString(4, player.getPosition());
                        insertStmt.setString(5, player.getNationality());
                        insertStmt.setInt(6, player.getAge());
                        insertStmt.executeUpdate();
                    }
                }

                resultList.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création ou mise à jour des joueurs", e);
        }

        return resultList;
    }
}
