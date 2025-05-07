package com.fifa.app.DAO;

import com.fifa.app.Entities.Transfert;
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
public class TransfertDAO {

    private final DataSource dataSource;

    public List<Transfert> getAll() {
        List<Transfert> transfertList = new ArrayList<>();

        String query = """
            SELECT
                id,
                player_id,
                transfert_date
            FROM transfert
            ORDER BY transfert_date DESC
        """;

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transfert transfert = new Transfert();
                transfert.setId(UUID.fromString(resultSet.getString("id")));
                transfert.setPlayerId(UUID.fromString(resultSet.getString("player_id")));
                transfert.setTransfertDate(resultSet.getDate("transfert_date").toLocalDate());

                transfertList.add(transfert);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des transferts", e);
        }

        return transfertList;
    }
}
