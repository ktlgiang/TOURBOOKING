package org.example.tourbooking.service;

import org.example.tourbooking.dao.BookingDAO;
import org.example.tourbooking.model.Booking;

import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public boolean createBooking(Booking booking) {
        return bookingDAO.insertBooking(booking);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingDAO.deleteBooking(bookingId);
    }
}
