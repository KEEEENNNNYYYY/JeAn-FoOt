package com.fifa.app.DAO;

import com.fifa.app.DTO.Season;
import com.fifa.app.Enum.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class SeasonDAO {

    private DataConnection dataConnection;

    public List<Season> getSeasons() {
        String query = "SELECT * FROM season";
        List<Season> seasons = new ArrayList<Season>();
        try(Connection con = dataConnection.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                Season season = getSeasonFromResultSet(resultSet);
                seasons.add(season);
            }
        }catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }
        return seasons;
    }

    public Season getSeason(int year) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Season> saveAll(List<Season> seasons) {
        String query =  "INSERT INTO seasons (id, year, alias, status) VALUES (?::UUID, ?, ? ,?)";
        List<Season> seasonList = new ArrayList<>();
        seasons.forEach(season -> {
            try(Connection connection = dataConnection.getConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, season.getId());
                stmt.setInt(2, season.getYear());
                stmt.setString(3, season.getAlias());
                stmt.setObject(4, season.getStatus().name());
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        });
        return seasonList;
    }

    private Season getSeasonFromResultSet(ResultSet rs) throws SQLException {
        Season season = new Season();
        season.setId(rs.getString("id"));
        season.setYear(rs.getInt("year"));
        season.setAlias(rs.getString("alias"));
        season.setStatus(rs.getObject("status", Status.class));
        return season;
    }
}
