package org.example.tourbooking.dao;

import org.example.tourbooking.model.Customer;
import org.example.tourbooking.utils.DBConnection;
import java.sql.*;
import java.util.*;

public class CustomerDAO {

    // Lấy tất cả khách hàng
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("city"),
                        rs.getString("country")
                );
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Thêm hàm đăng nhập (so khớp email + password_hash)
    public Customer checkLogin(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("city"),
                            rs.getString("country")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // đăng nhập sai
    }
}
