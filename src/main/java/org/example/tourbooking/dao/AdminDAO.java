package org.example.tourbooking.dao;

import org.example.tourbooking.model.Admin;
import org.example.tourbooking.utils.DBConnection; // Đảm bảo đúng package cho DBConnection
import java.sql.*;

// Tạo AdminDAO.java
public class AdminDAO {

    public Admin checkLogin(String email, String password) {
        // Truy vấn bảng 'admins', so sánh email và password với cột password_hash
        String sql = "SELECT id, full_name, email, phone, role FROM admins WHERE email = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn Admin: " + e.getMessage());
        }
        return null;
    }
}