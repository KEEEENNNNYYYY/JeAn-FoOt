package com.fifa.app.DAO;

import com.fifa.app.DTO.MatchDisplayDTO;
import com.fifa.app.Entities.*;
import com.fifa.app.dataSource.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class MatchDAO {

    private final DataSource dataSource;

    public List<MatchDisplayDTO> createMatchesForSeason(int seasonYear) {
        List<MatchDisplayDTO> createdMatches = new ArrayList<>();

        // Requêtes SQL
        String seasonQuery = "SELECT * FROM season WHERE year = ?";
        String checkMatchQuery = "SELECT COUNT(*) FROM match WHERE season_year = ?";
        String clubQuery = "SELECT * FROM club";
        String insertMatchQuery = """
        INSERT INTO match (id, club_playing_home_id, club_playing_away_id, stadium, match_datetime, actual_status, season_year)
        VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?::match_status, ?)
    """;

        try (Connection connection = dataSource.getConnection()) {
            // Vérifie saison
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

            if (season.getStatus() != SeasonStatus.STARTED) {
                throw new IllegalArgumentException("La saison doit être STARTED.");
            }

            // Vérifie matchs existants
            try (PreparedStatement stmt = connection.prepareStatement(checkMatchQuery)) {
                stmt.setInt(1, seasonYear);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalArgumentException("Les matchs sont déjà générés.");
                    }
                }
            }

            // Clubs
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

            // Génération des matchs
            try (PreparedStatement stmt = connection.prepareStatement(insertMatchQuery)) {
                for (int i = 0; i < clubs.size(); i++) {
                    for (int j = 0; j < clubs.size(); j++) {
                        if (i != j) {
                            String matchId = UUID.randomUUID().toString();
                            Club home = clubs.get(i);
                            Club away = clubs.get(j);
                            String stadium = "Stadium of " + home.getName();
                            LocalDateTime matchTime = LocalDateTime.now().plusDays(i + j);

                            // Insertion en DB
                            stmt.setObject(1, matchId);
                            stmt.setObject(2, UUID.fromString(home.getId()));
                            stmt.setObject(3, UUID.fromString(away.getId()));
                            stmt.setString(4, stadium);
                            stmt.setTimestamp(5, Timestamp.valueOf(matchTime));
                            stmt.setString(6, MatchStatus.NOT_STARTED.name());
                            stmt.setInt(7, seasonYear);
                            stmt.executeUpdate();

                            // Création des ClubPlaying
                            ClubPlaying homeClub = new ClubPlaying();
                            homeClub.setId(home.getId());
                            homeClub.setName(home.getName());
                            homeClub.setAcronym(home.getAcronym());
                            homeClub.setScore(0);
                            homeClub.setScorers(new ArrayList<>());

                            ClubPlaying awayClub = new ClubPlaying();
                            awayClub.setId(away.getId());
                            awayClub.setName(away.getName());
                            awayClub.setAcronym(away.getAcronym());
                            awayClub.setScore(0);
                            awayClub.setScorers(new ArrayList<>());

                            // DTO final
                            MatchDisplayDTO matchDto = new MatchDisplayDTO();
                            matchDto.setId(matchId);
                            matchDto.setClubPlayingHome(homeClub);
                            matchDto.setClubPlayingAway(awayClub);
                            matchDto.setStadium(stadium);
                            matchDto.setMatchDatetime(matchTime.toLocalDate()); // ISO 8601
                            matchDto.setActualStatus(MatchStatus.NOT_STARTED);

                            createdMatches.add(matchDto);
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
