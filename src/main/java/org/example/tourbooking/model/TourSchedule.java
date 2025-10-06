package org.example.tourbooking.model;

import java.sql.Date;

public class TourSchedule {
    private int id;
    private int tourId;
    private Date startDate;
    private Date endDate;
    private int availableSlots;
    private int bookedSlots;
    private double priceAdjustment;
    private String status;

    public TourSchedule(int id, int tourId, Date startDate, Date endDate, int availableSlots,
                        int bookedSlots, double priceAdjustment, String status) {
        this.id = id;
        this.tourId = tourId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableSlots = availableSlots;
        this.bookedSlots = bookedSlots;
        this.priceAdjustment = priceAdjustment;
        this.status = status;
    }

    public int getId() { return id; }
    public int getTourId() { return tourId; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public int getAvailableSlots() { return availableSlots; }
    public int getBookedSlots() { return bookedSlots; }
    public double getPriceAdjustment() { return priceAdjustment; }
    public String getStatus() { return status; }
}
