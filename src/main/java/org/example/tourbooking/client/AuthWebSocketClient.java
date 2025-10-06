package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

public class AuthWebSocketClient {
    private static WebSocketClient client;
    private static WebSocketListener listener;
    private static final Queue<String> pendingMessages = new LinkedList<>();

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
                final String url = serverUrl;
                client = new WebSocketClient(new URI(url)) {
                    @Override
                    public void onOpen(ServerHandshake handshake) {
                        System.out.println("✅ [AuthClient] Connected to " + url);
                        if (listener != null) listener.onOpen();
                        while (!pendingMessages.isEmpty()) {
                            String msg = pendingMessages.poll();
                            client.send(msg);
                            System.out.println("📤 [AuthClient] Flushed pending: " + msg);
                        }
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("📩 [AuthClient] Received: " + message);
                        if (listener != null) listener.onMessage(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("❌ [AuthClient] Closed: " + reason);
                        if (listener != null) listener.onClose(reason);
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.err.println("⚠️ [AuthClient] Error: " + ex.getMessage());
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
        } else {
            pendingMessages.add(msg);
            System.out.println("⏳ [AuthClient] Queued: " + msg);
        }
    }
}
