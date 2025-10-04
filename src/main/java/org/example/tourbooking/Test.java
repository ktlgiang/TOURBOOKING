package org.example.tourbooking;
import org.example.tourbooking.utils.DBConnection;

import java.sql.Connection;

public class Test {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Kết nối OK!");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    }
}