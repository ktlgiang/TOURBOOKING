package org.example.tourbooking.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

public class WebSocketClientSingleton {
    private static WebSocketClient client;
    private static WebSocketListener listener;
    private static Queue<String> pendingMessages = new LinkedList<>();

    // interface callback cho UI
    public interface WebSocketListener {
        void onOpen();
        void onMessage(String message);
        void onClose(String reason);
        void onError(Exception ex);
    }

    // cho UI đăng ký listener
    public static void setListener(WebSocketListener l) {
        listener = l;
    }

    // lấy instance client (kết nối nếu chưa có)
    public static WebSocketClient getInstance(String serverUrl) {
        if (client == null || !client.isOpen()) {
            try {
                client = new WebSocketClient(new URI(serverUrl)) {
                    @Override
                    public void onOpen(ServerHandshake handshake) {
                        System.out.println("🔗 [Client] Connected to " + serverUrl);
                        if (listener != null) listener.onOpen();

                        // gửi hết các message đang chờ
                        while (!pendingMessages.isEmpty()) {
                            String m = pendingMessages.poll();
                            client.send(m);
                            System.out.println("📤 [Client] Flushed pending: " + m);
                        }
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("📩 [Client] Received: " + message);
                        if (listener != null) listener.onMessage(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("❌ [Client] Closed: " + reason);
                        if (listener != null) listener.onClose(reason);
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.err.println("⚠️ [Client] Error: " + ex.getMessage());
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

    // ✅ phương thức gửi an toàn
    public static void sendMessage(String msg) {
        if (client != null && client.isOpen()) {
            client.send(msg);
            System.out.println("📤 [Client] Sent: " + msg);
        } else {
            System.out.println("⏳ [Client] Connection not ready, queueing: " + msg);
            pendingMessages.add(msg);
        }
    }
}
