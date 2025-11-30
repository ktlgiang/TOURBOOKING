package org.example.tourbooking.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AdminController {

    // --- Biến FXML ---
    @FXML private Label adminUserLabel;
    @FXML private Button logoutButton;
    @FXML private Button tourManagementButton;
    @FXML private Button bookingManagementButton;
    @FXML private Button userManagementButton;

    // Khối quản lý
    @FXML private VBox tourManagementView;
    @FXML private VBox bookingManagementView;
    @FXML private VBox userManagementView;

    // TableViews
    @FXML private TableView tourTable;
    @FXML private TableView bookingTable;
    @FXML private TableView userTable;

    // --- Biến Dữ liệu Cá nhân ---
    private int adminId;
    private String adminFullName;

    // Mảng chứa tất cả các VBox và Button liên quan
    private List<VBox> managementViews;
    private List<Button> navButtons;


    @FXML
    public void initialize() {
        // Khởi tạo danh sách các VBox và Button
        managementViews = Arrays.asList(tourManagementView, bookingManagementView, userManagementView);
        navButtons = Arrays.asList(tourManagementButton, bookingManagementButton, userManagementButton);

        // Cấu hình TableView (Đây là nơi bạn thêm các cột)
        configureTableViews();

        // Thiết lập mặc định
        showTourManagement();
    }

    // ==========================================================
    // === PHẦN BẮT BUỘC: Nhận dữ liệu từ LoginController ===
    // ==========================================================
    /**
     * Phương thức nhận thông tin admin từ màn hình đăng nhập.
     */
    public void setAdminInfo(int id, String email, String fullName, String phone, String role) {
        this.adminId = id;
        this.adminFullName = fullName;

        // Cập nhật nhãn chào mừng
        adminUserLabel.setText("Xin chào, " + fullName + " (" + role + ")");

        // Load dữ liệu ban đầu
        // loadTourData(); // Ví dụ: Gọi hàm tải dữ liệu Tour
    }

    // ==========================================================
    // === PHẦN LOGIC CHUYỂN ĐỔI GIAO DIỆN CHUYÊN NGHIỆP ===
    // ==========================================================

    /**
     * Phương thức chung để xử lý ẩn/hiện VBox và áp dụng CSS Accent Border.
     * Đồng thời, tô sáng nút điều hướng tương ứng.
     * @param activeView VBox đang hoạt động (hiển thị)
     * @param activeNavButton Button điều hướng tương ứng
     */
    private void setViewActive(VBox activeView, Button activeNavButton) {
        for (VBox view : managementViews) {
            boolean isActive = (view == activeView);

            // Ẩn/Hiện VBox
            view.setVisible(isActive);
            view.setManaged(isActive);

            // Thêm/Xóa lớp CSS Accent Border
            if (isActive) {
                // Thêm lớp ACTIVE mới (Management View)
                view.getStyleClass().add("management-view-active");
                view.getStyleClass().remove("management-view");
            } else {
                // Trở về lớp mặc định
                view.getStyleClass().add("management-view");
                view.getStyleClass().remove("management-view-active");
            }
        }

        // Xử lý tô sáng nút điều hướng (Sidebar)
        for (Button navButton : navButtons) {
            navButton.getStyleClass().remove("nav-active");
        }
        activeNavButton.getStyleClass().add("nav-active");
    }

    @FXML
    private void showTourManagement() {
        setViewActive(tourManagementView, tourManagementButton);
        // loadTourData(); // Tải dữ liệu khi chuyển tab
    }

    @FXML
    private void showBookingManagement() {
        setViewActive(bookingManagementView, bookingManagementButton);
        // loadBookingData();
    }

    @FXML
    private void showUserManagement() {
        setViewActive(userManagementView, userManagementButton);
        // loadUserData();
    }

    // ==========================================================
    // === PHẦN CẤU HÌNH VÀ HÀNH ĐỘNG KHÁC ===
    // ==========================================================

    /**
     * Cấu hình cột cho tất cả các TableView.
     */
    private void configureTableViews() {
        // Ví dụ cấu hình cột:
        // tourTable.getColumns().clear();
        // TableColumn<Tour, String> colName = new TableColumn<>("Tên Tour");
        // colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // tourTable.getColumns().add(colName);
        // ...
    }

    @FXML
    private void onLogout() {
        try {
            // TODO: BƯỚC 1: Gửi yêu cầu Đăng Xuất tới Server (nếu cần cập nhật is_logged_in)
            // AuthClient.sendLogoutRequest(adminId);

            // BƯỚC 2: Chuyển về màn hình Đăng nhập (Lấy Stage hiện tại)
            Node source = (Node) logoutButton;
            Stage currentStage = (Stage) source.getScene().getWindow();

            // Tải FXML của màn hình Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());

            currentStage.setTitle("Hệ thống Đặt Tour - Đăng nhập");
            currentStage.setScene(scene);
            currentStage.show();

        } catch (IOException e) {
            System.err.println("Lỗi chuyển màn hình đăng nhập: " + e.getMessage());
        }
    }

    @FXML
    private void onAddNewTour() {
        // Logic mở form/dialog để thêm tour mới
        System.out.println("Mở form Thêm Tour Mới.");
    }
}