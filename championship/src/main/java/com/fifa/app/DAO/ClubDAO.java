package com.fifa.app.DAO;

import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Coach;
import com.fifa.app.Entities.Nationality;
import com.fifa.app.dataSource.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ClubDAO {

    private final DataSource dataSource;

    public List<Club> findAllClubs() {
        List<Club> clubList = new ArrayList<>();
        String query = """
        SELECT c.id, c.name, c.acronym, c.year_creation, c.stadium,
               co.id AS coach_id, co.name AS coach_name, co.nationality AS coach_nationality
        FROM club c
        JOIN coach co ON c.coach_id = co.id
        """;

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Club club = mapFromResultSet(resultSet);
                clubList.add(club);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des clubs", e);
        }

        return clubList;
    }

    private Club mapFromResultSet(ResultSet rs) throws SQLException {
        Club club = new Club();
        club.setId(rs.getObject("id").toString());
        club.setName(rs.getString("name"));
        club.setAcronym(rs.getString("acronym"));
        club.setYearCreation(rs.getInt("year_creation"));
        club.setStadium(rs.getString("stadium"));

        // Création de l'entité Coach à partir des données de la base
        Coach coach = new Coach();
        coach.setId(rs.getObject("coach_id").toString());
        coach.setName(rs.getString("coach_name"));
        coach.setNationality(Nationality.valueOf(rs.getString("coach_nationality") != null
            ? rs.getString("coach_nationality") : "UNKNOWN")); // Valeur par défaut si aucune nationalité

        club.setCoach(coach);

        return club;
    }
    public List<Club> createOrUpdateClubs(List<Club> clubs) {
        String insertQuery = """
        INSERT INTO club (id, name, acronym, year_creation, stadium, coach_id)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            name = EXCLUDED.name,
            acronym = EXCLUDED.acronym,
            year_creation = EXCLUDED.year_creation,
            stadium = EXCLUDED.stadium,
            coach_id = EXCLUDED.coach_id
        """;

        String findCoachQuery = "SELECT id FROM coach WHERE name = ? AND nationality = ?";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement coachStmt = connection.prepareStatement(findCoachQuery);
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery)
        ) {
            for (Club club : clubs) {
                Coach coach = club.getCoach();
                // Trouver l'ID du coach en base
                coachStmt.setString(1, coach.getName());
                coachStmt.setString(2, coach.getNationality().name());
                ResultSet rs = coachStmt.executeQuery();

                if (!rs.next()) {
                    throw new RuntimeException("Coach introuvable : " + coach.getName() + ", " + coach.getNationality());
                }
                String coachId = rs.getObject("id").toString();

                // Préparer l'insert/update du club
                insertStmt.setObject(1, UUID.fromString(club.getId()));
                insertStmt.setString(2, club.getName());
                insertStmt.setString(3, club.getAcronym());
                insertStmt.setInt(4, club.getYearCreation());
                insertStmt.setString(5, club.getStadium());
                insertStmt.setObject(6, UUID.fromString(coachId));
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création ou mise à jour des clubs", e);
        }
        return clubs;
    }

}
