package org.example.tourbooking.server.tour;

public class TourServerApp {
    public static void main(String[] args) {
        int port = 8082;
        TourWebSocketServer server = new TourWebSocketServer(port);
        server.start();
        System.out.println("✅ TourServer đang chạy tại ws://localhost:" + port + "/tour");
    }
}
