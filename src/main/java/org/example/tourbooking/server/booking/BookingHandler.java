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
        int customerId = req.optInt("user_id");
        int tourId = req.optInt("tour_id");
        int scheduleId = req.optInt("schedule_id", 1);
        int numPeople = req.optInt("quantity", 1);

        // 🗓️ Lấy ngày từ client gửi lên (nếu có)
        String startDateStr = req.optString("start_date", null);
        String endDateStr = req.optString("end_date", null);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null)
                return error("Không thể kết nối cơ sở dữ liệu.");

            double price = 0;
            Date startDate = null;
            Date endDate = null;

            // ✅ Nếu client có gửi ngày thì dùng
            if (startDateStr != null && endDateStr != null) {
                startDate = Date.valueOf(startDateStr);
                endDate = Date.valueOf(endDateStr);
            } else {
                // 🧭 Nếu client không gửi → lấy từ bảng tour_schedules
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT t.price, s.start_date, s.end_date " +
                                "FROM tours t JOIN tour_schedules s ON t.id = s.tour_id " +
                                "WHERE t.id = ? AND s.id = ?")) {
                    ps.setInt(1, tourId);
                    ps.setInt(2, scheduleId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        price = rs.getDouble("price");
                        startDate = rs.getDate("start_date");
                        endDate = rs.getDate("end_date");
                    } else {
                        return error("Không tìm thấy lịch tour trong cơ sở dữ liệu!");
                    }
                }
            }

            // ====================== ⚠️ KIỂM TRA NGÀY ======================
            Date today = new Date(System.currentTimeMillis());

            if (startDate.before(today)) {
                return error("⛔ Ngày khởi hành không được ở trong quá khứ!");
            }

            if (endDate.before(startDate)) {
                return error("⛔ Ngày kết thúc phải sau ngày khởi hành!");
            }

            // ====================== ⚠️ KIỂM TRA TRÙNG LỊCH ======================
            String checkSql = """
            SELECT id 
            FROM bookings
            WHERE customer_id = ?
              AND status != 'Đã hủy'
              AND (
                    (start_date <= ? AND end_date >= ?)
                 OR (start_date <= ? AND end_date >= ?)
                 OR (? <= start_date AND ? >= end_date)
              )
        """;

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, customerId);
                psCheck.setDate(2, startDate);
                psCheck.setDate(3, startDate);
                psCheck.setDate(4, endDate);
                psCheck.setDate(5, endDate);
                psCheck.setDate(6, startDate);
                psCheck.setDate(7, endDate);
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    return error("⚠️ Bạn đã có tour khác trong khoảng thời gian này!");
                }
            }

            // ====================== 💾 THÊM BOOKING ======================
            String insertSql = """
            INSERT INTO bookings 
            (customer_id, tour_id, schedule_id, number_of_people, total_price, start_date, end_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setInt(2, tourId);
                ps.setInt(3, scheduleId);
                ps.setInt(4, numPeople);

                // ⚙️ Lấy giá nếu chưa có
                if (price == 0) {
                    try (PreparedStatement psPrice = conn.prepareStatement("SELECT price FROM tours WHERE id=?")) {
                        psPrice.setInt(1, tourId);
                        ResultSet rs = psPrice.executeQuery();
                        if (rs.next()) price = rs.getDouble("price");
                    }
                }

                ps.setDouble(5, price * numPeople);
                ps.setDate(6, startDate);
                ps.setDate(7, endDate);
                ps.executeUpdate();

                // ✅ Lấy ID booking vừa tạo
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int bookingId = rs.getInt(1);
                    System.out.println("✅ [BookingServer] Đặt tour thành công ID=" + bookingId);

// 🟢 Gửi sang peer để đồng bộ
                    try {
                        JSONObject bookingData = new JSONObject()
                                .put("id", bookingId)
                                .put("customer_id", customerId)
                                .put("tour_id", tourId)
                                .put("schedule_id", scheduleId)
                                .put("number_of_people", numPeople)
                                .put("total_price", price * numPeople)
                                .put("start_date", startDate.toString())
                                .put("end_date", endDate.toString())
                                .put("status", "Đã đặt");

                        org.example.tourbooking.p2p.PeerSyncManager.broadcastNewBooking(bookingData);
                    } catch (Exception ex) {
                        System.err.println("⚠️ Không thể gửi đồng bộ sang peer: " + ex.getMessage());
                    }

                }
            }

            return success("✅ Đặt tour thành công!");

        } catch (SQLIntegrityConstraintViolationException dup) {
            return error("Lỗi ràng buộc dữ liệu (Integrity Error).");
        } catch (Exception e) {
            e.printStackTrace();
            return error("❌ Lỗi khi đặt tour: " + e.getMessage());
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
                SELECT id, booking_code, tour_id, schedule_id, number_of_people, total_price,
                       booking_date, start_date, end_date, status
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
                    b.put("start_date", rs.getString("start_date"));
                    b.put("end_date", rs.getString("end_date"));
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
