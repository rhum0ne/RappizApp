package fr.rhumain.dashboard_app;

import fr.rhumain.rappiz_server.DashboardAppConnector;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Livreur;
import fr.rhumain.structs.Order;
import fr.rhumain.structs.Pizza;
import fr.rhumain.structs.User;
import fr.rhumain.structs.Vehicule;
import fr.rhumain.ui.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DashboardApp extends JFrame {

    private final DashboardAppConnector server;
    private final JTable ordersTable;
    private final DefaultTableModel tableModel;
    private final JLabel totalOrdersValue;
    private final JLabel pendingOrdersValue;
    private final JLabel deliveredOrdersValue;
    private final JLabel revenueValue;
    private final JLabel bestCustomerValue;
    private final JLabel worstDeliveryValue;
    private final JLabel mostOrderedPizzaValue;
    private final JLabel leastOrderedPizzaValue;
    private final JLabel favoriteIngredientValue;
    private final JLabel unusedVehiclesValue;
    private final JLabel averageOrdersValue;
    private final JLabel clientsAboveAverageValue;
    private final JLabel ordersByClientValue;
    private final JComboBox<Livreur> livreurComboBox;
    private final JComboBox<Vehicule> vehiculeComboBox;

    public DashboardApp(DashboardAppConnector server) {
        this.server = server;

        this.setTitle("Rappiz - Dashboard Administrateur");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 850);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(AppTheme.BACKGROUND);

        // --- EN-TÊTE ---
        JPanel headerPanel = createHeaderPanel();
        totalOrdersValue = createMetricValue();
        pendingOrdersValue = createMetricValue();
        deliveredOrdersValue = createMetricValue();
        revenueValue = createMetricValue();
        bestCustomerValue = createMetricValue();
        worstDeliveryValue = createMetricValue();
        mostOrderedPizzaValue = createMetricValue();
        leastOrderedPizzaValue = createMetricValue();
        favoriteIngredientValue = createMetricValue();
        unusedVehiclesValue = createMetricValue();
        averageOrdersValue = createMetricValue();
        clientsAboveAverageValue = createMetricValue();
        ordersByClientValue = createMetricValue();

        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setBackground(AppTheme.BACKGROUND);
        metricsPanel.add(createMetricCard("Commandes", totalOrdersValue));
        metricsPanel.add(createMetricCard("En préparation", pendingOrdersValue));
        metricsPanel.add(createMetricCard("Livrées", deliveredOrdersValue));
        metricsPanel.add(createMetricCard("Chiffre d'affaires", revenueValue));

        JPanel statisticsPanel = new JPanel(new GridLayout(3, 3, 12, 12));
        statisticsPanel.setBackground(AppTheme.BACKGROUND);
        statisticsPanel.add(createMetricCard("Meilleur client", bestCustomerValue));
        statisticsPanel.add(createMetricCard("Commandes par client", ordersByClientValue));
        statisticsPanel.add(createMetricCard("Livreur le plus en retard", worstDeliveryValue));
        statisticsPanel.add(createMetricCard("Pizza la plus demandée", mostOrderedPizzaValue));
        statisticsPanel.add(createMetricCard("Pizza la moins demandée", leastOrderedPizzaValue));
        statisticsPanel.add(createMetricCard("Ingrédient favori", favoriteIngredientValue));
        statisticsPanel.add(createMetricCard("Véhicules jamais servis", unusedVehiclesValue));
        statisticsPanel.add(createMetricCard("Moyenne commandes/client", averageOrdersValue));
        statisticsPanel.add(createMetricCard("Clients au-dessus moyenne", clientsAboveAverageValue));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(AppTheme.BACKGROUND);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        topPanel.add(headerPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        topPanel.add(metricsPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        topPanel.add(statisticsPanel);
        this.add(topPanel, BorderLayout.NORTH);

        // --- TABLEAU DES COMMANDES ---
        // Définition des colonnes du tableau
        String[] columns = {"Commande", "Client", "Pizza", "Format", "Prix base", "Prix facturé", "Date commande", "Date livraison", "Livreur", "Véhicule", "Type véhicule", "Retard", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêche l'édition directe des cellules au clic
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(34);
        ordersTable.setFont(AppTheme.BODY_FONT);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTable.getTableHeader().setBackground(AppTheme.PRIMARY);
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.setDefaultRenderer(Object.class, createTableRenderer());

        // Ajout du tableau dans un ScrollPane pour gérer les longues listes
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANNEAU DU BAS (Boutons d'action) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(AppTheme.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        livreurComboBox = new JComboBox<>();
        vehiculeComboBox = new JComboBox<>();
        configureLivreurComboBox();
        configureVehiculeComboBox();
        loadDeliveryOptions();

        JButton refreshButton = new JButton("Rafraîchir les données");
        AppTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> {
            loadDeliveryOptions();
            loadOrdersIntoTable();
        });

        JButton deliverButton = new JButton("Marquer comme livrée");
        AppTheme.stylePrimaryButton(deliverButton);
        deliverButton.addActionListener(e -> markSelectedOrderDelivered());

        bottomPanel.add(new JLabel("Livreur :"));
        bottomPanel.add(livreurComboBox);
        bottomPanel.add(new JLabel("Véhicule :"));
        bottomPanel.add(vehiculeComboBox);
        bottomPanel.add(refreshButton);
        bottomPanel.add(deliverButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // --- CHARGEMENT INITIAL ---
        loadOrdersIntoTable();

        // --- CONFIGURATION FINALE ---
        this.setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        this.setResizable(false);
        this.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(AppTheme.BACKGROUND);

        JLabel titleLabel = new JLabel("Dashboard Rappiz");
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Suivi des commandes, des livreurs et du chiffre d'affaires");
        subtitleLabel.setFont(AppTheme.BODY_FONT);
        subtitleLabel.setForeground(AppTheme.MUTED_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        AppTheme.styleCard(card);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.SMALL_FONT);
        titleLabel.setForeground(AppTheme.MUTED_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(valueLabel);

        return card;
    }

    private JLabel createMetricValue() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(AppTheme.PRIMARY);
        return label;
    }

    private void configureLivreurComboBox() {
        livreurComboBox.setFont(AppTheme.BODY_FONT);
        livreurComboBox.setPreferredSize(new Dimension(180, 36));
        livreurComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Livreur livreur) {
                    setText(livreur.firstName() + " " + livreur.lastName());
                } else {
                    setText("Choisir un livreur");
                }
                return component;
            }
        });
    }

    private void configureVehiculeComboBox() {
        vehiculeComboBox.setFont(AppTheme.BODY_FONT);
        vehiculeComboBox.setPreferredSize(new Dimension(190, 36));
        vehiculeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vehicule vehicule) {
                    setText(vehicule.brand() + " " + vehicule.model());
                } else {
                    setText("Choisir un véhicule");
                }
                return component;
            }
        });
    }

    private void loadDeliveryOptions() {
        livreurComboBox.removeAllItems();
        livreurComboBox.addItem(null);
        for (Livreur livreur : server.getAllLivreurs()) {
            livreurComboBox.addItem(livreur);
        }

        vehiculeComboBox.removeAllItems();
        vehiculeComboBox.addItem(null);
        for (Vehicule vehicule : server.getAllVehicules()) {
            vehiculeComboBox.addItem(vehicule);
        }
    }

    /**
     * Méthode qui récupère les commandes depuis le connecteur et peuple le tableau.
     */
    private void loadOrdersIntoTable() {
        // Vider le tableau actuel avant de recharger
        tableModel.setRowCount(0);

        List<Order> orders = server.getAllOrders();
        refreshMetrics(orders);
        refreshStatistics(orders);

        for (Order order : orders) {
            // Sécurisation des données nulles
            String pizzaName = (order.pizza() != null) ? order.pizza().name() : "N/A";
            String formatName = (order.format() != null) ? order.format().nom() : "N/A";
            String livreurName = getDeliveryPerson(order);
            String vehicleName = getVehicleName(order);
            String vehicleType = getVehicleType(order.vehicule());
            String clientName = getClientName(order.User() != null ? order.User().id() : null);
            String statut = getStatus(order);

            // Création de la ligne
            Object[] row = {
                    "#" + order.id(),
                    clientName,
                    pizzaName,
                    formatName,
                    getBasePrice(order),
                    order.price() + " €",
                    order.timeStamp(),
                    order.timeStampLivraison() != null ? order.timeStampLivraison() : "Non livrée",
                    livreurName,
                    vehicleName,
                    vehicleType,
                    getDelayLabel(order),
                    statut
            };

            // Ajout au modèle du tableau
            tableModel.addRow(row);
        }
    }

    private void refreshMetrics(List<Order> orders) {
        int totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(order -> order.timeStampLivraison() == null).count();
        long deliveredOrders = totalOrders - pendingOrders;
        int revenue = orders.stream().mapToInt(Order::price).sum();

        totalOrdersValue.setText(String.valueOf(totalOrders));
        pendingOrdersValue.setText(String.valueOf(pendingOrders));
        deliveredOrdersValue.setText(String.valueOf(deliveredOrders));
        revenueValue.setText(revenue + " €");
    }

    private void refreshStatistics(List<Order> orders) {
        List<User> users = server.getAllUsers();
        Map<Integer, Long> ordersByClient = orders.stream()
                .filter(order -> order.User() != null)
                .collect(Collectors.groupingBy(order -> order.User().id(), Collectors.counting()));

        bestCustomerValue.setText(ordersByClient.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> getClientName(entry.getKey()) + " (" + entry.getValue() + ")")
                .orElse("Aucun"));
        ordersByClientValue.setText("<html>" + users.stream()
                .map(user -> user.firstName() + " " + user.lastName() + ": " + ordersByClient.getOrDefault(user.id(), 0L))
                .collect(Collectors.joining("<br>")) + "</html>");

        double average = users.isEmpty() ? 0 : (double) orders.size() / users.size();
        averageOrdersValue.setText(String.format("%.1f", average));
        clientsAboveAverageValue.setText(users.stream()
                .filter(user -> ordersByClient.getOrDefault(user.id(), 0L) > average)
                .map(user -> user.firstName() + " " + user.lastName())
                .collect(Collectors.joining(", ")));
        if (clientsAboveAverageValue.getText().isEmpty()) {
            clientsAboveAverageValue.setText("Aucun");
        }

        Map<String, Long> pizzaCounts = orders.stream()
                .map(Order::pizza)
                .filter(pizza -> pizza != null)
                .map(Pizza::name)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        mostOrderedPizzaValue.setText(findExtremeLabel(pizzaCounts, true));
        leastOrderedPizzaValue.setText(findExtremeLabel(pizzaCounts, false));

        Map<String, Long> ingredientCounts = orders.stream()
                .map(Order::pizza)
                .filter(pizza -> pizza != null)
                .flatMap(pizza -> Arrays.stream(pizza.ingredients()))
                .map(Ingredient::nom)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        favoriteIngredientValue.setText(findExtremeLabel(ingredientCounts, true));

        Map<String, Long> lateDeliveriesByLivreur = orders.stream()
                .filter(order -> order.livreur() != null)
                .filter(order -> getDelayMinutes(order) > 30)
                .collect(Collectors.groupingBy(order -> order.livreur().firstName() + " " + order.livreur().lastName(), Collectors.counting()));
        worstDeliveryValue.setText(orders.stream()
                .filter(order -> order.livreur() != null)
                .filter(order -> getDelayMinutes(order) > 30)
                .max(Comparator.comparingLong(this::getDelayMinutes))
                .map(order -> getDeliveryPerson(order) + " (" + lateDeliveriesByLivreur.get(getDeliveryPerson(order)) + ", " + getVehicleName(order) + ")")
                .orElse("Aucun"));

        unusedVehiclesValue.setText(server.getUnusedVehicules().stream()
                .map(vehicle -> vehicle.brand() + " " + vehicle.model())
                .collect(Collectors.joining(", ")));
        if (unusedVehiclesValue.getText().isEmpty()) {
            unusedVehiclesValue.setText("Aucun");
        }
    }

    private void markSelectedOrderDelivered() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sélectionnez d'abord une commande dans le tableau.",
                    "Aucune commande sélectionnée",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Livreur selectedLivreur = (Livreur) livreurComboBox.getSelectedItem();
        Vehicule selectedVehicule = (Vehicule) vehiculeComboBox.getSelectedItem();
        if (selectedLivreur == null || selectedVehicule == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Choisissez un livreur et un véhicule avant de marquer la commande comme livrée.",
                    "Livreur ou véhicule manquant",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String rawOrderId = tableModel.getValueAt(selectedRow, 0).toString().replace("#", "");
        int orderId = Integer.parseInt(rawOrderId);
        boolean updated = server.markOrderDelivered(orderId, selectedLivreur.id(), selectedVehicule.id());

        if (!updated) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cette commande est déjà livrée, introuvable, ou le livreur/véhicule choisi n'existe plus.",
                    "Action impossible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        loadOrdersIntoTable();
    }

    private DefaultTableCellRenderer createTableRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                component.setFont(AppTheme.BODY_FONT);

                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : AppTheme.BACKGROUND);
                    component.setForeground(AppTheme.TEXT);
                }

                if (column == table.getColumnCount() - 1 && value != null) {
                    component.setForeground("Livrée".equals(value.toString()) ? AppTheme.SUCCESS : AppTheme.WARNING);
                }

                return component;
            }
        };
    }

    private String getStatus(Order order) {
        return order.timeStampLivraison() == null ? "En préparation" : "Livrée";
    }

    private String getDeliveryPerson(Order order) {
        if (order.livreur() == null) {
            return "Non assigné";
        }
        return order.livreur().firstName() + " " + order.livreur().lastName();
    }

    private String getVehicleName(Order order) {
        if (order.vehicule() == null) {
            return "Non assigné";
        }
        return order.vehicule().brand() + " " + order.vehicule().model();
    }

    private String getVehicleType(Vehicule vehicule) {
        if (vehicule == null) {
            return "N/A";
        }

        String label = (vehicule.brand() + " " + vehicule.model()).toLowerCase();
        if (label.contains("kisbee") || label.contains("nmax")) {
            return "Moto";
        }
        if (label.contains("o2feel")) {
            return "Vélo";
        }
        return "Voiture";
    }

    private String getBasePrice(Order order) {
        if (order.pizza() == null) {
            return "N/A";
        }
        return order.pizza().price() + " €";
    }

    private String getDelayLabel(Order order) {
        if (order.timeStampLivraison() == null) {
            return "En cours";
        }

        long minutes = getDelayMinutes(order);
        if (minutes <= 30) {
            return "Aucun";
        }
        return "+" + (minutes - 30) + " min";
    }

    private long getDelayMinutes(Order order) {
        if (order.timeStampLivraison() == null) {
            return 0;
        }
        return Duration.between(order.timeStamp(), order.timeStampLivraison()).toMinutes();
    }

    private String findExtremeLabel(Map<String, Long> values, boolean max) {
        Comparator<Map.Entry<String, Long>> comparator = Map.Entry.comparingByValue();
        return (max ? values.entrySet().stream().max(comparator) : values.entrySet().stream().min(comparator))
                .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                .orElse("Aucun");
    }

    private String getClientName(Integer idUser) {
        if (idUser == null) {
            return "Client inconnu";
        }
        User user = server.getUserById(idUser);
        if (user == null) {
            return "Client #" + idUser;
        }
        return user.firstName() + " " + user.lastName();
    }
}