package org.example.tourbooking.server.auth;

public class AuthServerApp {
    public static void main(String[] args) {
        int port = 8081; // Auth server chạy port 8081
        AuthWebSocketServer server = new AuthWebSocketServer(port);
        server.start();
        System.out.println("✅ AuthServer đang chạy tại ws://localhost:" + port + "/auth");
    }
}
