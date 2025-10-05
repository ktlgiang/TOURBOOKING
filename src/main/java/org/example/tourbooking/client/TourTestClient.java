package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class TourTestClient {
    public static void main(String[] args) throws Exception {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8082/tour")) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("🔗 Kết nối TourServer thành công!");

                // Gửi request lấy danh sách tour
                String request = "{ \"action\": \"getTours\" }";
                send(request);
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 Nhận từ server: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("❌ Mất kết nối TourServer");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connectBlocking();

        Thread.sleep(5000); // chờ 5s nhận phản hồi
        client.close();
    }
}
