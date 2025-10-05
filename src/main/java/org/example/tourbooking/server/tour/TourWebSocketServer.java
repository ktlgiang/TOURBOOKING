package org.example.tourbooking.server.tour;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;

public class TourWebSocketServer extends WebSocketServer {

    public TourWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🔗 Client kết nối TourServer: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ Client ngắt kết nối TourServer");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String response = TourHandler.handleMessage(message);
        conn.send(response);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("🚀 TourWebSocketServer sẵn sàng!");
    }
}
