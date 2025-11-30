package org.example.tourbooking.server.auth;

import org.example.tourbooking.p2p.PeerSyncManager;
import org.example.tourbooking.p2p.PeerWebSocketServer;

public class AuthServerApp {
    public static void main(String[] args) {
        int port = 8081;
        AuthWebSocketServer server = new AuthWebSocketServer(port);
        server.start();
        System.out.println("🚀 [AuthServer] Đang lắng nghe tại ws://localhost:" + port);

        // ⚙️ Khởi động PeerWebSocketServer (dùng cổng khác)
        try {
            int peerPort = 8090;
            PeerWebSocketServer peerServer = new PeerWebSocketServer(peerPort);
            peerServer.start();
            System.out.println("🤝 [P2P] Server P2P đang chạy tại ws://localhost:" + peerPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔗 Kết nối đến peer của máy kia
        // 💡 Thay localhost bằng IP của máy còn lại
        PeerSyncManager.connectToPeer("ws://172.20.10.2:8090/peer");
    }
}
