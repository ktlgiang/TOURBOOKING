package org.example.tourbooking.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class PeerWebSocketServer extends WebSocketServer {

    public PeerWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🔗 [PeerServer] Kết nối mới từ: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ [PeerServer] Ngắt kết nối từ: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("📩 [PeerServer] Nhận từ peer: " + message);

        try {
            JSONObject data = new JSONObject(message);
            String action = data.optString("action", "");

            // Gửi lại cho máy còn lại (đồng bộ 2 chiều)
            if (action.equals("sync_booking") || action.equals("user_logged_in") || action.equals("user_logged_out")) {
                PeerSyncManager.connectToPeer("ws://172.20.10.2:8090/peer"); // sẽ bỏ khi tự động sync
            }

            // Phản hồi nếu cần
            conn.send(new JSONObject().put("status", "received").toString());
        } catch (Exception e) {
            System.out.println("⚠️ [PeerServer] Lỗi xử lý message: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("⚠️ [PeerServer] Lỗi: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("🚀 [PeerServer] Đang chạy tại cổng " + getPort());
    }
}