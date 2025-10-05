package org.example.tourbooking.server.auth;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class AuthWebSocketServer extends WebSocketServer {

    public AuthWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🔗 [AuthServer] Client đã kết nối: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ [AuthServer] Client ngắt kết nối: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("📩 [AuthServer] Nhận từ client: " + message);

        // Gọi AuthHandler để xử lý logic
        String response = AuthHandler.handleMessage(message);

        System.out.println("📤 [AuthServer] Gửi về client: " + response);
        conn.send(response);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("⚠️ [AuthServer] Lỗi: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("🚀 [AuthServer] Server WebSocket đã khởi động thành công!");
    }
}
