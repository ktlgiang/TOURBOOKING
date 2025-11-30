package org.example.tourbooking.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.tourbooking.model.BookingItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Window;


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
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;



    private void loadMyBookings() {
        JSONObject req = new JSONObject();
        req.put("action", "get_user_bookings");
        req.put("user_id", currentUserId);

        BookingWebSocketClient.getInstance("ws://172.20.10.2:8083/booking");
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

    private static final String TOUR_SERVER_URL = "ws://172.20.10.2:8082/tour";

    public void setUserInfo(int userId, String email, String fullName, String phone) {
        this.currentUserId = userId;
        this.currentUserEmail = email;

        // ✅ Gán dữ liệu vào 3 ô trong form
        if (nameField != null) nameField.setText(fullName);
        if (emailField != null) emailField.setText(email);
        if (phoneField != null) phoneField.setText(phone);

        System.out.println("👤 Thông tin user đã được tự động điền: " + fullName + " | " + email + " | " + phone);
        loadMyBookings(); // 👈 tự tải danh sách tour sau khi đăng nhập

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (currentUserId > 0) {
                    JSONObject logoutMsg = new JSONObject();
                    logoutMsg.put("action", "logout");
                    logoutMsg.put("user_id", currentUserId);
                    AuthWebSocketClient.sendMessage(logoutMsg.toString());
                    System.out.println("❎ [AutoLogout] Đã tự động đăng xuất khi đóng cửa sổ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

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
        // 🟢 Khi cửa sổ bị đóng (bấm X)
        Platform.runLater(() -> {
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
            stage.setOnCloseRequest(event -> {
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("action", "logout");
                    msg.put("user_id", currentUserId);
                    AuthWebSocketClient.sendMessage(msg.toString());
                    System.out.println("❎ [AutoLogout] Đã tự động đăng xuất khi đóng cửa sổ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
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

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Vui lòng chọn ngày bắt đầu và ngày kết thúc!", Alert.AlertType.WARNING);
            return;
        }

        int tourId = 1; // sau có thể thay = lấy từ comboBox
        JSONObject request = new JSONObject();
        request.put("action", "book_tour");
        request.put("user_id", currentUserId);
        request.put("tour_id", tourId);
        request.put("start_date", startDatePicker.getValue().toString());
        request.put("end_date", endDatePicker.getValue().toString());

        System.out.println("📤 [TourListController] Gửi booking: " + request);
        BookingWebSocketClient.getInstance("ws://172.20.10.2:8083/booking");
        BookingWebSocketClient.sendMessage(request.toString());

        BookingWebSocketClient.setListener(new BookingWebSocketClient.WebSocketListener() {
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

                    if ("success".equals(status)) {
                        loadMyBookings(); // tải lại danh sách
                    }
                });
            }

            @Override public void onOpen() {}
            @Override public void onClose(String reason) {}
            @Override public void onError(Exception ex) {}
        });
    }
    @FXML
    private void onLogout() {
        System.out.println("🚪 [TourListController] Người dùng nhấn nút đăng xuất");

        JSONObject msg = new JSONObject();
        msg.put("action", "logout");
        msg.put("user_id", currentUserId);
        AuthWebSocketClient.sendMessage(msg.toString());

        AuthWebSocketClient.setListener(new AuthWebSocketClient.WebSocketListener() {
            @Override
            public void onMessage(String message) {
                JSONObject resp = new JSONObject(message);
                if ("success".equals(resp.optString("status"))) {
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                            Scene scene = new Scene(loader.load());

                            // 🟢 Thêm CSS cho màn hình login
                            scene.getStylesheets().add(
                                    getClass().getResource("/styles/login.css").toExternalForm()
                            );


                            // ✅ Lấy cửa sổ hiện tại (Stage đang mở)
                            Stage stage = (Stage) Stage.getWindows()
                                    .filtered(Window::isShowing)
                                    .get(0);

                            stage.setScene(scene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
            }

            @Override public void onOpen() {}
            @Override public void onClose(String reason) {}
            @Override public void onError(Exception ex) {}
        });
    }




}
