package org.example.tourbooking.utils;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn != null) return conn;

        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                System.out.println("❌ Không tìm thấy file db.properties!");
                return null;
            }

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Kết nối thành công MySQL!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Kết nối thất bại!");
        }

        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("🔒 Đã đóng kết nối MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
