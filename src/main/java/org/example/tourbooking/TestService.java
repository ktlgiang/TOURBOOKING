package org.example.tourbooking;

import org.example.tourbooking.model.Tour;
import org.example.tourbooking.service.TourService;

import java.util.List;

public class TestService {
    public static void main(String[] args) {
        System.out.println("✅ Kết nối thành công MySQL!");

        TourService tourService = new TourService();

        System.out.println("===== Danh sách Tour =====");
        for (Tour t : tourService.getAllTours()) {
            System.out.println(t);
        }

        System.out.println("===== Tour ID=1 =====");
        Tour tour = tourService.getTourById(1);
        System.out.println(tour);
    }
}

