package org.example.tourbooking.server.booking;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SyncHandler {
    public static void insertBookingFromPeer(JSONObject data) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                INSERT INTO bookings (customer_id, tour_id, schedule_id, number_of_people, total_price, start_date, end_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, data.getInt("customer_id"));
                ps.setInt(2, data.getInt("tour_id"));
                ps.setInt(3, data.getInt("schedule_id"));
                ps.setInt(4, data.getInt("number_of_people"));
                double price = 2990000 * data.getInt("number_of_people"); // ví dụ
                ps.setDouble(5, price);
                ps.setString(6, data.getString("start_date"));
                ps.setString(7, data.getString("end_date"));
                ps.executeUpdate();
            }

            System.out.println("✅ [SyncHandler] Đồng bộ booking từ peer thành công!");

        } catch (Exception e) {
            System.err.println("❌ [SyncHandler] Lỗi đồng bộ: " + e.getMessage());
        }
    }
}
