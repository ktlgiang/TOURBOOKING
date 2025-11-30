package org.example.tourbooking.p2p;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class PeerClient extends WebSocketClient {

    public PeerClient(String peerUri) throws Exception {
        super(new URI(peerUri));
        connectBlocking(); // chờ kết nối xong
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("🔗 [P2P] Kết nối tới peer thành công: " + getURI());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("📨 [P2P] Nhận từ peer: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ [P2P] Ngắt kết nối peer: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("⚠️ [P2P] Lỗi: " + ex.getMessage());
    }
}
