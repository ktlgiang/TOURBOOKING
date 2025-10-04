package org.example.tourbooking.service;

import org.example.tourbooking.dao.TourDAO;
import org.example.tourbooking.model.Tour;
import java.util.List;

public class TourService {
    private TourDAO tourDAO;

    public TourService() {
        this.tourDAO = new TourDAO();
    }

    public List<Tour> getAllTours() {
        return tourDAO.getAllTours();
    }

    public Tour getTourById(int id) {
        return tourDAO.getTourById(id);
    }
}
