package com.fifa.app.DAO;

import com.fifa.app.DTO.PlayerStatisticDTO;
import com.fifa.app.Entities.DurationUnit;
import com.fifa.app.Entities.PlayingTime;
import com.fifa.app.dataSource.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Repository
@AllArgsConstructor
public class PlayerStatisticDAO {

    private final DataSource dataSource;

    public PlayerStatisticDTO findByPlayerIdAndSeasonYear(String playerId, int seasonYear) {
        String query = """
            SELECT ps.scored_goal, ps.playing_time
            FROM player_statistic ps
            JOIN season s ON ps.season_id = s.id
            WHERE ps.player_id = ?::uuid AND s.year = ?
        """;

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setObject(1, playerId);
            statement.setInt(2, seasonYear);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int scoredGoals = rs.getInt("scored_goal");
                    long playingTimeInSeconds = rs.getLong("playing_time");

                    PlayingTime playingTime = new PlayingTime(
                        playingTimeInSeconds,
                        DurationUnit.SECONDS
                    );



                    return new PlayerStatisticDTO(scoredGoals, playingTime);
                } else {
                    return null; // Aucun résultat trouvé
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des statistiques", e);
        }
    }
}
