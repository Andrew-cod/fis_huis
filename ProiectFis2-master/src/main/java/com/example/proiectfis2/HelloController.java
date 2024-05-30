package com.example.proiectfis2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.*;

public class HelloController {
    @FXML
    private ListView<Product> productList;

    @FXML
    private ListView<Product> cartList;

    @FXML
    private ListView<Order> orderList;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton, addButton, addPromoButton, removePromoButton, addToCartButton, removeFromCartButton, placeOrderButton, addEmployeeButton, viewEmployeesButton, removeProductButton, changeStatusButton;

    private DatabaseManager databaseManager = new DatabaseManager();
    private Employee currentEmployee;
    private Customer currentCustomer;
    private List<Product> cart = new ArrayList<>();

    @FXML
    public void initialize() {
        //PENTRU A VIZIONA MAI FRUMOS PRODUSELE
        productList.setCellFactory(param -> new ProductCell());
        orderList.setCellFactory(param -> new OrderCell());
        cartList.setCellFactory(param -> new ProductCell());

        //incarcam datele initiale si dam update la listview.
        updateProductListView();
        updateOrderListView();
        updateCartListView();

        //setam  accesul la butoane cand pornim aplicatie. initial sunt toate false adic off.
        setButtonAccess(false, false, false, false, false, false, false, false, false, false,false);
    }

    private void updateProductListView() {
        List<Product> products = databaseManager.getProducts();
        productList.setItems(FXCollections.observableArrayList(products));
    }

    private void updateOrderListView() {
        List<Order> orders = databaseManager.getOrders();
        orderList.setItems(FXCollections.observableArrayList(orders));
    }

    private void updateCartListView() {
        cartList.setItems(FXCollections.observableArrayList(cart));
    }

    private void setButtonAccess(boolean addProduct, boolean addPromo, boolean removePromo, boolean addToCart, boolean removeFromCart, boolean placeOrder, boolean changeStatus, boolean addEmployee, boolean viewEmployees, boolean removeCompleted, boolean removeProduct) {
        addButton.setVisible(addProduct);
        removePromoButton.setVisible(removePromo);
        addToCartButton.setVisible(addToCart);
        removeFromCartButton.setVisible(removeFromCart);
        placeOrderButton.setVisible(placeOrder);
        addEmployeeButton.setVisible(addEmployee);
        viewEmployeesButton.setVisible(viewEmployees);
        removeProductButton.setVisible(removeProduct);
        addPromoButton.setVisible(addPromo);

        //pentru a seta limitele la listview.
        orderList.setVisible(addEmployee || changeStatus || removeCompleted);
    }

    private void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    private void setListViewVisibility(boolean isVisible) {
        orderList.setVisible(isVisible);
    }
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        //resetam userul curent
        currentEmployee = null;
        currentCustomer = null;

