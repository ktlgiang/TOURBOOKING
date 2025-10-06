package org.example.tourbooking.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.tourbooking.model.BookingItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TourListController {
    @FXML private ListView<String> tourListView;
    @FXML private ComboBox<String> tourComboBox;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TableView<BookingItem> myToursTable;
    @FXML private TableColumn<BookingItem, String> colTourName;
    @FXML private TableColumn<BookingItem, String> colStartDate;
    @FXML private TableColumn<BookingItem, Number> colPeople;
    @FXML private TableColumn<BookingItem, String> colStatus;

    private void loadMyBookings() {
        JSONObject req = new JSONObject();
        req.put("action", "get_user_bookings");
        req.put("user_id", currentUserId);

        BookingWebSocketClient.getInstance("ws://localhost:8083/booking");
        BookingWebSocketClient.setListener(new BookingWebSocketClient.WebSocketListener() {
            @Override
            public void onMessage(String message) {
                JSONObject resp = new JSONObject(message);
                if ("success".equals(resp.optString("status"))) {
                    JSONArray arr = resp.optJSONArray("bookings");
                    List<BookingItem> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject b = arr.getJSONObject(i);
                        String name = "Tour #" + b.optInt("tour_id");
                        String date = b.optString("booking_date");
                        int people = b.optInt("number_of_people");
                        String status = b.optString("status");
                        list.add(new BookingItem(name, date, people, status));
                    }

                    Platform.runLater(() -> {
                        colTourName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTourName()));
                        colStartDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStartDate()));
                        colPeople.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPeople()));
                        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
                        myToursTable.getItems().setAll(list);
                    });
                }
            }

            @Override public void onOpen() {}
            @Override public void onClose(String reason) {}
            @Override public void onError(Exception ex) {}
        });

        BookingWebSocketClient.sendMessage(req.toString());
    }

    private int currentUserId;
    private String currentUserEmail;

    private static final String TOUR_SERVER_URL = "ws://localhost:8082/tour";

    public void setUserInfo(int userId, String email, String fullName, String phone) {
        this.currentUserId = userId;
        this.currentUserEmail = email;

        // ✅ Gán dữ liệu vào 3 ô trong form
        if (nameField != null) nameField.setText(fullName);
        if (emailField != null) emailField.setText(email);
        if (phoneField != null) phoneField.setText(phone);

        System.out.println("👤 Thông tin user đã được tự động điền: " + fullName + " | " + email + " | " + phone);
        loadMyBookings(); // 👈 tự tải danh sách tour sau khi đăng nhập

    }


    @FXML
    private void initialize() {
        System.out.println("🚀 [TourListController] Kết nối tới TourServer...");
        TourWebSocketClient.getInstance(TOUR_SERVER_URL);
        TourWebSocketClient.setListener(new TourWebSocketClient.WebSocketListener() {
            @Override
            public void onOpen() {
                System.out.println("✅ [TourListController] Đã kết nối với TourServer!");
                requestTourList();
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 [TourListController] Nhận từ server: " + message);
                JSONObject response = new JSONObject(message);
                if ("success".equals(response.optString("status")) && response.has("tours")) {
                    JSONArray tours = response.getJSONArray("tours");
                    List<String> names = new ArrayList<>();
                    for (int i = 0; i < tours.length(); i++) {
                        JSONObject t = tours.getJSONObject(i);
                        names.add(t.getString("name") + " - " + String.format("%,.0fđ", t.getDouble("price")));
                    }
                    Platform.runLater(() -> {
                        tourListView.setItems(FXCollections.observableArrayList(names));
                        tourComboBox.setItems(FXCollections.observableArrayList(names));
                    });
                }
            }

            @Override
            public void onClose(String reason) {
                Platform.runLater(() -> showAlert("TourServer ngắt kết nối: " + reason, Alert.AlertType.WARNING));
            }

            @Override
            public void onError(Exception ex) {
                Platform.runLater(() -> showAlert("Lỗi TourServer: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }

    private void requestTourList() {
        JSONObject req = new JSONObject();
        req.put("action", "get_tours");
        System.out.println("📤 [TourListController] Gửi: " + req);
        TourWebSocketClient.sendMessage(req.toString());
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private void onBookSelected() {
        System.out.println("🟢 [TourListController] Nút 'Xác nhận đặt tour' được nhấn!");

        String selectedTour = tourComboBox.getValue();
        if (selectedTour == null) {
            showAlert("Vui lòng chọn tour để đặt!", Alert.AlertType.WARNING);
            return;
        }

        int tourId = 1; // tạm test, sau này bạn có thể lấy id thực
        JSONObject request = new JSONObject();
        request.put("action", "book_tour");
        request.put("user_id", currentUserId);
        request.put("tour_id", tourId);

        System.out.println("📤 [TourListController] Gửi booking: " + request);
        BookingWebSocketClient.getInstance("ws://localhost:8083/booking");
        BookingWebSocketClient.sendMessage(request.toString());
        BookingWebSocketClient.setListener(new BookingWebSocketClient.WebSocketListener() {
            @Override
            public void onOpen() {
                System.out.println("✅ [TourListController] Kết nối tới BookingServer!");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("📩 [TourListController] Phản hồi từ BookingServer: " + message);
                JSONObject res = new JSONObject(message);
                String status = res.optString("status");
                String msg = res.optString("message");

                Platform.runLater(() -> {
                    Alert.AlertType type = "success".equals(status)
                            ? Alert.AlertType.INFORMATION
                            : Alert.AlertType.WARNING;

                    Alert alert = new Alert(type);
                    alert.setTitle("Kết quả đặt tour");
                    alert.setHeaderText(null);
                    alert.setContentText(msg);
                    alert.showAndWait();

                    // ✅ Nếu đặt thành công thì tự tải lại danh sách tour
                    if ("success".equals(status)) {
                        loadMyBookings();
                    }
                });

            }

            @Override
            public void onClose(String reason) {
                Platform.runLater(() -> showAlert("BookingServer ngắt kết nối: " + reason, Alert.AlertType.WARNING));
            }

            @Override
            public void onError(Exception ex) {
                Platform.runLater(() -> showAlert("Lỗi BookingServer: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        });


    }


}
