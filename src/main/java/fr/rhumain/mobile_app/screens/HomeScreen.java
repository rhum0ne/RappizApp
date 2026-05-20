package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.Order;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HomeScreen extends JPanel {

    private final MobileApp mobileApp;

    public HomeScreen(MobileApp mobileApp) {
        this.mobileApp = mobileApp;

        this.setLayout(new BorderLayout(0, 15)); // 15px d'espacement vertical

        // --- EN-TÊTE (Titre + Bouton) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Bienvenue sur Rappiz !");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espacement

        JButton orderButton = new JButton("Passer une commande");
        orderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderButton.addActionListener(e -> mobileApp.setScreen(new OrderScreen(mobileApp)));
        mobileApp.styleButton(orderButton);
        headerPanel.add(orderButton);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- CONTENU (Liste des commandes avec Scroll) ---
        JPanel ordersPanel = createOrdersPanel();
        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null); // Retire la bordure moche du scrollpane par défaut
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll plus fluide

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Récupération des données mockées via le connecteur
        // Je mets l'ID utilisateur "1" arbitrairement pour le test
        List<Order> orders = mobileApp.getServer().getOrdersByUserId(1);

        for (Order order : orders) {
            panel.add(createOrderComponent(order));
            panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement entre chaque carte
        }

        return panel;
    }

    private JComponent createOrderComponent(final Order order) {
        JPanel panel = new JPanel();

        // Configuration du layout (1 colonne, nombre de lignes dynamique, marges de 5px)
        panel.setLayout(new GridLayout(0, 1, 5, 5));

        // Ajout d'une bordure avec le numéro de la commande comme titre
        panel.setBorder(BorderFactory.createTitledBorder("Commande #" + order.id()));

        // Création des labels pour les informations principales
        JLabel userLabel = new JLabel("ID Utilisateur : " + order.idUser());

        // Utilisation propre des accesseurs des records au lieu de toString()
        String pizzaName = order.pizza() != null ? order.pizza().name() : "Inconnue";
        String formatName = order.format() != null ? order.format().nom() : "N/A";
        JLabel pizzaLabel = new JLabel("Pizza : " + pizzaName + " (Format : " + formatName + ")");

        JLabel timeLabel = new JLabel("Créée le : " + (order.timeStamp() != null ? order.timeStamp() : "N/A"));
        JLabel timeDeliveryLabel = new JLabel("Livraison : " + (order.timeStampLivraison() != null ? order.timeStampLivraison() : "En attente"));

        JLabel priceLabel = new JLabel("Prix total : " + order.price() + " €");

        // Informations sur la livraison (utilisation des getters du record Livreur et Vehicule)
        String livreurName = order.livreur() != null ? (order.livreur().firstName() + " " + order.livreur().lastName()) : "Non assigné";
        String vehiculeName = order.vehicule() != null ? (order.vehicule().brand() + " " + order.vehicule().model()) : "Aucun";
        JLabel deliveryLabel = new JLabel("Livreur : " + livreurName + " | Véhicule : " + vehiculeName);

        // Ajout des composants au panneau
        panel.add(userLabel);
        panel.add(pizzaLabel);
        panel.add(priceLabel);
        panel.add(timeLabel);
        panel.add(timeDeliveryLabel);
        panel.add(deliveryLabel);

        // Marge interne supplémentaire
        panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Empêche la carte de prendre toute la hauteur disponible dans le BoxLayout
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }
}