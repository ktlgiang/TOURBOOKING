package org.example.tourbooking.model;

public class Destination {
    private int id;
    private String name;
    private String country;
    private String city;
    private String description;
    private String imageUrl;

    public Destination(int id, String name, String country, String city, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.city = city;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    @Override
    public String toString() {
        return name + " - " + city + ", " + country;
    }
}
