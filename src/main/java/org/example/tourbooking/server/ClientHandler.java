package org.example.tourbooking.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.tourbooking.service.TourService;
import org.example.tourbooking.service.BookingService;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson;

    private TourService tourService;
    private BookingService bookingService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.gson = new Gson();
        this.tourService = new TourService();
        this.bookingService = new BookingService();
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                JsonObject request = gson.fromJson(message, JsonObject.class);
                String action = request.get("action").getAsString();

                JsonObject response = new JsonObject();

                switch (action) {
                    case "getTours":
                        response.add("data", gson.toJsonTree(tourService.getAllTours()));
                        break;
                    case "getBookings":
                        response.add("data", gson.toJsonTree(bookingService.getAllBookings()));
                        break;
                    default:
                        response.addProperty("error", "❌ Unknown action: " + action);
                }

                writer.println(gson.toJson(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
