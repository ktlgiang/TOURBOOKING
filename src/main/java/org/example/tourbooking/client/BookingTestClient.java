package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BookingTestClient {
    public static void main(String[] args) throws Exception {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8083/booking")) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("🔗 Kết nối BookingServer thành công!");

                // Gửi request đặt tour
                String request = "{ \"action\": \"bookTour\", \"customerId\": 1, \"tourId\": 2 }";
                send(request);
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 Nhận từ server: " + message);

                // Test thêm lấy danh sách booking
                String request = "{ \"action\": \"getBookings\", \"customerId\": 1 }";
                send(request);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("❌ Mất kết nối BookingServer");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connectBlocking();

        Thread.sleep(5000); // chờ 5 giây để nhận phản hồi
        client.close();
    }
}
