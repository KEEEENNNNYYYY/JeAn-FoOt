package com.fifa.app.DAO;

import com.fifa.app.DTO.PlayerDTO;
import com.fifa.app.Entities.Club;
import com.fifa.app.Entities.Coach;
import com.fifa.app.Entities.Nationality;
import com.fifa.app.Entities.Player;
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

    public List<PlayerDTO> updateClubPlayers(UUID clubId, List<PlayerDTO> players) {
        String deleteQuery = """
    DELETE FROM club_player WHERE club_id = ?::uuid;
    """;

        String insertClubPlayerQuery = """
    INSERT INTO club_player (id, club_id, player_id)
    VALUES (?::uuid, ?::uuid, ?::uuid);
    """;

        String deletePlayerFromOldClubQuery = """
    DELETE FROM club_player WHERE player_id = ?::uuid;
    """;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // Démarrer une transaction

            try (
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                PreparedStatement insertStmt = connection.prepareStatement(insertClubPlayerQuery);
                PreparedStatement deleteOldClubStmt = connection.prepareStatement(deletePlayerFromOldClubQuery);
            ) {
                // 1. Supprimer tous les joueurs du club (pour les joueurs dans le club actuel)
                deleteStmt.setObject(1, clubId);
                deleteStmt.executeUpdate();

                // 2. Ajouter les nouveaux joueurs au club
                for (PlayerDTO player : players) {
                    // Vérifier si le joueur est déjà dans un autre club
                    String checkPlayerQuery = "SELECT club_id FROM club_player WHERE player_id = ?::uuid";
                    try (PreparedStatement checkStmt = connection.prepareStatement(checkPlayerQuery)) {
                        checkStmt.setObject(1, UUID.fromString(player.getId()));
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next()) {
                                UUID existingClubId = UUID.fromString(rs.getString("club_id"));
                                if (!existingClubId.equals(clubId)) {
                                    // Si le joueur est déjà dans un autre club, on le supprime de ce club
                                    deleteOldClubStmt.setObject(1, UUID.fromString(player.getId()));
                                    deleteOldClubStmt.executeUpdate();
                                }
                            }

                            // Ensuite, on ajoute le joueur au nouveau club
                            insertStmt.setObject(1, UUID.randomUUID()); // Générer un nouvel ID pour la relation
                            insertStmt.setObject(2, clubId);
                            insertStmt.setObject(3, UUID.fromString(player.getId()));
                            insertStmt.executeUpdate();
                        }
                    }
                }

                connection.commit(); // Confirmer la transaction
            } catch (SQLException e) {
                connection.rollback(); // Annuler si une erreur survient
                throw new RuntimeException("Erreur lors de la mise à jour des joueurs du club", e);
            }

            return players;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

}
