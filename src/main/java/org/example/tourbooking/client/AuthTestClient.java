package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class AuthTestClient {
    public static void main(String[] args) throws Exception {
        // Kết nối đến AuthServer
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8081/auth")) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("🔗 Đã kết nối tới AuthServer");

                // Gửi thử request login
                String loginRequest = "{ \"action\": \"login\", \"email\": \"test@example.com\", \"password\": \"123\" }";
                send(loginRequest);
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 Nhận từ server: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("❌ Mất kết nối AuthServer");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        // Chờ client kết nối
        client.connectBlocking();

        // Giữ cho chương trình chạy (chờ phản hồi)
        Thread.sleep(5000);
        client.close();
    }
}
