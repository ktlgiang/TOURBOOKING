package org.example.tourbooking;

import org.example.tourbooking.dao.DestinationDAO;
import org.example.tourbooking.model.Destination;
import java.util.List;

public class TestDestinationDAO {
    public static void main(String[] args) {
        System.out.println("===== 📍 TEST DESTINATION DAO =====");

        DestinationDAO dao = new DestinationDAO();
        List<Destination> list = dao.getAllDestinations();

        System.out.println("📂 Danh sách Destination:");
        for (Destination d : list) {
            System.out.println(" - " + d.getId() + " | " + d.getName() + " | " + d.getCountry());
        }

        System.out.println("✅ Test DestinationDAO hoàn tất!");
    }
}
