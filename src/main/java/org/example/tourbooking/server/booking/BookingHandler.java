package org.example.tourbooking.server.booking;

import org.example.tourbooking.utils.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class BookingHandler {

    public static String handleMessage(String message) {
        try {
            JSONObject req = new JSONObject(message);
            String action = req.optString("action");

            switch (action) {
                case "book_tour":
                    return handleBooking(req);

                case "cancel_booking":
                    return handleCancel(req);

                case "get_user_bookings":
                    return handleGetBookings(req);

                default:
                    return new JSONObject()
                            .put("status", "error")
                            .put("message", "Hành động không hợp lệ")
                            .toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject()
                    .put("status", "error")
                    .put("message", "Lỗi xử lý BookingHandler").toString();
        }
    }

    // ==================== 📦 ĐẶT TOUR ====================
    private static String handleBooking(JSONObject req) {
        int customerId = req.optInt("user_id");     // client gửi user_id
        int tourId = req.optInt("tour_id");
        int scheduleId = req.optInt("schedule_id", 1);
        int numPeople = req.optInt("quantity", 1);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null)
                return error("Không thể kết nối cơ sở dữ liệu.");

            // 🔍 Lấy giá tour
            double price = 0;
            try (PreparedStatement psPrice = conn.prepareStatement("SELECT price FROM tours WHERE id = ?")) {
                psPrice.setInt(1, tourId);
                ResultSet rs = psPrice.executeQuery();
                if (rs.next()) {
                    price = rs.getDouble("price");
                } else {
                    return error("Không tìm thấy tour trong cơ sở dữ liệu!");
                }
            }

            // 💾 Thêm booking (MySQL tự sinh booking_code, status, date,...)
            String insertSql = """
                INSERT INTO bookings (customer_id, tour_id, schedule_id, number_of_people, total_price)
                VALUES (?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, customerId);
                ps.setInt(2, tourId);
                ps.setInt(3, scheduleId);
                ps.setInt(4, numPeople);
                ps.setDouble(5, price * numPeople);
                ps.executeUpdate();
            }

            return success("✅ Đặt tour thành công!");

        } catch (SQLIntegrityConstraintViolationException dup) {
            // 🔁 Lỗi khi trùng unique key (customer_id + schedule_id)
            if (dup.getMessage().contains("uq_customer_schedule")) {
                return error("⚠️ Bạn đã đặt tour này trước đó!");
            }
            dup.printStackTrace();
            return error("Lỗi ràng buộc dữ liệu (Integrity Error).");

        } catch (Exception e) {
            e.printStackTrace();
            return error("❌ Lỗi khi đặt tour.");
        }
    }

    // ==================== ❌ HỦY BOOKING ====================
    private static String handleCancel(JSONObject req) {
        int bookingId = req.optInt("booking_id");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE bookings SET status = 'Đã hủy', cancelled_at = NOW() WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, bookingId);
                int rows = ps.executeUpdate();

                if (rows > 0)
                    return success("✅ Hủy tour thành công!");
                else
                    return error("Không tìm thấy booking để hủy.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return error("❌ Lỗi khi hủy tour.");
        }
    }

    // ==================== 📋 LẤY DANH SÁCH BOOKING ====================
    private static String handleGetBookings(JSONObject req) {
        int customerId = req.optInt("user_id");
        JSONObject response = new JSONObject();
        JSONArray bookings = new JSONArray();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT id, booking_code, tour_id, schedule_id,
                       number_of_people, total_price,
                       booking_date, status
                FROM bookings
                WHERE customer_id = ?
                ORDER BY booking_date DESC
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    JSONObject b = new JSONObject();
                    b.put("id", rs.getInt("id"));
                    b.put("booking_code", rs.getString("booking_code"));
                    b.put("tour_id", rs.getInt("tour_id"));
                    b.put("schedule_id", rs.getInt("schedule_id"));
                    b.put("number_of_people", rs.getInt("number_of_people"));
                    b.put("total_price", rs.getDouble("total_price"));
                    b.put("booking_date", rs.getString("booking_date"));
                    b.put("status", rs.getString("status"));
                    bookings.put(b);
                }
            }

            response.put("status", "success");
            response.put("bookings", bookings);
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return error("❌ Lỗi khi lấy danh sách booking.");
        }
    }

    // ==================== ⚙️ HÀM TIỆN ÍCH ====================
    private static String error(String msg) {
        return new JSONObject().put("status", "error").put("message", msg).toString();
    }

    private static String success(String msg) {
        return new JSONObject().put("status", "success").put("message", msg).toString();
    }
}
