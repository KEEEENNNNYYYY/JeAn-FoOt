package com.fifa.app.DAO;

import com.fifa.app.DTO.Club;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class ClubDAO {

    private DataConnection dataConnection;

    public Club getClub(String clubId) {
        String query = "SELECT id, name, acronym,year_creation, stadium  FROM clubs WHERE id = ?::UUID";
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
        String query =  "INSERT INTO clubs(id, name, acronym, year_creation,stadium,coach_id)" +
                "VALUES (?::UUID,?,?,?,?,?::UUID)" +
                "ON CONFLICT (id) " +
                "DO UPDATE SET " +
                "name = EXCLUDED.name," +
                "acronym = EXCLUDED.acronym," +
                "year_creation = EXCLUDED.year_creation," +
                "stadium = EXCLUDED.stadium," +
                "coach_id = EXCLUDED.coach_id " +
                "RETURNING id, name, acronym, year_creation,stadium,coach_id";
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
        return club;
    }
}
