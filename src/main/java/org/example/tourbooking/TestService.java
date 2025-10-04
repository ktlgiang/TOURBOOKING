package org.example.tourbooking;

import org.example.tourbooking.model.Tour;
import org.example.tourbooking.service.TourService;

import java.util.List;

public class TestService {
    public static void main(String[] args) {
        TourService tourService = new TourService();

        // Lấy tất cả tours
        List<Tour> tours = tourService.getAllTours();
        System.out.println("===== Danh sách Tour =====");
        for (Tour t : tours) {
            System.out.println(t);
        }

        // Lấy 1 tour theo ID
        Tour tour = tourService.getTourById(1);
        System.out.println("===== Tour ID=1 =====");
        System.out.println(tour);
    }
}
