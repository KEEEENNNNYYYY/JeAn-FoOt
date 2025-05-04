package com.fifa.app.DAO;

import com.fifa.app.DTO.MatchDisplayDTO;
import com.fifa.app.Entities.*;
import com.fifa.app.dataSource.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
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

    public List<MatchDisplayDTO> findMatchesFiltered(int seasonYear, String matchStatus, String clubPlayingName,
                                                     LocalDateTime matchAfter, LocalDateTime matchBeforeOrEquals) {
        List<MatchDisplayDTO> matches = new ArrayList<>();

        StringBuilder query = new StringBuilder("""
        SELECT m.id as match_id, m.stadium, m.match_datetime, m.actual_status,
               home.id as home_id, home.name as home_name, home.acronym as home_acronym,
               away.id as away_id, away.name as away_name, away.acronym as away_acronym
        FROM match m
        JOIN club home ON m.club_playing_home_id = home.id
        JOIN club away ON m.club_playing_away_id = away.id
        WHERE m.season_year = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(seasonYear);

        if (matchStatus != null) {
            query.append(" AND m.actual_status = ?::match_status");
            params.add(matchStatus);
        }

        if (clubPlayingName != null) {
            query.append(" AND (home.name ILIKE ? OR away.name ILIKE ?)");
            params.add("%" + clubPlayingName + "%");
            params.add("%" + clubPlayingName + "%");
        }

        if (matchAfter != null) {
            query.append(" AND m.match_datetime > ?");
            params.add(Timestamp.valueOf(matchAfter));
        }

        if (matchBeforeOrEquals != null) {
            query.append(" AND m.match_datetime <= ?");
            params.add(Timestamp.valueOf(matchBeforeOrEquals));
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String matchId = rs.getString("match_id");
                    String stadium = rs.getString("stadium");
                    LocalDate matchDate = rs.getTimestamp("match_datetime").toLocalDateTime().toLocalDate();
                    MatchStatus status = MatchStatus.valueOf(rs.getString("actual_status"));

                    ClubPlaying home = new ClubPlaying();
                    home.setId(rs.getString("home_id"));
                    home.setName(rs.getString("home_name"));
                    home.setAcronym(rs.getString("home_acronym"));
                    home.setScore(0); // à charger si tu stockes les scores
                    home.setScorers(new ArrayList<>()); // à remplir depuis table scorer

                    ClubPlaying away = new ClubPlaying();
                    away.setId(rs.getString("away_id"));
                    away.setName(rs.getString("away_name"));
                    away.setAcronym(rs.getString("away_acronym"));
                    away.setScore(0);
                    away.setScorers(new ArrayList<>());

                    MatchDisplayDTO dto = new MatchDisplayDTO();
                    dto.setId(matchId);
                    dto.setStadium(stadium);
                    dto.setMatchDatetime(matchDate);
                    dto.setActualStatus(status);
                    dto.setClubPlayingHome(home);
                    dto.setClubPlayingAway(away);

                    matches.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du filtrage des matchs", e);
        }

        return matches;
    }

}
