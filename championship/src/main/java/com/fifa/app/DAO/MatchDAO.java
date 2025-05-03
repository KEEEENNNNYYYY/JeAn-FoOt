package com.fifa.app.DAO;

import com.fifa.app.Entities.*;
import com.fifa.app.dataSource.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class MatchDAO {

    private final DataSource dataSource;

    public List<Match> createMatchesForSeason(int seasonYear) {
        List<Match> createdMatches = new ArrayList<>();

        String seasonQuery = "SELECT * FROM season WHERE year = ?";
        String checkMatchQuery = "SELECT COUNT(*) FROM match WHERE season_year = ?";
        String clubQuery = "SELECT * FROM club";
        String insertMatchQuery = """
            INSERT INTO match (id, club_playing_home_id, club_playing_away_id, stadium, match_datetime, actual_status, season_year)
            VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?::match_status, ?)
        """;

        try (Connection connection = dataSource.getConnection()) {

            // 1. Vérifie que la saison existe
            Season season = null;
            try (PreparedStatement stmt = connection.prepareStatement(seasonQuery)) {
                stmt.setInt(1, seasonYear);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        season = new Season(
                            rs.getObject("id").toString(),
                            rs.getInt("year"),
                            rs.getString("alias"),
                            SeasonStatus.valueOf(rs.getString("status"))
                        );
                    } else {
                        throw new RuntimeException("Saison " + seasonYear + " introuvable.");
                    }
                }
            }

            // 2. Statut non valide
            if (season.getStatus() != SeasonStatus.STARTED) {
                throw new IllegalArgumentException("La saison doit être STARTED.");
            }

            // 3. Vérifie si les matchs sont déjà créés
            try (PreparedStatement stmt = connection.prepareStatement(checkMatchQuery)) {
                stmt.setInt(1, seasonYear);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("Les matchs de cette saison sont déjà générés.");
                    }
                }
            }

            // 4. Récupère tous les clubs
            List<Club> clubs = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(clubQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club(
                        rs.getObject("id").toString(),
                        rs.getString("name"),
                        rs.getString("acronym")
                    );
                    clubs.add(club);
                }
            }

            // 5. Génère les matchs (aller-retour)
            try (PreparedStatement stmt = connection.prepareStatement(insertMatchQuery)) {
                for (int i = 0; i < clubs.size(); i++) {
                    for (int j = 0; j < clubs.size(); j++) {
                        if (i != j) {
                            String matchId = UUID.randomUUID().toString();
                            stmt.setObject(1, matchId);
                            stmt.setObject(2, UUID.fromString(clubs.get(i).getId())); // home
                            stmt.setObject(3, UUID.fromString(clubs.get(j).getId())); // away
                            stmt.setString(4, "Stadium of " + clubs.get(i).getName());
                            stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now().plusDays(i + j)));
                            stmt.setString(6, MatchStatus.NOT_STARTED.name());
                            stmt.setInt(7, seasonYear);
                            stmt.executeUpdate();

                            Match match = new Match(matchId, clubs.get(i), clubs.get(j), "Stadium of " + clubs.get(i).getName(),
                                java.time.LocalDateTime.now().plusDays(i + j), MatchStatus.NOT_STARTED);
                            createdMatches.add(match);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération des matchs", e);
        }

        return createdMatches;
    }
}
