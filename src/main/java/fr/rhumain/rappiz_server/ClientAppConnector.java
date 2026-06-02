package fr.rhumain.rappiz_server;

import fr.rhumain.structs.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ClientAppConnector {

    private final RappizDataStore dataStore;

    public ClientAppConnector() {
        this(new RappizDataStore());
    }

    public ClientAppConnector(RappizDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Récupère la liste des pizzas disponibles au menu.
     */
    public List<Pizza> getPizzas() {
        return dataStore.getPizzas();
    }

    /**
     * Récupère les formats disponibles pour composer une commande.
     */
    public List<Format> getFormats() {
        return dataStore.getFormats();
    }

    /**
     * Simule la création d'une commande depuis l'application cliente.
     */
    public boolean createOrder(int idUser, Pizza pizza, Format format) {
        Order order = dataStore.createOrder(idUser, pizza, format);
        if (order == null) {
            System.out.println("Commande refusée pour le client #" + idUser + " : solde insuffisant");
            return false;
        }
        System.out.println("Nouvelle commande #" + order.id() + " créée pour le client #" + idUser);
        return true;
    }

    public int calculatePrice(Pizza pizza, Format format) {
        return dataStore.calculatePrice(pizza, format);
    }

    public int getCustomerBalance(int idUser) {
        return dataStore.getCustomerBalance(idUser);
    }

    public int getBoughtPizzaCount(int idUser) {
        return dataStore.getBoughtPizzaCount(idUser);
    }

    public Optional<User> authenticateUser(String email, String password) {
        return dataStore.authenticateUser(email, password);
    }

    /**
     * Récupère les informations basiques du client depuis son ID.
     */
    public User getUserById(int idUser) {
        return dataStore.findUserById(idUser).orElse(null);
    }

    /**
     * Récupère l'historique ou les commandes en cours d'un client.
     */
    public List<Order> getOrdersByUserId(int idUser) {
        return dataStore.getOrders()
                .stream()
                .filter(order -> order.idUser() == idUser)
                .sorted(Comparator.comparingInt(Order::id).reversed())
                .toList();
    }

    /**
     * Récupère la liste des reçus / factures associés à l'utilisateur.
     */
    public List<Receipt> getReceiptsByUserId(int idUser) {
        return dataStore.getReceipts()
                .stream()
                .filter(receipt -> receipt.idUser() == idUser)
                .toList();
    }

    /**
     * Méthode bonus : si tu as besoin de simuler l'état d'une commande en direct.
     */
    public Order getActiveOrder(int idUser) {
        return getOrdersByUserId(idUser)
                .stream()
                .filter(order -> order.timeStampLivraison() == null)
                .findFirst()
                .orElse(null);
    }
}