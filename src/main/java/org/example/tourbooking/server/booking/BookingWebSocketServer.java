package org.example.tourbooking.server.booking;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookingWebSocketServer extends WebSocketServer {

    public BookingWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🔗 [BookingServer] Client đã kết nối: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ [BookingServer] Client ngắt kết nối: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("📩 [BookingServer] Nhận: " + message);

        try {
            JSONObject req = new JSONObject(message);
            String action = req.optString("action", "");

            // 🔁 Nếu là dữ liệu đồng bộ từ máy khác
            if ("sync_booking".equals(action)) {
                JSONObject data = req.getJSONObject("data");
                insertBookingFromPeer(data);
                conn.send("{\"status\":\"ok\",\"message\":\"Đã đồng bộ booking\"}");
                return;
            }

            // ⚙️ Nếu là yêu cầu bình thường (client gửi)
            String response = BookingHandler.handleMessage(message);
            conn.send(response);

        } catch (Exception e) {
            e.printStackTrace();
            conn.send("{\"status\":\"error\",\"message\":\"Lỗi xử lý message\"}");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("⚠️ [BookingServer] Lỗi: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("✅ [BookingServer] Khởi động thành công!");
    }

    // =================== 💾 HÀM GHI BOOKING TỪ PEER ===================
    private void insertBookingFromPeer(JSONObject data) {
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
                ps.setDouble(5, data.getDouble("total_price"));
                ps.setString(6, data.getString("start_date"));
                ps.setString(7, data.getString("end_date"));
                ps.executeUpdate();
            }
            System.out.println("🔁 [Sync] Đồng bộ booking từ peer thành công!");
        } catch (Exception e) {
            System.err.println("❌ [Sync] Lỗi khi đồng bộ từ peer: " + e.getMessage());
        }
    }
}
