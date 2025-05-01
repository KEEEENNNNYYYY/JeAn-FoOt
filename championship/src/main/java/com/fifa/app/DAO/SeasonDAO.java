package com.fifa.app.DAO;

import com.fifa.app.Entities.Season;
import com.fifa.app.Entities.SeasonStatus;
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
public class SeasonDAO {

    private final DataSource dataSource;

    public List<Season> findAll() {
        List<Season> seasonList = new ArrayList<>();
        String query = "SELECT * FROM season";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Season season = mapFromResultSet(resultSet);
                seasonList.add(season);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des saisons", e);
        }

        return seasonList;
    }


    public List<Season> createSeasons(List<Season> seasons) {
        List<Season> createdSeasons = new ArrayList<>();

        String insertQuery = """
        INSERT INTO season (id, alias, status, year)
        VALUES (?::uuid, ?, ?::season_status, ?)
    """;

        try (Connection connection = dataSource.getConnection()) {
            for (Season season : seasons) {
                String generatedId = UUID.randomUUID().toString();
                String alias = season.getAlias();
                int year = season.getYear(); // car year est une string dans l'entité
                SeasonStatus status = SeasonStatus.NOT_STARTED;

                try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                    stmt.setObject(1, generatedId);
                    stmt.setString(2, alias);
                    stmt.setString(3, status.name());
                    stmt.setInt(4, year);
                    stmt.executeUpdate();
                }

                createdSeasons.add(new Season(generatedId,year, alias, status));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création des saisons", e);
        }

        return createdSeasons;
    }

    private Season mapFromResultSet(ResultSet rs) throws SQLException {
        return new Season(
            rs.getObject("id").toString(),
            rs.getInt("year"), // stocké en int, converti en String
            rs.getString("alias"),
            SeasonStatus.valueOf(rs.getString("status"))
        );
    }
}
