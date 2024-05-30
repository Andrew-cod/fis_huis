package com.example.proiectfis2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class Teste {
    private DatabaseManager databaseManager;
    private Employee Employee;
    private Product validProduct;
    private Product anotherProduct;
    private Customer validCustomer;
    private Order validOrder;

    @BeforeEach
    void setUp() {
        databaseManager = new DatabaseManager();

        Employee = new Employee("User", "password", "user");
        validProduct = new Product("Test produs", Category.TELEFON, 1000.0, "Test descriere", 5, false);
        anotherProduct = new Product("Alt produs", Category.LAPTOP, 1500.0, "Alta descriere", 3, false);
        validCustomer = new Customer("TestClient", "password123", "Test Client ", "Test@example.com");
        validOrder = new Order(validCustomer, Collections.singletonList(validProduct), "Pending");
    }



    @Test
    void testAddProductWithUser() {
        System.out.println("Testul: testAddProductWithUser a inceput");
        int initialSize = databaseManager.getProducts().size();
        databaseManager.addProduct(validProduct, Employee);
        assertEquals(initialSize, databaseManager.getProducts().size());
        assertFalse(databaseManager.getProducts().contains(validProduct));
    }

    @Test
    void testAddProductWithoutSeller() {
        System.out.println("Testul: testAddProductWithNullEmployee a inceput");
        int initialSize = databaseManager.getProducts().size();
        databaseManager.addProduct(validProduct, null);
        assertEquals(initialSize, databaseManager.getProducts().size());
        assertFalse(databaseManager.getProducts().contains(validProduct));
    }

    @Test
    void testPlaceValidOrder() {
        System.out.println("Testul: testPlaceValidOrder a inceput");
        int initialSize = databaseManager.getOrders().size();
        databaseManager.placeOrder(validOrder);
        assertEquals(initialSize + 1, databaseManager.getOrders().size());
        assertTrue(databaseManager.getOrders().contains(validOrder));
    }

    @Test
    void testPlaceOrderWithoutCustomer() {
        System.out.println("Testul: testPlaceOrderWithoutCustomer a inceput");
        Order invalidOrder = new Order(null, Collections.singletonList(validProduct), "Pending");
        int initialSize = databaseManager.getOrders().size();
        databaseManager.placeOrder(invalidOrder);
        assertEquals(initialSize, databaseManager.getOrders().size());
        assertFalse(databaseManager.getOrders().contains(invalidOrder));
    }
    @Test
    void testPlaceOrderWithoutAnyProductInList() {
        System.out.println("Testul: testPlaceOrderWithoutAnyProductInList a inceput");
        Order invalidOrder = new Order(validCustomer, Collections.emptyList(), "Pending");
        int initialSize = databaseManager.getOrders().size();
        databaseManager.placeOrder(invalidOrder);
        assertEquals(initialSize, databaseManager.getOrders().size());
        assertFalse(databaseManager.getOrders().contains(invalidOrder));
    }


    @Test
    void testAddPromotionWithNonManager() {
        System.out.println("Testul: testAddPromotionWithNonManager a inceput");
        Employee nonManagerEmployee = new Employee("nonManagerUser", "password", "junior");
        Promotion promotion = new Promotion("Test Promotion", Collections.singletonList(validProduct), 10.0);
        int initialSize = databaseManager.getPromotions().size();
        databaseManager.addPromotion(promotion, nonManagerEmployee);
        assertEquals(initialSize, databaseManager.getPromotions().size());
        assertFalse(databaseManager.getPromotions().contains(promotion));
    }
}
