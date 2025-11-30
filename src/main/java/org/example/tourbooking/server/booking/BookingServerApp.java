package org.example.tourbooking.server.booking;

import org.example.tourbooking.utils.DatabaseManager;
import org.example.tourbooking.p2p.PeerSyncManager;

public class BookingServerApp {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8083"));
        String peerUrl = System.getenv().getOrDefault("PEER_URL", "ws://172.20.10.2:8084");
        String dbConfig = System.getenv().getOrDefault("DB_CONFIG", "db.properties");

        System.out.println("🚀 BookingServer đang chạy tại ws://172.20.10.2:" + port + "/booking");
        System.out.println("🗄️  File cấu hình database: " + dbConfig);
        System.out.println("🌐 Peer URL: " + peerUrl);

        DatabaseManager.initialize(dbConfig);

        BookingWebSocketServer server = new BookingWebSocketServer(port);
        server.start();

        PeerSyncManager.connectToPeer(peerUrl);
    }
}
