package org.example.tourbooking.server.auth;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.example.tourbooking.p2p.PeerSyncManager;


public class AuthHandler {

    public static String handleMessage(String message) {
        try {
            JSONObject request = new JSONObject(message);
            String action = request.optString("action", "");

            if ("login".equalsIgnoreCase(action)) {
                return handleLogin(request);
            } else if ("register".equalsIgnoreCase(action)) {
                return handleRegister(request);
            } else if ("logout".equalsIgnoreCase(action)) {
                return handleLogout(request); // 👈 Thêm dòng này
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

        String sql = "SELECT id, full_name, email, is_logged_in FROM customers WHERE email=? AND password_hash=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("full_name");
                String mail = rs.getString("email");
                boolean isLoggedIn = rs.getBoolean("is_logged_in");

                // ⚠️ Nếu đã đăng nhập ở nơi khác → chặn lại
                if (isLoggedIn) {
                    return new JSONObject()
                            .put("status", "fail")
                            .put("message", "Tài khoản này đang đăng nhập ở nơi khác!")
                            .toString();
                }

                // ✅ Cho phép đăng nhập → cập nhật trạng thái
                String updateSql = "UPDATE customers SET is_logged_in=true WHERE id=?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                    ps2.setInt(1, userId);
                    ps2.executeUpdate();
                }

                // 🔁 Thông báo sang peer
                PeerSyncManager.broadcastLoginStatus(userId, true);
                JSONObject res = new JSONObject();
                res.put("status", "success");
                res.put("message", "Đăng nhập thành công");
                res.put("user_id", userId);
                res.put("full_name", name);
                res.put("email", mail);
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
    private static String handleLogout(JSONObject req) {
        int userId = req.optInt("user_id", -1);

        if (userId == -1) {
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Thiếu user_id để đăng xuất!")
                    .toString();
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE customers SET is_logged_in=false WHERE id=?")) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            // 🔁 Gửi thông báo sang peer
            PeerSyncManager.broadcastLoginStatus(userId, false);

            return new JSONObject()
                    .put("status", "success")
                    .put("message", "Đăng xuất thành công!")
                    .toString();

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Lỗi khi đăng xuất: " + e.getMessage())
                    .toString();
        }
    }

    private static String handleRegister(JSONObject req) {
        String name = req.optString("full_name", "");
        String email = req.optString("email", "");
        String phone = req.optString("phone", "");
        String password = req.optString("password", "");

        String sql = "INSERT INTO customers (full_name, email, phone, password_hash, email_verified) VALUES (?, ?, ?, ?, 1)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // 🟢 Gửi thông tin sang peer để đồng bộ
                JSONObject syncMsg = new JSONObject();
                syncMsg.put("action", "sync_register");
                syncMsg.put("full_name", name);
                syncMsg.put("email", email);
                syncMsg.put("phone", phone);
                syncMsg.put("password", password);
                PeerSyncManager.broadcastRegister(syncMsg);

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