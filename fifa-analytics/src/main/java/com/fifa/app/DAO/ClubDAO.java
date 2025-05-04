package com.fifa.app.DAO;

import com.fifa.app.DTO.Club;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ClubDAO {

    private DataConnection dataConnection;
    public List<Club> getAll() {
        List<Club> clubs = new ArrayList<>();
        String query = "SELECT id, name, acronym,year_creation, stadium,coach_id,ranking_points,scored_goals,difference_goals,clean_sheet_number  FROM clubs";
        try (Connection connection = dataConnection.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Club club = mapFromResultSet(resultSet);
                clubs.add(club);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return clubs;
    }

    public Club getClub(String clubId) {
        String query = "SELECT id, name, acronym,year_creation, stadium,coach_id,ranking_points,scored_goals,difference_goals,clean_sheet_number  FROM clubs WHERE id = ?::UUID";
        try (Connection connection = dataConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, clubId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return  mapFromResultSet(resultSet);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Club> saveAll(List<Club> clubs) {
        List<Club> clubList = new ArrayList<>();
        String query =  "INSERT INTO clubs(id, name, acronym, year_creation,stadium,coach_id,ranking_points,scored_goals,difference_goals,clean_sheet_number )" +
                "VALUES (?::UUID,?,?,?,?,?::UUID,?,?,?,?)" +
                "ON CONFLICT (id) " +
                "DO UPDATE SET " +
                "name = EXCLUDED.name," +
                "acronym = EXCLUDED.acronym," +
                "year_creation = EXCLUDED.year_creation," +
                "stadium = EXCLUDED.stadium," +
                "coach_id = EXCLUDED.coach_id," +
                "ranking_points = EXCLUDED.ranking_points," +
                "scored_goals = EXCLUDED.scored_goals," +
                "difference_goals = EXCLUDED.difference_goals," +
                "clean_sheet_number = EXCLUDED.clean_sheet_number " +
                "RETURNING id, name, acronym, year_creation,stadium,coach_id,ranking_points,scored_goals,difference_goals,clean_sheet_number";
        clubs.forEach((club) -> {
            System.out.println("clubId: " + club.getId());
            try(Connection connection = dataConnection.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, club.getId());
                preparedStatement.setString(2, club.getName());
                preparedStatement.setString(3, club.getAcronym());
                preparedStatement.setObject(4, club.getYearCreation());
                preparedStatement.setString(5, club.getStadium());
                preparedStatement.setString(6,club.getCoach().getId());
                preparedStatement.setInt(7, club.getRankingPoints());
                preparedStatement.setInt(8, club.getScoredGoals());
                preparedStatement.setInt(9, club.getDifferenceGoals());
                preparedStatement.setInt(10, club.getCleanSheetNumber());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Club clubFromDB = mapFromResultSet(resultSet);
                    clubList.add(clubFromDB);
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });
        return clubList;
    }

    public Club mapFromResultSet(ResultSet resultSet) throws SQLException {
        Club club = new Club();
        club.setId(resultSet.getString("id"));
        club.setName(resultSet.getString("name"));
        club.setAcronym(resultSet.getString("acronym"));
        club.setYearCreation(resultSet.getInt("year_creation"));
        club.setStadium(resultSet.getString("stadium"));
        club.setScoredGoals(resultSet.getInt("scored_goals"));
        club.setDifferenceGoals(resultSet.getInt("difference_goals"));
        club.setCleanSheetNumber(resultSet.getInt("clean_sheet_number"));
        club.setRankingPoints(resultSet.getInt("ranking_points"));
        return club;
    }

}
