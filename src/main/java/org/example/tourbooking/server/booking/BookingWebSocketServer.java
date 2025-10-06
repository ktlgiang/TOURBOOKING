package org.example.tourbooking.server.booking;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

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
        String response = BookingHandler.handleMessage(message);
        System.out.println("📤 [BookingServer] Gửi lại: " + response);
        conn.send(response);
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
}
