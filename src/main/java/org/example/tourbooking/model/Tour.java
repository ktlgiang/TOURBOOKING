package org.example.tourbooking.model;

import java.sql.Timestamp;

public class Tour {
    private int id;
    private String name;
    private String description;
    private String shortDescription;
    private int categoryId;
    private int destinationId;
    private double price;
    private int durationDays;
    private int maxParticipants;
    private int minParticipants;
    private String difficultyLevel;
    private String imageUrl;
    private boolean featured;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public Tour(int id, String name, String description, String shortDescription,
                int categoryId, int destinationId, double price, int durationDays,
                int maxParticipants, int minParticipants, String difficultyLevel,
                String imageUrl, boolean featured, String status,
                Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.categoryId = categoryId;
        this.destinationId = destinationId;
        this.price = price;
        this.durationDays = durationDays;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.difficultyLevel = difficultyLevel;
        this.imageUrl = imageUrl;
        this.featured = featured;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters và Setters (viết đầy đủ)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFeatured() {
        return featured;
    }

    public String getStatus() {
        return status;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // ...
    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", duration=" + durationDays + " days" +
                ", status='" + status + '\'' +
                '}';
    }

}
