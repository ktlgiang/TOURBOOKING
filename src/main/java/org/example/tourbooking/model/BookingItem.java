package org.example.tourbooking.model;

public class BookingItem {
    private String tourName;
    private String startDate;
    private int people;
    private String status;

    public BookingItem(String tourName, String startDate, int people, String status) {
        this.tourName = tourName;
        this.startDate = startDate;
        this.people = people;
        this.status = status;
    }

    public String getTourName() { return tourName; }
    public String getStartDate() { return startDate; }
    public int getPeople() { return people; }
    public String getStatus() { return status; }
}
