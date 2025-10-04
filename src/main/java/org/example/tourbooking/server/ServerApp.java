package org.example.tourbooking.server;

public class ServerApp {
    public static void main(String[] args) {
        WebSocketServer server = new WebSocketServer(8080);
        server.start();
    }
}
