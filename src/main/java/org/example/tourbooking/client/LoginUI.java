package org.example.tourbooking.client;

import javax.swing.*;
import java.awt.*;
import org.example.tourbooking.client.WebSocketClientSingleton;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONObject;

public class LoginUI extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // URL cố định của AuthServer
    private static final String SERVER_URL = "ws://localhost:8081/auth";

    public LoginUI() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(new JLabel()); // placeholder
        panel.add(loginButton);

        add(panel);

        // Đăng ký listener để UI nhận message từ server
        WebSocketClientSingleton.setListener(new WebSocketClientSingleton.WebSocketListener() {
            @Override
            public void onOpen() {
                System.out.println("✅ [LoginUI] Đã kết nối tới AuthServer!");
                JOptionPane.showMessageDialog(null, "Đã kết nối tới AuthServer!");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 [LoginUI] Nhận từ server: " + message);

                try {
                    JSONObject json = new JSONObject(message);
                    String status = json.optString("status");
                    String msg = json.optString("message");

                    if ("success".equalsIgnoreCase(status)) {
                        JOptionPane.showMessageDialog(null, "✅ " + msg);
                    } else if ("error".equalsIgnoreCase(status)) {
                        JOptionPane.showMessageDialog(null, "❌ " + msg);
                    } else {
                        JOptionPane.showMessageDialog(null, "⚠️ Server gửi dữ liệu lạ: " + message);
                    }

                } catch (Exception ex) {
                    System.err.println("❌ [LoginUI] Lỗi parse JSON: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Lỗi parse JSON: " + message);
                }
            }

            @Override
            public void onClose(String reason) {
                System.out.println("⚠️ [LoginUI] Mất kết nối: " + reason);
                JOptionPane.showMessageDialog(null, "Mất kết nối: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("❌ [LoginUI] Lỗi: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            }
        });

        // Bắt sự kiện khi nhấn nút Login
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            String msg = String.format("{\"action\":\"login\",\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password);

            System.out.println("📤 [LoginUI] Gửi tới server: " + msg);

            WebSocketClientSingleton.getInstance(SERVER_URL); // đảm bảo kết nối
            WebSocketClientSingleton.sendMessage(msg);        // gửi an toàn (có queue)

        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
