package com.example.proiectfis2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBmanager {
    private List<Customer> customers;
    private List<Employee> employees;
    private List<Product> products;
    private List<Order> orders;
    private List<Negociere> negocieres;

    private static final String CUSTOMERS_FILE = "src/main/resources/com/example/proiectfis2/customers.json";
    private static final String EMPLOYEES_FILE = "src/main/resources/com/example/proiectfis2/employees.json";
    private static final String PRODUCTS_FILE = "src/main/resources/com/example/proiectfis2/products.json";
    private static final String ORDERS_FILE = "src/main/resources/com/example/proiectfis2/orders.json";
    private static final String PROMOTIONS_FILE = "src/main/resources/com/example/proiectfis2/promotions.json";

    private Gson gson = new Gson();

    public DBmanager() {
        this.customers = loadCustomers();
        this.employees = loadEmployees();
        this.products = loadProducts();
        this.orders = loadOrders();
        this.negocieres = loadPromotions();
    }

    // Load methods...

    private List<Customer> loadCustomers() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/example/proiectfis2/customers.json"))) {
            Type customerListType = new TypeToken<ArrayList<Customer>>(){}.getType();
            return gson.fromJson(reader, customerListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Employee> loadEmployees() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/example/proiectfis2/employees.json"))) {
            Type employeeListType = new TypeToken<ArrayList<Employee>>(){}.getType();
            return gson.fromJson(reader, employeeListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Product> loadProducts() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/example/proiectfis2/products.json"))) {
            Type productListType = new TypeToken<ArrayList<Product>>(){}.getType();
            return gson.fromJson(reader, productListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Order> loadOrders() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/example/proiectfis2/orders.json"))) {
            Type orderListType = new TypeToken<ArrayList<Order>>(){}.getType();
            return gson.fromJson(reader, orderListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Negociere> loadPromotions() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/example/proiectfis2/promotions.json"))) {
            Type promotionListType = new TypeToken<ArrayList<Negociere>>() {}.getType();
            return gson.fromJson(reader, promotionListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private void saveCustomers() {
        try (FileWriter writer = new FileWriter(CUSTOMERS_FILE)) {
            gson.toJson(customers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEmployees() {
        try (FileWriter writer = new FileWriter(EMPLOYEES_FILE)) {
            gson.toJson(employees, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveProducts() {
        try (FileWriter writer = new FileWriter(PRODUCTS_FILE)) {
            gson.toJson(products, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveOrders() {
        try (FileWriter writer = new FileWriter(ORDERS_FILE)) {
            gson.toJson(orders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePromotions() {
        try (FileWriter writer = new FileWriter(PROMOTIONS_FILE)) {
            gson.toJson(negocieres, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomers();
    }

    public Customer getCustomer(String username) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(username)) {
                return customer;
            }
        }
        return null;
    }

    public boolean authenticateCustomer(String username, String password) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        saveEmployees();
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public boolean authenticateEmployee(String username, String password) {
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username) && employee.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Employee getEmployee(String username) {
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username)) {
                return employee;
            }
        }
        return null;
    }

    public void addProduct(Product product, Employee employee) {
        if (employee != null && employee.getRole().equals("seller")) {
            products.add(product);
            saveProducts();
        } else {
            System.out.println("Doar sellerii pot adăuga produse.");
        }
    }

    public void removeProduct(Product product, Employee employee){
        if (employee != null && employee.getRole().equals("seller")) {
            products.remove(product);
            saveProducts();
        } else {
            System.out.println("Doar sellerii pot sterge produse.");
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public void placeOrder(Order order) {
        if (order.getCustomer() == null || order.getProducts() == null || order.getProducts().isEmpty()) {
            System.out.println("Nu se pot plasa comenzi daca nu esti logat sau daca nu ati adaugat produse in cos.");

            return;
        }
        orders.add(order);
        saveOrders();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addPromotion(Negociere negociere, Employee employee) {
        if (employee != null && employee.getRole().equals("seller")) {
            negocieres.add(negociere);
            for (Product product : negociere.getProducts()) {
                if (!products.contains(product)) {
                    products.add(product);
                }
            }
            savePromotions();
            saveProducts();
        } else {
            System.out.println("Doar managerii pot sterge negocieri.");
        }
    }

    public void removePromotion(Negociere negociere, Employee employee) {
        if (employee != null && employee.getRole().equals("seller")) {
            negocieres.remove(negociere);
            savePromotions();
        } else {
            System.out.println("Doar sellerii pot șterge negocieri.");
        }
    }

    public double getDiscountForProduct(Product product) {
        for (Negociere negociere : negocieres) {
            if (negociere.getProducts().contains(product)) {
                return negociere.getDiscountPercent();
            }
        }
        return 0;
    }
    public void updateOrders(List<Order> orders) {
        this.orders = orders;
        saveOrders();
    }
    public List<Negociere> getPromotions() {
        return negocieres;
    }

}
