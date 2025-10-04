package org.example.tourbooking.dao;

import org.example.tourbooking.model.Destination;
import org.example.tourbooking.utils.DBConnection;
import java.sql.*;
import java.util.*;

public class DestinationDAO {

    public List<Destination> getAllDestinations() {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Destination d = new Destination(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("city"),
                        rs.getString("description"),
                        rs.getString("image_url")
                );
                list.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
