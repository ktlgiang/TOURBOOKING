package org.example.tourbooking.server.tour;

import org.example.tourbooking.dao.TourDAO;
import org.example.tourbooking.model.Tour;
import org.example.tourbooking.model.TourSchedule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class TourHandler {

    public static String handleMessage(String message) {
        JSONObject request = new JSONObject(message);
        String action = request.optString("action");
        System.out.println("📥 [TourHandler] Nhận action = " + action);

        JSONObject response = new JSONObject();

        try {
            switch (action) {

                // 🧭 Lấy danh sách tour
                case "get_tours": {
                    TourDAO dao = new TourDAO();
                    List<Tour> tours = dao.getAllTours();

                    JSONArray tourArray = new JSONArray();
                    for (Tour t : tours) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", t.getId());
                        obj.put("name", t.getName());
                        obj.put("short_description", t.getShortDescription());
                        obj.put("price", t.getPrice());
                        obj.put("image_url", t.getImageUrl());
                        obj.put("duration_days", t.getDurationDays());
                        obj.put("difficulty_level", t.getDifficultyLevel());
                        obj.put("featured", t.isFeatured());
                        tourArray.put(obj);
                    }

                    response.put("status", "success");
                    response.put("tours", tourArray);
                    break;
                }

                // 🔍 Lấy chi tiết 1 tour
                case "get_tour_by_id": {
                    int id = request.optInt("id");
                    TourDAO dao = new TourDAO();
                    Tour t = dao.getTourById(id);

                    if (t != null) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", t.getId());
                        obj.put("name", t.getName());
                        obj.put("description", t.getDescription());
                        obj.put("price", t.getPrice());
                        obj.put("duration_days", t.getDurationDays());
                        obj.put("difficulty_level", t.getDifficultyLevel());
                        obj.put("image_url", t.getImageUrl());

                        response.put("status", "success");
                        response.put("tour", obj);
                    } else {
                        response.put("status", "error");
                        response.put("message", "Không tìm thấy tour");
                    }
                    break;
                }

                // 🗓️ Lấy danh sách lịch trình cho 1 tour
                case "get_schedules_by_tour_id": {
                    int tourId = request.optInt("tour_id");
                    TourDAO dao = new TourDAO();
                    List<TourSchedule> schedules = dao.getSchedulesByTourId(tourId);

                    JSONArray scheduleArray = new JSONArray();
                    for (TourSchedule s : schedules) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", s.getId());
                        obj.put("start_date", s.getStartDate().toString());
                        obj.put("end_date", s.getEndDate().toString());
                        obj.put("available_slots", s.getAvailableSlots());
                        obj.put("booked_slots", s.getBookedSlots());
                        obj.put("price_adjustment", s.getPriceAdjustment());
                        scheduleArray.put(obj);
                    }

                    response.put("status", "success");
                    response.put("schedules", scheduleArray);
                    break;
                }

                default:
                    response.put("status", "error");
                    response.put("message", "Hành động không hợp lệ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Lỗi xử lý: " + e.getMessage());
        }

        return response.toString();
    }
}