        // Autentificare client
        if (databaseManager.authenticateCustomer(username, password)) {
            currentCustomer = databaseManager.getCustomer(username);
            showAlert(Alert.AlertType.INFORMATION,"Log In","Bine ai venit!","Cont client: " + username);
            setButtonAccess(false, false, false, true, true, true, false, false, false, false,false);
        } else if (databaseManager.authenticateEmployee(username, password)) {
            // Autentificare angajat
            currentEmployee = databaseManager.getEmployee(username);
            showAlert(Alert.AlertType.INFORMATION,"Log In","Bine ai venit!","Rolul tau este: " + currentEmployee.getUsername());
            switch (currentEmployee.getRole()) {
                case "admin":
                    setButtonAccess(false, false, false, false, false, false, true, true, true, true,false);
                    break;
                case "seller":
                    setButtonAccess(true, false, false, false, false, false, true, false, false, true,true);
                    break;
                case "user":
                    setButtonAccess(false, false, false, true, true, true, false, false, false, true,false);
                    break;
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Eroare autentificare", "Date invalide", "Email sau parola gresita!");
            setButtonAccess(false, false, false, false, false, false, false, false, false, false,false);
        }
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        if (currentEmployee != null && "seller".equals(currentEmployee.getRole())) {

            ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(Category.TELEFON, Category.values());
            categoryDialog.setTitle("Adaugare produs");
            categoryDialog.setHeaderText("Alegeti categoria din care face parte produsul");
            categoryDialog.setContentText("Categorie:");
            Optional<Category> resultCategory = categoryDialog.showAndWait();
            if (!resultCategory.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Categoria produsul nu a fost selectata", "Va rugam selectati o categorie.");
                return;
            }

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Adaugare produs");
            nameDialog.setHeaderText("Introduceti numele produsului");
            nameDialog.setContentText("Nume:");
            Optional<String> resultName = nameDialog.showAndWait();
            if (!resultName.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Numele produsului nu a fost dat", "Va rugam introduceti un nume pentru produs");
                return;
            }

            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Adaugare produs");
            priceDialog.setHeaderText("Pretul produsului");
            priceDialog.setContentText("Pret:");
            Optional<String> resultPrice = priceDialog.showAndWait();
            if (!resultPrice.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Pretul produsului nu a fost dat", "Va rugam introduceti un pret.");
                return;
            }

            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Adaugare produs");
            descriptionDialog.setHeaderText("Introduceti descrierea produsului");
            descriptionDialog.setContentText("Descriere:");
            Optional<String> resultDescription = descriptionDialog.showAndWait();
            if (!resultDescription.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Descrierea produsului nu a fost scrisa", "Va rugam introduceti o descriere");
                return;
            }

            try {
                Category category = resultCategory.get();
                String name = resultName.get();
                double price = Double.parseDouble(resultPrice.get());
                String description = resultDescription.get();

                Product product = new Product(name, category, price, description, 4, false);
                databaseManager.addProduct(product, currentEmployee); //salvam produsele in json.
                updateProductListView();
                showAlert(Alert.AlertType.INFORMATION, "Adaugare produse", "Produs adaugat", "Product added: " + product.getName());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produse", "Formatul pretului este gresit", "Introduceti un pret valid(ex:1000.0)");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Adaugare produse", "Lipsa permisiuni", "Trebuie sa fiti seller pentru a putea adauga produse!");
        }
    }


    @FXML
    private void handleAddPromotion(ActionEvent event) {
        if (currentEmployee != null && "seller".equals(currentEmployee.getRole())) {
            ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(Category.TELEFON, Category.values());
            categoryDialog.setTitle("Adaugare produs");
            categoryDialog.setHeaderText("Alegeti tipul");
            categoryDialog.setContentText("Categorie:");
            Optional<Category> resultCategory = categoryDialog.showAndWait();
            if (!resultCategory.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Categoria produsul nu a fost selectata", "Va rugam selectati o categorie.");
                return;
            }

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Adaugare produs");
            nameDialog.setHeaderText("Introduceti numele produsului");
            nameDialog.setContentText("Nume:");
            Optional<String> resultName = nameDialog.showAndWait();
            if (!resultName.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Numele produsului nu a fost dat", "Va rugam introduceti un nume pentru produs");
                return;
            }

            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Adaugare produs");
            priceDialog.setHeaderText("Pretul produsului");
            priceDialog.setContentText("Pret:");
            Optional<String> resultPrice = priceDialog.showAndWait();
            if (!resultPrice.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Pretul produsului nu a fost dat", "Va rugam introduceti un pret.");
                return;
            }

            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Adaugare produs");
            descriptionDialog.setHeaderText("Introduceti descrierea produsului");
            descriptionDialog.setContentText("Descriere:");
            Optional<String> resultDescription = descriptionDialog.showAndWait();
            if (!resultDescription.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Descrierea produsului nu a fost scrisa", "Va rugam introduceti o descriere");
                return;
            }

            ChoiceDialog<String> nogociabilDialog = new ChoiceDialog<>("Selecteaza","Negociabil","Pret Fix");
            categoryDialog.setTitle("Adaugare negociabil");
            categoryDialog.setHeaderText("Alegeti tipul");
            categoryDialog.setContentText("Tipul:");
            Optional<String> resultNogociabilDialog = nogociabilDialog.showAndWait();
            if (!resultCategory.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Categoria produsul nu a fost selectata", "Va rugam selectati o categorie.");
                return;
            }

            try {
                Category category = resultCategory.get();
                String name = resultName.get();
                double price = Double.parseDouble(resultPrice.get());
                String description = resultDescription.get();
                boolean negociabil = resultNogociabilDialog.isPresent();

                Product product = new Product(name, category, price, description, 4, negociabil);
                databaseManager.addProduct(product, currentEmployee); //salvam produsele in json.
                updateProductListView();
                showAlert(Alert.AlertType.INFORMATION, "Adaugare produse", "Produs adaugat", "Product added: " + product.getName());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Adaugare produse", "Formatul pretului este gresit", "Introduceti un pret valid(ex:1000.0)");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Adaugare produse", "Lipsa permisiuni", "Trebuie sa fiti seller pentru a putea adauga produse!");
        }
    }

