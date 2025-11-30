package org.example.tourbooking.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;

    private static final String SERVER_URL = "ws://172.20.10.2:8081/auth";

    @FXML
    private void onRegister() {
        String name = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
            return;
        }

        JSONObject msg = new JSONObject();
        msg.put("action", "register");
        msg.put("full_name", name);
        msg.put("email", email);
        msg.put("phone", phone);
        msg.put("password", password);

        AuthWebSocketClient.getInstance(SERVER_URL);
        AuthWebSocketClient.sendMessage(msg.toString());

        AuthWebSocketClient.setListener(new AuthWebSocketClient.WebSocketListener() {
            @Override
            public void onMessage(String message) {
                JSONObject resp = new JSONObject(message);
                String status = resp.optString("status");
                String msg = resp.optString("message");

                Platform.runLater(() -> {
                    if ("success".equals(status)) {
                        showAlert("Đăng ký thành công! Vui lòng đăng nhập.", Alert.AlertType.INFORMATION);
                        goToLogin();
                    } else {
                        showAlert(msg, Alert.AlertType.ERROR);
                    }
                });
            }

            @Override public void onOpen() {}
            @Override public void onClose(String reason) {}
            @Override public void onError(Exception ex) {
                Platform.runLater(() -> showAlert("Lỗi kết nối: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void onGoToLogin() {
        goToLogin();
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}