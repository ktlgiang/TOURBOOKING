package org.example.tourbooking.server.auth;

public class AuthServerApp {
    public static void main(String[] args) {
        int port = 8081; // hoặc cổng bạn chọn
        AuthWebSocketServer server = new AuthWebSocketServer(port);
        server.start();
        System.out.println("🚀 [AuthServer] Đang lắng nghe tại ws://localhost:" + port);
    }
}
