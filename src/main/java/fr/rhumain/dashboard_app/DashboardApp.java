package fr.rhumain.dashboard_app;

import fr.rhumain.rappiz_server.DashboardAppConnector;
import fr.rhumain.structs.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardApp extends JFrame {

    private final DashboardAppConnector server;
    private final JTable ordersTable;
    private final DefaultTableModel tableModel;

    public DashboardApp(DashboardAppConnector server) {
        this.server = server;

        this.setTitle("Rappiz - Dashboard Administrateur");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700);
        this.setLayout(new BorderLayout());

        // --- EN-TÊTE ---
        JLabel titleLabel = new JLabel("Gestion Globale des Commandes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // --- TABLEAU DES COMMANDES ---
        // Définition des colonnes du tableau
        String[] columns = {"ID Cmd", "ID Client", "Pizza", "Format", "Prix", "Livreur", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêche l'édition directe des cellules au clic
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(30); // Lignes plus aérées
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        ordersTable.getTableHeader().setBackground(new Color(230, 230, 230));

        // Ajout du tableau dans un ScrollPane pour gérer les longues listes
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANNEAU DU BAS (Boutons d'action) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton refreshButton = new JButton("Rafraîchir les données");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadOrdersIntoTable());

        bottomPanel.add(refreshButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // --- CHARGEMENT INITIAL ---
        loadOrdersIntoTable();

        // --- CONFIGURATION FINALE ---
        this.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * Méthode qui récupère les commandes depuis le connecteur et peuple le tableau.
     */
    private void loadOrdersIntoTable() {
        // Vider le tableau actuel avant de recharger
        tableModel.setRowCount(0);

        List<Order> orders = server.getAllOrders();

        for (Order order : orders) {
            // Sécurisation des données nulles
            String pizzaName = (order.pizza() != null) ? order.pizza().name() : "N/A";
            String formatName = (order.format() != null) ? order.format().nom() : "N/A";
            String livreurName = (order.livreur() != null) ? (order.livreur().firstName() + " " + order.livreur().lastName()) : "Non assigné";

            // Déduction du statut basée sur la date de livraison
            String statut = "Livrée";
            if (order.timeStampLivraison() == null || order.timeStampLivraison().isEmpty()) {
                statut = "En attente"; // Ou "En cours de livraison"
            }

            // Création de la ligne
            Object[] row = {
                    "#" + order.id(),
                    order.idUser(),
                    pizzaName,
                    formatName,
                    order.price() + " €",
                    livreurName,
                    statut
            };

            // Ajout au modèle du tableau
            tableModel.addRow(row);
        }
    }
}