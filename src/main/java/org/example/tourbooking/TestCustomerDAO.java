package org.example.tourbooking;

import org.example.tourbooking.dao.CustomerDAO;
import org.example.tourbooking.model.Customer;
import java.util.List;

public class TestCustomerDAO {
    public static void main(String[] args) {
        System.out.println("===== 👤 TEST CUSTOMER DAO =====");

        CustomerDAO dao = new CustomerDAO();
        List<Customer> list = dao.getAllCustomers();

        System.out.println("📂 Danh sách Customer:");
        for (Customer c : list) {
            System.out.println(" - " + c.getId() + " | " + c.getFullName() + " | " + c.getEmail());
        }

        System.out.println("✅ Test CustomerDAO hoàn tất!");
    }
}
