package org.example.tourbooking.model;

// Tạo Admin.java
public class Admin {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String role; // Ví dụ: "Super Admin", "Admin", "Staff"

    public Admin(int id, String fullName, String email, String phone, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
}