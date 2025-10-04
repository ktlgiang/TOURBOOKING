package org.example.tourbooking.client;

import java.io.*;
import java.net.Socket;

public class WebSocketClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Gửi yêu cầu JSON
            out.println("{\"action\":\"getTours\"}");

            // Nhận phản hồi từ server
            String response = in.readLine();
            System.out.println("📥 Server trả về: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
