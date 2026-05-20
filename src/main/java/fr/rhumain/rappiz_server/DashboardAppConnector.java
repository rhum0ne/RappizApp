package fr.rhumain.rappiz_server;

import fr.rhumain.structs.*;

import java.util.Arrays;
import java.util.List;

public class DashboardAppConnector {

    /**
     * Récupère toutes les commandes de la plateforme pour le dashboard.
     */
    public List<Order> getAllOrders() {
        // Ingrédients et Pizzas de base
        Ingredient tomate = new Ingredient("Sauce Tomate");
        Ingredient mozza = new Ingredient("Mozzarella");
        Ingredient jambon = new Ingredient("Jambon blanc");

        Pizza margarita = new Pizza("Margarita", 12, new Ingredient[]{tomate, mozza});
        Pizza reine = new Pizza("Reine", 14, new Ingredient[]{tomate, mozza, jambon});

        Format classique = new Format("Classique", 100);
        Format large = new Format("Large", 130);

        // Employés
        Vehicule scooter = new Vehicule(1, "Peugeot", "Kisbee");
        Livreur marc = new Livreur(10, "Marc", "Lefèvre", "marc@rappiz.com", "pass", scooter);

        // Historique factice (statuts variés)
        Order order1 = new Order(1001, 1, margarita, classique, "2026-05-20T18:00:00Z", "2026-05-20T18:30:00Z", 12, marc, scooter);

        // Commande en cours (pas de date de livraison, pas de livreur)
        Order order2 = new Order(1002, 2, reine, large, "2026-05-20T19:00:00Z", null, 18, null, null);

        Order order3 = new Order(1003, 3, margarita, large, "2026-05-19T20:00:00Z", "2026-05-19T20:45:00Z", 15, marc, scooter);

        return Arrays.asList(order1, order2, order3);
    }
}