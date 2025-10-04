package org.example.tourbooking;

import org.example.tourbooking.dao.TourDAO;
import org.example.tourbooking.model.Tour;

import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        TourDAO dao = new TourDAO();
        List<Tour> tours = dao.getAllTours();

        System.out.println("===== Danh sách Tour =====");
        for (Tour t : tours) {
            System.out.println(t.getId() + " - " + t.getName() + " - " + t.getPrice());
        }
    }
}
