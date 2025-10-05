package org.example.tourbooking.server.booking;

import org.json.JSONObject;
import org.example.tourbooking.dao.BookingDAO;
import org.example.tourbooking.model.Booking;

import java.sql.Timestamp;
import java.util.List;

public class BookingHandler {
    public static String handleMessage(String message) {
        JSONObject request = new JSONObject(message);
        String action = request.optString("action");

        JSONObject response = new JSONObject();

        switch (action) {
            case "bookTour": {
                int customerId = request.getInt("customerId");
                int tourId = request.getInt("tourId");

                Booking booking = new Booking(
                        0, // id auto increment
                        customerId,
                        tourId,
                        1,                        // mặc định 1 người
                        0.0,                      // giá có thể tính sau
                        "pending",                // trạng thái mặc định
                        new Timestamp(System.currentTimeMillis())
                );

                BookingDAO dao = new BookingDAO();
                boolean ok = dao.addBooking(booking);

                if (ok) {
                    response.put("status", "success");
                    response.put("message", "Đặt tour thành công");
                } else {
                    response.put("status", "error");
                    response.put("message", "Tour bị trùng lịch hoặc đặt thất bại");
                }
                break;
            }

            case "getBookings": {
                int customerId = request.getInt("customerId");
                BookingDAO dao = new BookingDAO();
                List<Booking> bookings = dao.getBookingByCustomer(customerId);

                response.put("status", "success");
                response.put("bookings", bookings.toString());
                // 👉 bạn có thể convert sang JSONArray nếu muốn JSON đẹp hơn
                break;
            }

            default:
                response.put("status", "error");
                response.put("message", "Yêu cầu không hợp lệ");
        }

        return response.toString();
    }
}
