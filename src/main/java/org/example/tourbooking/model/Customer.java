package org.example.tourbooking.model;

public class Customer {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String country;

    public Customer(int id, String fullName, String email, String phone, String city, String country) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.country = country;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getCountry() { return country; }

    @Override
    public String toString() {
        return id + " - " + fullName + " (" + email + ")";
    }
}
