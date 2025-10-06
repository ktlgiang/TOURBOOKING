package org.example.tourbooking.server.tour;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TourWebSocketServer extends WebSocketServer {

    public TourWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String time = getTime();
        System.out.println("🔗 [" + time + "] Client kết nối: " + conn.getRemoteSocketAddress());
        JSONObject welcome = new JSONObject();
        welcome.put("status", "connected");
        welcome.put("server", "TourServer");
        welcome.put("message", "Chào mừng bạn đến TourServer!");
        conn.send(welcome.toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String time = getTime();
        try {
            System.out.println("📩 [" + time + "] [TourServer] Nhận: " + message);

            // Xử lý JSON
            String response = TourHandler.handleMessage(message);

            // Đảm bảo phản hồi dạng UTF-8
            conn.send(new String(response.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

            System.out.println("📤 [" + time + "] [TourServer] Gửi phản hồi: " + response);

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Lỗi xử lý yêu cầu: " + e.getMessage());
            conn.send(error.toString());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String time = getTime();
        System.out.println("❌ [" + time + "] Client ngắt kết nối (" + reason + ")");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        String time = getTime();
        System.err.println("⚠️ [" + time + "] Lỗi trên TourServer: " + ex.getMessage());
        ex.printStackTrace();
        if (conn != null && !conn.isClosed()) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Lỗi máy chủ: " + ex.getMessage());
            conn.send(error.toString());
        }
    }

    @Override
    public void onStart() {
        System.out.println("🚀 TourWebSocketServer sẵn sàng tại ws://localhost:" + getPort() + "/tour");
        setConnectionLostTimeout(30); // Kiểm tra kết nối mỗi 30s
    }

    // 🕓 Hàm hỗ trợ ghi log có timestamp
    private String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
