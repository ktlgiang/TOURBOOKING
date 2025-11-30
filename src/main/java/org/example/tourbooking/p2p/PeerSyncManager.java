package org.example.tourbooking.p2p;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PeerSyncManager {

    private static WebSocketClient peerClient;
    private static boolean isConnected = false;

    // ⚙️ Kết nối tới peer
    public static void connectToPeer(String url) {
        try {
            peerClient = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    isConnected = true;
                    System.out.println("🔗 [P2P] Kết nối tới peer thành công: " + url);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (isConnected) return; // tránh gọi lại nhiều lần
                    isConnected = false;
                    System.out.println("❌ [P2P] Ngắt kết nối peer: " + reason);

                    new Thread(() -> {
                        try {
                            while (!isConnected) {
                                Thread.sleep(3000);
                                System.out.println("🔁 [P2P] Đang thử kết nối lại peer...");

                                try {
                                    connectToPeer(url);
                                    Thread.sleep(2000); // chờ trạng thái cập nhật
                                } catch (Exception e) {
                                    System.out.println("⚠️ [P2P] Lỗi khi thử kết nối lại: " + e.getMessage());
                                }
                            }
                        } catch (InterruptedException ignored) {}
                    }).start();
                }



                @Override
                public void onError(Exception ex) {
                    isConnected = false;
                    System.out.println("⚠️ [P2P] Lỗi: " + ex.getMessage());
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JSONObject data = new JSONObject(message);
                        String action = data.optString("action", "");

                        // 🟢 Thêm 2 case mới để xử lý đồng bộ trạng thái đăng nhập
                        switch (action) {
                            case "sync_booking" -> savePeerBooking(data.getJSONObject("booking"));
                            case "user_logged_in" -> markUserLoginStatus(data.getJSONObject("data").getInt("user_id"), true);
                            case "user_logged_out" -> markUserLoginStatus(data.getJSONObject("data").getInt("user_id"), false);
                            case "sync_register" -> savePeerRegister(data.getJSONObject("data"));

                        }

                    } catch (Exception e) {
                        System.out.println("⚠️ [P2P] Lỗi khi xử lý dữ liệu nhận: " + e.getMessage());
                    }
                }

            };
            peerClient.connect();
        } catch (Exception e) {
            System.out.println("⚠️ [P2P] Không thể kết nối peer: " + e.getMessage());
        }
    }

    // 📤 Gửi booking mới sang peer (được gọi từ BookingHandler)
    public static void broadcastNewBooking(JSONObject bookingData) {
        try {
            if (peerClient == null) {
                System.out.println("⚠️ [P2P] Peer client chưa được khởi tạo.");
                return;
            }

            // 🕒 Đợi tối đa 3 giây để đảm bảo WebSocket mở hoàn toàn
            int retries = 0;
            while (!peerClient.isOpen() && retries < 6) {
                Thread.sleep(500);
                retries++;
            }

            if (!peerClient.isOpen()) {
                System.out.println("⚠️ [P2P] Peer vẫn chưa sẵn sàng sau 3s, hủy gửi.");
                return;
            }

            JSONObject msg = new JSONObject();
            msg.put("action", "sync_booking");
            msg.put("data", bookingData); // 🔄 đổi "booking" → "data" cho khớp bên server


            peerClient.send(msg.toString());
            System.out.println("🔄 [P2P] Gửi booking sang peer thành công!");

        } catch (Exception e) {
            System.out.println("⚠️ [P2P] Lỗi khi gửi booking sang peer: " + e.getMessage());
        }
    }

    // 📥 Nhận và lưu booking từ peer vào DB cục bộ
    private static void savePeerBooking(JSONObject booking) {
        try (Connection conn = DBConnection.getConnection()) {
            String check = """
                SELECT id FROM bookings 
                WHERE customer_id=? AND tour_id=? 
                  AND start_date=? AND end_date=?
            """;
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setInt(1, booking.getInt("customer_id"));
                ps.setInt(2, booking.getInt("tour_id"));
                ps.setString(3, booking.getString("start_date"));
                ps.setString(4, booking.getString("end_date"));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("⚙️ [PeerSync] Booking đã tồn tại, bỏ qua đồng bộ.");
                    return;
                }
            }

            String sql = """
                INSERT INTO bookings 
                (customer_id, tour_id, schedule_id, number_of_people, total_price, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, booking.getInt("customer_id"));
                ps.setInt(2, booking.getInt("tour_id"));
                ps.setInt(3, booking.getInt("schedule_id"));
                ps.setInt(4, booking.getInt("number_of_people"));
                ps.setDouble(5, booking.getDouble("total_price"));
                ps.setString(6, booking.getString("start_date"));
                ps.setString(7, booking.getString("end_date"));
                ps.setString(8, booking.optString("status", "Đã đặt"));
                ps.executeUpdate();
            }

            System.out.println("✅ [PeerSync] Nhận booking mới từ peer và lưu thành công!");

        } catch (Exception e) {
            System.out.println("⚠️ [PeerSync] Lỗi khi lưu booking từ peer: " + e.getMessage());
        }
    }
    // 📤 Gửi trạng thái đăng nhập
    public static void broadcastLoginStatus(int userId, boolean isLogin) {
        try {
            if (peerClient == null || !peerClient.isOpen()) {
                System.out.println("⚠️ [P2P] Peer chưa sẵn sàng.");
                return;
            }

            JSONObject msg = new JSONObject();
            msg.put("action", isLogin ? "user_logged_in" : "user_logged_out");
            msg.put("data", new JSONObject().put("user_id", userId));

            peerClient.send(msg.toString());
            System.out.println("🔄 [P2P] Gửi trạng thái user_id=" + userId + (isLogin ? " (login)" : " (logout)"));
        } catch (Exception e) {
            System.out.println("⚠️ [P2P] Lỗi khi gửi trạng thái đăng nhập: " + e.getMessage());
        }
    }
    // 🧩 Cập nhật trạng thái đăng nhập của user theo dữ liệu từ peer
    private static void markUserLoginStatus(int userId, boolean isLogin) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customers SET is_logged_in=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, isLogin);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
            System.out.println("✅ [PeerSync] " + (isLogin ? "Khóa" : "Mở khóa") + " user_id=" + userId + " từ peer");
        } catch (Exception e) {
            System.out.println("⚠️ [PeerSync] Lỗi khi cập nhật trạng thái user: " + e.getMessage());
        }
    }
    public static void broadcastRegister(JSONObject userData) {
        try {
            if (peerClient == null || !peerClient.isOpen()) {
                System.out.println("⚠️ [P2P] Peer chưa sẵn sàng.");
                return;
            }

            JSONObject msg = new JSONObject();
            msg.put("action", "sync_register");
            msg.put("data", userData);

            peerClient.send(msg.toString());
            System.out.println("🔄 [P2P] Gửi đăng ký user sang peer");

        } catch (Exception e) {
            System.out.println("⚠️ [P2P] Lỗi send user register: " + e.getMessage());
        }
    }
    private static void savePeerRegister(JSONObject user) {
        try (Connection conn = DBConnection.getConnection()) {

            // check duplicate
            String check = "SELECT id FROM customers WHERE email=?";
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, user.getString("email"));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("⚙️ [PeerSync] User đã tồn tại → bỏ qua");
                    return;
                }
            }

            String sql = """
            INSERT INTO customers (id, full_name, email, phone, password_hash, email_verified)
            VALUES (?, ?, ?, ?, ?, 1)
        """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, user.getInt("id"));
                ps.setString(2, user.getString("full_name"));
                ps.setString(3, user.getString("email"));
                ps.setString(4, user.optString("phone", ""));
                ps.setString(5, user.getString("password_hash"));
                ps.executeUpdate();
            }

            System.out.println("✅ [PeerSync] Đồng bộ đăng ký user thành công!");

        } catch (Exception e) {
            System.out.println("⚠️ [PeerSync] Lỗi lưu user từ peer: " + e.getMessage());
        }
    }

}
