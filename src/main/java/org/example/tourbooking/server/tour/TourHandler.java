package org.example.tourbooking.server.tour;

import org.json.JSONObject;
import java.util.List;
import org.example.tourbooking.dao.TourDAO;
import org.example.tourbooking.model.Tour;

public class TourHandler {
    public static String handleMessage(String message) {
        JSONObject request = new JSONObject(message);
        String action = request.optString("action");

        JSONObject response = new JSONObject();

        switch (action) {
            case "getTours":
                TourDAO dao = new TourDAO();
                List<Tour> tours = dao.getAllTours();
                response.put("status", "success");
                response.put("tours", tours); // có thể convert sang JSONArray
                break;

            default:
                response.put("status", "error");
                response.put("message", "Yêu cầu không hợp lệ");
        }

        return response.toString();
    }
}
