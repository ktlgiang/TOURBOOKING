package org.example.tourbooking.dao;

import org.example.tourbooking.model.Tour;
import org.example.tourbooking.model.TourSchedule;
import org.example.tourbooking.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDAO {

    // 🔹 Lấy toàn bộ tour đang hoạt động
    public List<Tour> getAllTours() {
        List<Tour> tours = new ArrayList<>();
        String sql = "SELECT * FROM tours WHERE status = 'Hoạt động'";

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
            System.err.println("❌ Lỗi khi lấy danh sách tour: " + e.getMessage());
        }
        return tours;
    }

    // 🔹 Lấy chi tiết tour theo ID
    public Tour getTourById(int id) {
        Tour tour = null;
        String sql = "SELECT * FROM tours WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tour = new Tour(
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
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy tour theo ID: " + e.getMessage());
        }

        return tour;
    }

    // 🔹 Lấy danh sách lịch trình của tour
    public List<TourSchedule> getSchedulesByTourId(int tourId) {
        List<TourSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM tour_schedules WHERE tour_id = ? AND status = 'Mở' ORDER BY start_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tourId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TourSchedule s = new TourSchedule(
                            rs.getInt("id"),
                            rs.getInt("tour_id"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("available_slots"),
                            rs.getInt("booked_slots"),
                            rs.getDouble("price_adjustment"),
                            rs.getString("status")
                    );
                    schedules.add(s);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy lịch trình tour: " + e.getMessage());
        }

        return schedules;
    }
}
