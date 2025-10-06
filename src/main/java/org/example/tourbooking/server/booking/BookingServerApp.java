package org.example.tourbooking.server.booking;

public class BookingServerApp {
    public static void main(String[] args) {
        int port = 8083; // ví dụ cổng cho booking server
        BookingWebSocketServer server = new BookingWebSocketServer(port);
        server.start();
        System.out.println("🚀 BookingServer đang chạy tại ws://localhost:" + port + "/booking");
    }
}
