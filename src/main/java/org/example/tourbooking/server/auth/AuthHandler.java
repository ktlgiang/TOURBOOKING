package org.example.tourbooking.server.auth;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthHandler {

    public static String handleMessage(String message) {
        try {
            JSONObject request = new JSONObject(message);
            String action = request.optString("action", "");

            if ("login".equalsIgnoreCase(action)) {
                return handleLogin(request);
            } else if ("register".equalsIgnoreCase(action)) {
                return handleRegister(request);
            } else {
                return new JSONObject()
                        .put("status", "error")
                        .put("message", "Hành động không hợp lệ!")
                        .toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Lỗi xử lý JSON: " + e.getMessage())
                    .toString();
        }
    }

    private static String handleLogin(JSONObject req) {
        String email = req.optString("email", "");
        String password = req.optString("password", "");

        String sql = "SELECT id, full_name, email FROM customers WHERE email=? AND password_hash=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject res = new JSONObject();
                res.put("status", "success");
                res.put("message", "Đăng nhập thành công");
                res.put("user_id", rs.getInt("id"));
                res.put("full_name", rs.getString("full_name"));
                res.put("email", rs.getString("email"));
                return res.toString();
            } else {
                return new JSONObject()
                        .put("status", "fail")
                        .put("message", "Email hoặc mật khẩu không đúng!")
                        .toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Lỗi CSDL: " + e.getMessage())
                    .toString();
        }
    }

    private static String handleRegister(JSONObject req) {
        String name = req.optString("full_name", "");
        String email = req.optString("email", "");
        String password = req.optString("password", "");

        String sql = "INSERT INTO customers (full_name, email, password_hash, email_verified) VALUES (?, ?, ?, 1)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                return new JSONObject()
                        .put("status", "success")
                        .put("message", "Đăng ký thành công!")
                        .toString();
            } else {
                return new JSONObject()
                        .put("status", "fail")
                        .put("message", "Đăng ký thất bại!")
                        .toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Lỗi CSDL: " + e.getMessage())
                    .toString();
        }
    }
}
