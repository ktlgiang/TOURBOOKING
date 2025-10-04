package org.example.tourbooking.model;

import java.sql.Timestamp;

public class Booking {
    private int id;
    private int customerId;
    private int tourId;
    private int numberOfPeople;
    private double totalPrice;
    private String status;
    private Timestamp bookingDate;

    public Booking(int id, int customerId, int tourId, int numberOfPeople,
                   double totalPrice, String status, Timestamp bookingDate) {
        this.id = id;
        this.customerId = customerId;
        this.tourId = tourId;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getTourId() { return tourId; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public Timestamp getBookingDate() { return bookingDate; }

    @Override
    public String toString() {
        return "Booking #" + id + " (Tour " + tourId + ", Customer " + customerId + ", Status: " + status + ")";
    }
}
