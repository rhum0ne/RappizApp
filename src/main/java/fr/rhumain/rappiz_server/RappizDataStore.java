package fr.rhumain.rappiz_server;

import fr.rhumain.structs.Format;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Livreur;
import fr.rhumain.structs.Order;
import fr.rhumain.structs.Pizza;
import fr.rhumain.structs.Receipt;
import fr.rhumain.structs.User;
import fr.rhumain.structs.Vehicule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RappizDataStore {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<Pizza> pizzas = new ArrayList<>();
    private final List<Format> formats = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Livreur> livreurs = new ArrayList<>();
    private final List<Vehicule> vehicules = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Receipt> receipts = new ArrayList<>();
    private final Map<Integer, Integer> customerBalances = new HashMap<>();

    private int nextOrderId = 1005;
    private int nextReceiptId = 5005;

    public RappizDataStore() {
        seedData();
    }

    public synchronized List<Pizza> getPizzas() {
        return List.copyOf(pizzas);
    }

    public synchronized List<Format> getFormats() {
        return List.copyOf(formats);
    }

    public synchronized List<User> getUsers() {
        return List.copyOf(users);
    }

    public synchronized List<Vehicule> getVehicules() {
        return List.copyOf(vehicules);
    }

    public synchronized List<Order> getOrders() {
        return List.copyOf(orders);
    }

    public synchronized List<Receipt> getReceipts() {
        return List.copyOf(receipts);
    }

    public synchronized Optional<User> findUserById(int idUser) {
        return users.stream()
                .filter(user -> user.id() == idUser)
                .findFirst();
    }

    public synchronized Optional<User> authenticateUser(String email, String password) {
        return users.stream()
                .filter(user -> user.email().equalsIgnoreCase(email.trim()))
                .filter(user -> user.password().equals(password))
                .findFirst();
    }

    public synchronized Order createOrder(int idUser, Pizza pizza, Format format) {
        int price = calculatePrice(pizza, format);
        if (getCustomerBalance(idUser) < price) {
            return null;
        }

        Order order = new Order(
                nextOrderId++,
                idUser,
                pizza,
                format,
                now(),
                null,
                price,
                null,
                null
        );
        orders.add(0, order);
        receipts.add(new Receipt(nextReceiptId++, order.id(), order.price(), idUser));
        customerBalances.put(idUser, getCustomerBalance(idUser) - price);
        return order;
    }

    public synchronized boolean markOrderDelivered(int orderId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.id() == orderId && order.timeStampLivraison() == null) {
                Livreur livreur = livreurs.get(i % livreurs.size());
                orders.set(i, new Order(
                        order.id(),
                        order.idUser(),
                        order.pizza(),
                        order.format(),
                        order.timeStamp(),
                        now(),
                        order.price(),
                        livreur,
                        livreur.vehicule()
                ));
                return true;
            }
        }
        return false;
    }

    public int calculatePrice(Pizza pizza, Format format) {
        return (pizza.price() * format.pricePercetage()) / 100;
    }

    public synchronized int getCustomerBalance(int idUser) {
        return customerBalances.getOrDefault(idUser, 0);
    }

    public synchronized int getBoughtPizzaCount(int idUser) {
        return (int) orders.stream()
                .filter(order -> order.idUser() == idUser)
                .filter(order -> order.timeStampLivraison() != null)
                .count();
    }

    private void seedData() {
        Ingredient tomate = new Ingredient("Sauce tomate");
        Ingredient creme = new Ingredient("Crème fraiche");
        Ingredient mozzarella = new Ingredient("Mozzarella");
        Ingredient jambon = new Ingredient("Jambon");
        Ingredient champignon = new Ingredient("Champignon");
        Ingredient chevre = new Ingredient("Fromage de chèvre");
        Ingredient miel = new Ingredient("Miel");
        Ingredient chorizo = new Ingredient("Chorizo");
        Ingredient poivron = new Ingredient("Poivron");

        Pizza margherita = new Pizza("Margherita", 12, new Ingredient[]{tomate, mozzarella});
        Pizza reine = new Pizza("Reine", 14, new Ingredient[]{tomate, mozzarella, jambon, champignon});
        Pizza chevreMiel = new Pizza("Chèvre miel", 15, new Ingredient[]{creme, mozzarella, chevre, miel});
        Pizza caliente = new Pizza("Caliente", 16, new Ingredient[]{tomate, mozzarella, chorizo, poivron});

        pizzas.addAll(List.of(margherita, reine, chevreMiel, caliente));

        Format naine = new Format("Naine", 67);
        Format humaine = new Format("Humaine", 100);
        Format ogresse = new Format("Ogresse", 134);
        formats.addAll(List.of(naine, humaine, ogresse));

        users.addAll(List.of(
                new User(1, "Jean", "Dupont", "jean.dupont@rappiz.fr", "rappiz"),
                new User(2, "Sarah", "Martin", "sarah.martin@rappiz.fr", "rappiz"),
                new User(3, "Yanis", "Petit", "yanis.petit@rappiz.fr", "rappiz")
        ));
        customerBalances.put(1, 55);
        customerBalances.put(2, 18);
        customerBalances.put(3, 8);

        Vehicule scooter = new Vehicule(1, "Peugeot", "Kisbee");
        Vehicule velo = new Vehicule(2, "O2Feel", "iVog City");
        Vehicule voiture = new Vehicule(3, "Renault", "Twingo");
        Vehicule motoReserve = new Vehicule(4, "Yamaha", "NMAX");
        vehicules.addAll(List.of(scooter, velo, voiture, motoReserve));

        livreurs.addAll(List.of(
                new Livreur(10, "Marc", "Lefèvre", "marc.lefevre@rappiz.fr", "rappiz", scooter),
                new Livreur(11, "Nora", "Bailly", "nora.bailly@rappiz.fr", "rappiz", velo),
                new Livreur(12, "Hugo", "Bernard", "hugo.bernard@rappiz.fr", "rappiz", voiture)
        ));

        orders.add(new Order(1001, 1, margherita, humaine, "30/05/2026 19:10", "30/05/2026 19:38", calculatePrice(margherita, humaine), livreurs.get(0), scooter));
        orders.add(new Order(1002, 1, chevreMiel, ogresse, "01/06/2026 20:05", null, calculatePrice(chevreMiel, ogresse), null, null));
        orders.add(new Order(1003, 2, reine, naine, "01/06/2026 12:30", "01/06/2026 12:55", calculatePrice(reine, naine), livreurs.get(1), velo));
        orders.add(new Order(1004, 3, caliente, humaine, "02/06/2026 11:00", "02/06/2026 11:42", 0, livreurs.get(2), voiture));

        receipts.add(new Receipt(5001, 1001, calculatePrice(margherita, humaine), 1));
        receipts.add(new Receipt(5002, 1002, calculatePrice(chevreMiel, ogresse), 1));
        receipts.add(new Receipt(5003, 1003, calculatePrice(reine, naine), 2));
        receipts.add(new Receipt(5004, 1004, 0, 3));
    }

    private String now() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }
}
