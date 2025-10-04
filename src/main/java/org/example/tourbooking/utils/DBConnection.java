package org.example.tourbooking.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load file db.properties
                Properties props = new Properties();
                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                props.load(input);

                // Lấy thông tin từ file
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String pass = props.getProperty("db.password");
                String driver = props.getProperty("db.driver");

                // Nạp Driver
                Class.forName(driver);
                // Kết nối
                connection = DriverManager.getConnection(url, user, pass);

                System.out.println("✅ Kết nối thành công MySQL!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
