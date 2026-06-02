package fr.rhumain.rappiz_server;

import fr.rhumain.structs.*;

import java.util.List;

public class DashboardAppConnector {

    private final RappizDataStore dataStore;

    public DashboardAppConnector() {
        this(new RappizDataStore());
    }

    public DashboardAppConnector(RappizDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Récupère toutes les commandes de la plateforme pour le dashboard.
     */
    public List<Order> getAllOrders() {
        return dataStore.getOrders();
    }

    public List<User> getAllUsers() {
        return dataStore.getUsers();
    }

    public List<Vehicule> getAllVehicules() {
        return dataStore.getVehicules();
    }

    public User getUserById(int idUser) {
        return dataStore.findUserById(idUser).orElse(null);
    }

    public boolean markOrderDelivered(int idOrder) {
        return dataStore.markOrderDelivered(idOrder);
    }
}