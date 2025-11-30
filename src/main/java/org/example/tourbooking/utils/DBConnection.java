package org.example.tourbooking.utils;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private static final String DEFAULT_FILE = "db.properties";

    public static Connection getConnection() {
        String configFile = System.getenv().getOrDefault("DB_CONFIG", DEFAULT_FILE);

        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(configFile)) {

            if (input == null) {
                System.out.println("❌ Không tìm thấy file cấu hình: " + configFile);
                return null;
            }

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            Class.forName(driver);
            System.out.println("✅ Kết nối thành công database: " + url);
            return DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Kết nối thất bại!");
            return null;
        }
    }
}
