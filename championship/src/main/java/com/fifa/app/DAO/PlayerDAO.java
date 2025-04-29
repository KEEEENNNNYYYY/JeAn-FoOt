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


}
