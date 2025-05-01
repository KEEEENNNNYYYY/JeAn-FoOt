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
}
