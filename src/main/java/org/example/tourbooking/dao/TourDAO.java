package org.example.tourbooking.dao;

import org.example.tourbooking.model.Tour;
import org.example.tourbooking.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDAO {

    public List<Tour> getAllTours() {
        List<Tour> tours = new ArrayList<>();
        String sql = "SELECT * FROM tours";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tour t = new Tour(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("short_description"),
                        rs.getInt("category_id"),
                        rs.getInt("destination_id"),
                        rs.getDouble("price"),
                        rs.getInt("duration_days"),
                        rs.getInt("max_participants"),
                        rs.getInt("min_participants"),
                        rs.getString("difficulty_level"),
                        rs.getString("image_url"),
                        rs.getBoolean("featured"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                tours.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tours;
    }
    public Tour getTourById(int id) {
        String sql = "SELECT * FROM tours WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Tour(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("short_description"),
                        rs.getInt("category_id"),
                        rs.getInt("destination_id"),
                        rs.getDouble("price"),
                        rs.getInt("duration_days"),
                        rs.getInt("max_participants"),
                        rs.getInt("min_participants"),
                        rs.getString("difficulty_level"),
                        rs.getString("image_url"),
                        rs.getBoolean("featured"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
