package fr.rhumain.rappiz_server;

import fr.rhumain.structs.*;

import java.util.Arrays;
import java.util.List;

public class ClientAppConnector {

    /**
     * Récupère la liste des pizzas disponibles au menu.
     */
    public List<Pizza> getPizzas() {
        Ingredient tomate = new Ingredient("Sauce Tomate");
        Ingredient creme = new Ingredient("Crème Fraîche");
        Ingredient mozza = new Ingredient("Mozzarella");
        Ingredient chevre = new Ingredient("Chèvre");
        Ingredient miel = new Ingredient("Miel");
        Ingredient jambon = new Ingredient("Jambon blanc");
        Ingredient champignon = new Ingredient("Champignons de Paris");

        Pizza margarita = new Pizza("Margarita", 12, new Ingredient[]{tomate, mozza});
        Pizza reine = new Pizza("Reine", 14, new Ingredient[]{tomate, mozza, jambon, champignon});
        Pizza chevreMiel = new Pizza("Chèvre Miel", 15, new Ingredient[]{creme, mozza, chevre, miel});
        Pizza calzone = new Pizza("Calzone", 16, new Ingredient[]{tomate, mozza, jambon, champignon});

        return Arrays.asList(margarita, reine, chevreMiel, calzone);
    }

    /**
     * Simule la création d'une commande depuis l'application cliente.
     */
    public boolean createOrder(int idUser, Pizza pizza, Format format) {
        // Dans une vraie application, ici on ferait une requête HTTP vers le serveur
        // pour insérer la commande en base de données.
        // Pour l'interface, on simule que la commande passe toujours avec succès (true).

        System.out.println("--- NOUVELLE COMMANDE SIMULÉE ---");
        System.out.println("Utilisateur ID : " + idUser);
        System.out.println("Pizza choisie  : " + pizza.name());
        System.out.println("Format choisi  : " + format.nom());

        // Calcul du prix simulé
        int prixTotal = (pizza.price() * format.pricePercetage()) / 100;
        System.out.println("Prix à payer   : " + prixTotal + " €");
        System.out.println("---------------------------------");

        return true;
    }

    /**
     * Récupère les informations basiques du client depuis son ID.
     */
    public User getUserById(int idUser) {
        return new User(
                idUser,
                "Jean",
                "Dupont",
                "jean.dupont@email.com",
                "motdepasseSecret123"
        );
    }

    /**
     * Récupère l'historique ou les commandes en cours d'un client.
     */
    public List<Order> getOrdersByUserId(int idUser) {
        // Création d'ingrédients
        Ingredient tomate = new Ingredient("Sauce Tomate");
        Ingredient mozza = new Ingredient("Mozzarella");
        Ingredient jambon = new Ingredient("Jambon blanc");
        Ingredient champignon = new Ingredient("Champignons de Paris");

        // Création de pizzas avec un tableau d'ingrédients
        Pizza margarita = new Pizza("Margarita", 12, new Ingredient[]{tomate, mozza});
        Pizza reine = new Pizza("Reine", 14, new Ingredient[]{tomate, mozza, jambon, champignon});

        // Création des formats (pricePercetage correspond au surcoût/réduction)
        Format classique = new Format("Classique", 100);
        Format large = new Format("Large", 130);

        // Création du livreur et de son véhicule
        Vehicule scooter = new Vehicule(1, "Peugeot", "Kisbee");
        Livreur livreur = new Livreur(10, "Marc", "Lefèvre", "marc.livreur@rappiz.com", "pass123", scooter);

        // Création de deux commandes factices
        Order order1 = new Order(
                1001,
                idUser,
                margarita,
                classique,
                "2026-05-20T19:00:00Z",
                "2026-05-20T19:30:00Z",
                12,
                livreur,
                scooter // Repassé ici car défini dans ton record Order
        );

        Order order2 = new Order(
                1002,
                idUser,
                reine,
                large,
                "2026-05-18T20:00:00Z",
                "2026-05-18T20:45:00Z",
                18,
                livreur,
                scooter
        );

        return Arrays.asList(order1, order2);
    }

    /**
     * Récupère la liste des reçus / factures associés à l'utilisateur.
     */
    public List<Receipt> getReceiptsByUserId(int idUser) {
        Receipt receipt1 = new Receipt(5001, 1001, 12, idUser);
        Receipt receipt2 = new Receipt(5002, 1002, 18, idUser);

        return Arrays.asList(receipt1, receipt2);
    }

    /**
     * Méthode bonus : si tu as besoin de simuler l'état d'une commande en direct.
     */
    public Order getActiveOrder(int idUser) {
        List<Order> allOrders = getOrdersByUserId(idUser);
        // On retourne arbitrairement la première commande pour tester l'affichage "en cours"
        return allOrders.get(0);
    }
}