package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

public class BookingWebSocketClient {
    private static WebSocketClient client;
    private static WebSocketListener listener;
    private static final Queue<String> pending = new LinkedList<>();

    public interface WebSocketListener {
        void onOpen();
        void onMessage(String message);
        void onClose(String reason);
        void onError(Exception ex);
    }

    public static void setListener(WebSocketListener l) {
        listener = l;
    }

    public static WebSocketClient getInstance(String serverUrl) {
        if (client == null || !client.isOpen()) {
            try {
                client = new WebSocketClient(new URI(serverUrl)) {
                    @Override
                    public void onOpen(ServerHandshake handshake) {
                        System.out.println("✅ [BookingClient] Connected to " + serverUrl);
                        if (listener != null) listener.onOpen();
                        while (!pending.isEmpty()) {
                            String msg = pending.poll();
                            send(msg);
                            System.out.println("📤 [BookingClient] Flushed: " + msg);
                        }
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("📩 [BookingClient] Received: " + message);
                        if (listener != null) listener.onMessage(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("❌ [BookingClient] Closed: " + reason);
                        if (listener != null) listener.onClose(reason);
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.err.println("⚠️ [BookingClient] Error: " + ex.getMessage());
                        if (listener != null) listener.onError(ex);
                    }
                };
                client.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public static void sendMessage(String msg) {
        if (client != null && client.isOpen()) {
            client.send(msg);
            System.out.println("📤 [BookingClient] Sent: " + msg);
        } else {
            pending.add(msg);
            System.out.println("⏳ [BookingClient] Queued: " + msg);
        }
    }
}
