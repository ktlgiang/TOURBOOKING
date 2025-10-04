package org.example.tourbooking.dao;

import org.example.tourbooking.model.Booking;
import org.example.tourbooking.utils.DBConnection;

import java.sql.*;
import java.util.*;

public class BookingDAO {

    // Lấy tất cả booking
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking b = new Booking(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("tour_id"),
                        rs.getInt("number_of_people"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("booking_date")
                );
                list.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm booking
    public boolean insertBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, tour_id, number_of_people, total_price, status, booking_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getTourId());
            ps.setInt(3, booking.getNumberOfPeople());
            ps.setDouble(4, booking.getTotalPrice());
            ps.setString(5, booking.getStatus());
            ps.setTimestamp(6, booking.getBookingDate());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa booking theo ID
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