    @FXML
    private void handleRemovePromotion(ActionEvent event) {
        if (currentEmployee != null && "seller".equals(currentEmployee.getRole())) {
            List<Promotion> promotions = databaseManager.getPromotions();
            ChoiceDialog<Promotion> promoDialog = new ChoiceDialog<>(promotions.get(0), promotions);
            promoDialog.setTitle("Stergere promotie");
            promoDialog.setHeaderText("Selectati promotia pe care doriti sa o stergeti");
            promoDialog.setContentText("Promotii:");

            Optional<Promotion> resultPromotion = promoDialog.showAndWait();
            if (!resultPromotion.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Stergere promotii", "Nici o promotie selectata", "Va rugam selectati o promotie");
                return;
            }

            Promotion selectedPromotion = resultPromotion.get();
            databaseManager.removePromotion(selectedPromotion, currentEmployee);
            updateProductListView();
            showAlert(Alert.AlertType.INFORMATION, "Stergere promotii", "Promotie stearsa", "Promotie stearsa: " + selectedPromotion.getName());
        } else {
            showAlert(Alert.AlertType.ERROR, "Stergere promotii", "Lipsa permisiuni", "Trebuie sa fiti seller pentru a putea adauga/sterge promotii");
        }
    }

    //asta e a mea
    @FXML
    private void handleRemoveProduct(ActionEvent event) {
        Product selectedProduct = productList.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            List<Product> Products = databaseManager.getProducts();

            databaseManager.removeProduct(selectedProduct,  currentEmployee);
            updateProductListView();

            showAlert(Alert.AlertType.INFORMATION, "Adauga in cos", "Produs adaugat in cos", "Produs adaugat: ");
        } else {
            showAlert(Alert.AlertType.ERROR, "Adauga in cos", "Nici un produs selectat", "Va rugam selectati produsul pe care doriti sa il adaugati.");
        }
    }

    @FXML
    private void handleAddToCart(ActionEvent event) {
        Product selectedProduct = productList.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                if (selectedProduct.isPart()) {
                    TextInputDialog priceDialog = new TextInputDialog();
                    priceDialog.setTitle("Propune un pret");
                    priceDialog.setHeaderText("Pret Propus:");
                    priceDialog.setContentText("Pret:");
                    Optional<String> resultPrice = priceDialog.showAndWait();
                    if (!resultPrice.isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Adaugare produs", "Pretul produsului nu a fost dat", "Va rugam introduceti un pret.");
                        return;
                    }
                    if (selectedProduct.getPrice() < Integer.parseInt(valueOf(resultPrice.get()))) {

                        Product productToAdd = new Product(
                                selectedProduct.getName(),
                                selectedProduct.getCategory(),
                                Integer.parseInt(valueOf(resultPrice.get())),
                                selectedProduct.getDescription(),
                                selectedProduct.getRating(),
                                selectedProduct.isPart()
                        );

                        cart.add(productToAdd);
                        updateCartListView();
                        showAlert(Alert.AlertType.INFORMATION, "Adauga in cos", "Produs adaugat in cos", "Produs adaugat: " + productToAdd.getName());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Pret Mic", "Trebuie sa introduceti un pret mai mic", "Pentru a cumpara acest produs trebuie sa propuneti un pret mai mare decat pretul minim dorit de vanzator");

                    }

                } else {
                    Product productToAdd = new Product(
                            selectedProduct.getName(),
                            selectedProduct.getCategory(),
                            selectedProduct.getPrice(),
                            selectedProduct.getDescription(),
                            selectedProduct.getRating(),
                            selectedProduct.isPart()
                    );

                    cart.add(productToAdd);
                    updateCartListView();
                    showAlert(Alert.AlertType.INFORMATION, "Adauga in cos", "Produs adaugat in cos", "Produs adaugat: " + productToAdd.getName());

                }
            }else {
                showAlert(Alert.AlertType.ERROR, "Adauga in cos", "Nici un produs selectat", "Va rugam selectati produsul pe care doriti sa il adaugati.");
            }



    }

    @FXML
    private void handleRemoveFromCart(ActionEvent event) {
        Product selectedProduct = cartList.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            cart.remove(selectedProduct);
            updateCartListView();
            showAlert(Alert.AlertType.INFORMATION, "Stergere din cos", "Produs sters din cos", "Produs sters: " + selectedProduct.getName());
        } else {
            showAlert(Alert.AlertType.ERROR, "Stergere din cos", "Nici un produs selectat", "Va rugam selectati produsul pe care doriti sa-l stergeti.");
        }
    }

    @FXML
    private void handlePlaceOrder(ActionEvent event) {
        if (currentCustomer != null) {
            if (!cart.isEmpty()) {
                double totalPrice = 0;
                boolean hasPreAssembledParts = false;

                for (Product product : cart) {
                    totalPrice += product.getPrice();
                    if (product.getCategory() == Category.CEAS) {
                        hasPreAssembledParts = true;
                    }
                }

                if (hasPreAssembledParts) {
                    totalPrice += 100; //adaugam o taxa suplimentara de 100 ron pentr produse preasamblate.
                }

                List<Product> orderProducts = new ArrayList<>(cart);
                cart.clear();
                updateCartListView();
                Order order = new Order(currentCustomer, orderProducts, "Pending");
                databaseManager.placeOrder(order); // This method saves the orders list to JSON
                updateOrderListView();
                showAlert(Alert.AlertType.INFORMATION, "Plasati comanda", "Comanda plasata", "Comanda plasata. Pret total: " + totalPrice);
            } else {
                showAlert(Alert.AlertType.ERROR, "Plasati comanda", "Cosul este gol", "Va rugam adaugati produse in cos pentru a putea plasa o comanda");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Plasati comanda", "Lipsa permisiuni", "Trebuie sa fiti logat ca si user pentru a trimite o comanda");
        }
    }

    @FXML
    private void handleOrderStatusChange(ActionEvent event) {
        Order selectedOrder = orderList.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            ChoiceDialog<String> statusDialog = new ChoiceDialog<>("Pending", "Pending", "Processing", "Completed", "Cancelled");
            statusDialog.setTitle("Schimba status comanda");
            statusDialog.setHeaderText("Selectati noul status al comenzii");
            statusDialog.setContentText("Status:");
            Optional<String> resultStatus = statusDialog.showAndWait();
            if (!resultStatus.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Schimba status comanda", "Nici un status nu a fost selectat", "Va rugam selectati un nou status pentru comanda");
                return;
            }

            String newStatus = resultStatus.get();
            selectedOrder.setStatus(newStatus);
            updateOrderListView();
            showAlert(Alert.AlertType.INFORMATION, "Schimba status comanda", "Status comanda schimbat", "Statusul comenzii a fost schimbat: " + newStatus);
        } else {
            showAlert(Alert.AlertType.ERROR, "Schimba status comanda", "Nici o comanda selectata", "Va rugam selectati o comanda pentru a schimba statusul.");
        }
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        if (currentEmployee != null && "admin".equals(currentEmployee.getRole())) {
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Adaugarea seller");
            usernameDialog.setHeaderText("Nume seller");
            usernameDialog.setContentText("Username:");
            Optional<String> resultUsername = usernameDialog.showAndWait();
            if (!resultUsername.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare seller", "Nume seller invalid", "Va rugam introduceti un nume pentru noul seller.");
                return;
            }

            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Adaugare seller");
            passwordDialog.setHeaderText("Parola seller");
            passwordDialog.setContentText("Parola:");
            Optional<String> resultPassword = passwordDialog.showAndWait();
            if (!resultPassword.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare seller", "Parola seller invalida", "Va rugam introduceti o parola pentru noul seller.");
                return;
            }

            ChoiceDialog<String> roleDialog = new ChoiceDialog<>("user", "user", "seller");
            roleDialog.setTitle("Adaugare seller");
            roleDialog.setHeaderText("Rol seller");
            roleDialog.setContentText("Rol:");
            Optional<String> resultRole = roleDialog.showAndWait();
            if (!resultRole.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Adaugare seller", "Rol seller invalid", "Va rugam selectati un rol pentru seller.");
                return;
            }

            String username = resultUsername.get();
            String password = resultPassword.get();
            String role = resultRole.get();

            Employee employee = new Employee(username, password, role);
            databaseManager.addEmployee(employee); // This method saves the employees list to JSON
            showAlert(Alert.AlertType.INFORMATION, "Adaugare seller", "Angajat seller", "Angajat seller: " + username);
        } else {
            showAlert(Alert.AlertType.ERROR, "Adaugare seller", "Lipsa permisiuni", "Trebuie sa fiti logat ca admin pentru a adauga noi vanzatori.");
        }
    }

    @FXML
    private void handleRemoveCompleted(ActionEvent event) {
        if (currentEmployee != null) {
            if ("admin".equals(currentEmployee.getRole()) || "seller".equals(currentEmployee.getRole()) || "user".equals(currentEmployee.getRole())) {
                ObservableList<Order> items = orderList.getItems();

                //stergem comenziile terminate sau care au fost anulate
                List<Order> completedOrCanceledOrders = items.stream()
                        .filter(order -> order.getStatus().equals("Completed") || order.getStatus().equals("Cancelled"))
                        .collect(Collectors.toList());
                items.removeAll(completedOrCanceledOrders);

                //updatam lista
                orderList.setItems(items);

                //updatam in baza de date.
                databaseManager.updateOrders(items);

                showAlert(Alert.AlertType.INFORMATION, "Comenzi sterse", "Comenziile terminate sau anulate au fost sterse", "Stergere cu succes");
            } else {
                showAlert(Alert.AlertType.ERROR, "Comenzi sterse", "Fara permisiune", "Doar angajatii pot sterge listele cu comenzi");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Comenzi sterse", "Permisiuni invalide", "Doar anagajtii pot sterge comenzi din aceasta lista!");
        }
    }

    @FXML
    private void handleViewEmployees(ActionEvent event) {
        if (currentEmployee != null && "admin".equals(currentEmployee.getRole())) {
            List<Employee> employees = databaseManager.getEmployees();
            StringBuilder employeeInfo = new StringBuilder("Selleri:\n");
            for (Employee employee : employees) {
                if(employee.getRole().equals("seller"))
                    employeeInfo.append("Username: ").append(employee.getUsername()).append(", Rol: ").append(employee.getRole()).append("\n");
            }
            showAlert(Alert.AlertType.INFORMATION, "Vizualizare selleri", "Informatii selleri", employeeInfo.toString());
        } else {
            showAlert(Alert.AlertType.ERROR, "Vizualizare selleri", "Permisiuni invalide", "Doar adminii pot sa vada lista de selleri.");
        }
    }

    @FXML
    private void handleServiceRequest(ActionEvent event) {
        if (currentCustomer != null) {
            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Cerere service");
            descriptionDialog.setHeaderText("Introduceti o descriere a problemei");
            descriptionDialog.setContentText("Desciere:");
            Optional<String> resultDescription = descriptionDialog.showAndWait();
            if (!resultDescription.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Cerere service", "Descrierea invalida", "Va rugam introduceti o scurta descriere a problemei.");
                return;
            }

            TextInputDialog dateDialog = new TextInputDialog();
            dateDialog.setTitle("Cerere service");
            dateDialog.setHeaderText("Data in care doriti service-ul:");
            dateDialog.setContentText("Data:");
            Optional<String> resultDate = dateDialog.showAndWait();
            if (!resultDate.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Cerere service", "Data service invalida", "Va rugam introduceti o data pentru preluare in service.");
                return;
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Cerere service", "Permisiuni invalide", "Trebuie sa aveti cont de client pentru trimiterea unei cerere service!");
        }
    }
}