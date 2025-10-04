package org.example.tourbooking;

import org.example.tourbooking.dao.CategoryDAO;
import org.example.tourbooking.model.Category;

import java.util.List;

public class TestCategoryDAO {
    public static void main(String[] args) {
        System.out.println("===== 🧩 TEST CATEGORY DAO =====");

        CategoryDAO dao = new CategoryDAO();

        List<Category> list = dao.getAllCategories();

        System.out.println("📂 Danh sách Category:");
        for (Category c : list) {
            System.out.println(" - " + c.getId() + " | " + c.getName() + " | " + c.getDescription());
        }

        System.out.println("✅ Test CategoryDAO hoàn tất!");
    }
}
