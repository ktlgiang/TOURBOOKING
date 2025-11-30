package org.example.tourbooking.p2p;

public class PeerServerApp {
    public static void main(String[] args) {
        int port = 8090;
        PeerWebSocketServer server = new PeerWebSocketServer(port);
        server.start();
        System.out.println("🚀 [PeerServer] Listening on ws://localhost:" + port);

        // 🔁 Kết nối ngược sang máy kia
        String peerUrl = "ws://172.20.10.2:8090";
        PeerSyncManager.connectToPeer(peerUrl);
    }
}