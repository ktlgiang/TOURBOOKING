package org.example.tourbooking.utils;

import org.example.tourbooking.utils.DBConnection;
import java.sql.Connection;

public class DatabaseManager {

    private static Connection connection;

    public static void initialize(String configFile) {
        try {
            System.out.println("🔌 Đang khởi tạo database từ: " + configFile);
            connection = DBConnection.getConnection();
            if (connection != null) {
                System.out.println("✅ Kết nối database thành công!");
            } else {
                System.out.println("❌ Không thể kết nối database!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("⚠️ Lỗi khi khởi tạo DatabaseManager: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
