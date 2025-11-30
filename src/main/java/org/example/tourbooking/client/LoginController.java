package org.example.tourbooking.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.JSONObject;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private static final String SERVER_URL = "ws://172.20.10.2:8081/auth";

    @FXML
    private void initialize() {
        // ✅ Đăng ký listener mới cho AuthWebSocketClient
        AuthWebSocketClient.setListener(new AuthWebSocketClient.WebSocketListener() {
            @Override
            public void onOpen() {
                System.out.println("✅ [LoginController] Kết nối AuthServer thành công!");
            }

            @Override
            public void onMessage(String message) {
                JSONObject response = new JSONObject(message);
                String status = response.optString("status");
                String msg = response.optString("message");

                Platform.runLater(() -> {
                    if (status.equals("success")) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tour_list.fxml"));
                            Scene scene = new Scene(loader.load());

                            TourListController tourController = loader.getController();

                            int userId = response.has("user_id") ? response.getInt("user_id") : -1;                            String email = response.optString("email", "");
                            String fullName = response.optString("full_name", "");
                            String phone = response.optString("phone", "");

                            tourController.setUserInfo(userId, email, fullName, phone);

                            scene.getStylesheets().add(getClass().getResource("/styles/tour_list.css").toExternalForm());
                            Stage stage = (Stage) Stage.getWindows()
                                    .filtered(Window::isShowing)
                                    .stream()
                                    .findFirst()
                                    .orElse(null);

                            if (stage == null) return; // không làm gì nếu cửa sổ đã đóng                            stage.setScene(scene);
                            stage.setTitle("Danh Sách Tour");
                            stage.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Không thể mở trang Tour List: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    } else {
                        showAlert(msg, Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            public void onClose(String reason) {
                Platform.runLater(() -> showAlert("Mất kết nối: " + reason, Alert.AlertType.WARNING));
            }

            @Override
            public void onError(Exception ex) {
                Platform.runLater(() -> showAlert("Lỗi: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
            return;
        }

        JSONObject msg = new JSONObject();
        msg.put("action", "login");
        msg.put("email", email);
        msg.put("password", password);

        System.out.println("📤 [LoginController] Gửi: " + msg);
        AuthWebSocketClient.getInstance(SERVER_URL);
        AuthWebSocketClient.sendMessage(msg.toString());
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void onGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/register.css").toExternalForm());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng ký tài khoản");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở trang đăng ký: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
