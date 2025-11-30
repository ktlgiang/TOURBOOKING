//package org.example.tourbooking.client;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class WebSocketClientSingleton {
//
//    private static WebSocketClient client;
//    private static WebSocketListener listener;
//    private static final Queue<String> pendingMessages = new LinkedList<>();
//
//    // ✅ Interface callback cho UI
//    public interface WebSocketListener {
//        void onOpen();
//        void onMessage(String message);
//        void onClose(String reason);
//        void onError(Exception ex);
//    }
//
//    // ✅ Cho UI đăng ký listener
//    public static void setListener(WebSocketListener l) {
//        listener = l;
//    }
//
//    // ✅ Lấy instance client (kết nối nếu chưa có)
//    public static WebSocketClient getInstance(String serverUrl) {
//        if (client == null || !client.isOpen()) {
//            try {
//                final String url = serverUrl; // <-- BẮT BUỘC phải final hoặc effectively final
//
//                client = new WebSocketClient(new URI(url)) {
//                    @Override
//                    public void onOpen(ServerHandshake handshake) {
//                        System.out.println("✅ [Client] Connected to " + url);
//                        if (listener != null) listener.onOpen();
//
//                        // Gửi hết các message đang chờ
//                        while (!pendingMessages.isEmpty()) {
//                            String msg = pendingMessages.poll();
//                            client.send(msg);
//                            System.out.println("📤 [Client] Flushed pending to " + url + ": " + msg);
//                        }
//                    }
//
//                    @Override
//                    public void onMessage(String message) {
//                        System.out.println("📩 [Client] Received from " + url + ": " + message);
//                        if (listener != null) listener.onMessage(message);
//                    }
//
//                    @Override
//                    public void onClose(int code, String reason, boolean remote) {
//                        System.out.println("❌ [Client] Closed " + url + " | reason: " + reason);
//                        if (listener != null) listener.onClose(reason);
//                    }
//
//                    @Override
//                    public void onError(Exception ex) {
//                        System.err.println("⚠️ [Client] Error at " + url + ": " + ex.getMessage());
//                        if (listener != null) listener.onError(ex);
//                    }
//                };
//
//                client.connect();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return client;
//    }
//
//    // ✅ Gửi message an toàn — tự queue nếu chưa sẵn sàng
//    public static void sendMessage(String msg) {
//        if (client != null && client.isOpen()) {
//            client.send(msg);
//            System.out.println("📤 [Client] Sent: " + msg);
//        } else {
//            System.out.println("⏳ [Client] Connection not ready, queueing: " + msg);
//            pendingMessages.add(msg);
//        }
//    }
//}
