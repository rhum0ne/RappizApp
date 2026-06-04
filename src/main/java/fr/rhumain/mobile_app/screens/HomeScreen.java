package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Order;
import fr.rhumain.structs.Receipt;
import fr.rhumain.structs.User;
import fr.rhumain.ui.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomeScreen extends JPanel {

    private final MobileApp mobileApp;
    private final User user;

    public HomeScreen(MobileApp mobileApp) {
        this.mobileApp = mobileApp;
        this.user = mobileApp.getConnectedUser() != null
                ? mobileApp.getConnectedUser()
                : mobileApp.getServer().getUserById(1);

        this.setLayout(new BorderLayout(0, 15));
        this.setBackground(AppTheme.BACKGROUND);
        this.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // --- EN-TÊTE (Titre + Bouton) ---
        this.add(createHeaderPanel(), BorderLayout.NORTH);

        // --- CONTENU (Liste des commandes avec Scroll) ---
        JPanel ordersPanel = createOrdersPanel();
        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(headerPanel);

        JLabel title = new JLabel("Bonjour " + user.firstName() + " !");
        title.setFont(AppTheme.TITLE_FONT);
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(user.email());
        subtitle.setFont(AppTheme.BODY_FONT);
        subtitle.setForeground(AppTheme.MUTED_TEXT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel balance = new JLabel("Solde prépayé : " + mobileApp.getServer().getCustomerBalance(user.id()) + " €");
        balance.setFont(AppTheme.SECTION_FONT);
        balance.setForeground(AppTheme.PRIMARY);
        balance.setAlignmentX(Component.LEFT_ALIGNMENT);

        int fidelityCount = mobileApp.getServer().getBoughtPizzaCount(user.id()) % 10;
        JLabel fidelity = new JLabel("Fidélité : " + fidelityCount + "/10 pizzas avant la prochaine offerte");
        fidelity.setFont(AppTheme.BODY_FONT);
        fidelity.setForeground(AppTheme.MUTED_TEXT);
        fidelity.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton orderButton = new JButton("Passer une commande");
        orderButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        orderButton.addActionListener(e -> mobileApp.setScreen(new OrderScreen(mobileApp)));
        mobileApp.styleButton(orderButton);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        headerPanel.add(balance);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(fidelity);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        headerPanel.add(orderButton);

        return headerPanel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.BACKGROUND);

        Order activeOrder = mobileApp.getServer().getActiveOrder(user.id());
        if (activeOrder != null) {
            panel.add(createSectionTitle("Commande en cours"));
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
            panel.add(createOrderComponent(activeOrder));
            panel.add(Box.createRigidArea(new Dimension(0, 18)));
        }

        panel.add(createSectionTitle("Historique des commandes"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        List<Order> orders = mobileApp.getServer().getOrdersByUserId(user.id())
                .stream()
                .filter(order -> activeOrder == null || order.id() != activeOrder.id())
                .toList();
        if (orders.isEmpty()) {
            panel.add(createEmptyState());
        } else {
            for (Order order : orders) {
                panel.add(createOrderComponent(order));
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createSectionTitle("Reçus et factures"));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        for (Receipt receipt : mobileApp.getServer().getReceiptsByUserId(user.id())) {
            panel.add(createReceiptComponent(receipt));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.SECTION_FONT);
        label.setForeground(AppTheme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JComponent createOrderComponent(final Order order) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(panel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String pizzaName = order.pizza() != null ? order.pizza().name() : "Inconnue";
        String formatName = order.format() != null ? order.format().nom() : "N/A";
        String ingredients = order.pizza() != null
                ? Arrays.stream(order.pizza().ingredients()).map(Ingredient::nom).collect(Collectors.joining(", "))
                : "Non renseignés";

        JLabel title = new JLabel("Commande #" + order.id() + " - " + getStatus(order));
        title.setFont(AppTheme.SECTION_FONT);
        title.setForeground(order.timeStampLivraison() == null ? AppTheme.WARNING : AppTheme.SUCCESS);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createInfoLine("Pizza", pizzaName + " - " + formatName));
        panel.add(createInfoLine("Ingrédients", ingredients));
        panel.add(createInfoLine("Prix total", order.price() + " €"));
        panel.add(createInfoLine("Commandée le", order.timeStamp().format(AppTheme.DATE_TIME_FORMATTER)));
        panel.add(createInfoLine("Livraison", order.timeStampLivraison() != null ? order.timeStampLivraison().format(AppTheme.DATE_TIME_FORMATTER) : "En préparation"));
        panel.add(createInfoLine("Livreur", getDeliveryInfo(order)));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }

    private JLabel createInfoLine(String label, String value) {
        JLabel info = new JLabel(label + " : " + value);
        info.setFont(AppTheme.BODY_FONT);
        info.setForeground(AppTheme.TEXT);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        return info;
    }

    private JComponent createEmptyState() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(panel);

        JLabel label = new JLabel("Aucune commande pour le moment.");
        label.setFont(AppTheme.BODY_FONT);
        label.setForeground(AppTheme.MUTED_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JComponent createReceiptComponent(Receipt receipt) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(panel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Reçu #" + receipt.id());
        title.setFont(AppTheme.SECTION_FONT);
        title.setForeground(AppTheme.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createInfoLine("Commande associée", "#" + receipt.idOrder()));
        panel.add(createInfoLine("Montant payé", receipt.price() + " €"));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private String getStatus(Order order) {
        return order.timeStampLivraison() == null ? "En préparation" : "Livrée";
    }

    private String getDeliveryInfo(Order order) {
        if (order.livreur() == null || order.vehicule() == null) {
            return "Livreur non assigné";
        }
        return order.livreur().firstName() + " " + order.livreur().lastName()
                + " (" + order.vehicule().brand() + " " + order.vehicule().model() + ")";
    }
}