package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.Format;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Pizza;
import fr.rhumain.structs.User;
import fr.rhumain.ui.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderScreen extends JPanel {

    private final MobileApp mobileApp;
    private JComboBox<String> pizzaComboBox;
    private JComboBox<String> formatComboBox;
    private JLabel ingredientsLabel;
    private JLabel pricePreview;
    private JLabel balancePreview;
    private List<Pizza> availablePizzas;
    private List<Format> availableFormats;

    public OrderScreen(MobileApp mobileApp) {
        this.mobileApp = mobileApp;
        this.setLayout(new BorderLayout(0, 20));
        this.setBackground(AppTheme.BACKGROUND);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(AppTheme.BACKGROUND);

        JButton backButton = new JButton("← Retour");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        AppTheme.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> mobileApp.setScreen(new HomeScreen(mobileApp)));

        JLabel title = new JLabel("Composer votre commande");
        title.setFont(AppTheme.TITLE_FONT);
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(backButton);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(title);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- FORMULAIRE ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(formPanel);

        // 1. Choix de la Pizza
        JLabel pizzaLabel = new JLabel("1. Choisissez votre pizza :");
        pizzaLabel.setFont(AppTheme.SECTION_FONT);
        pizzaLabel.setForeground(AppTheme.TEXT);
        pizzaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Récupération des pizzas depuis le connecteur
        availablePizzas = mobileApp.getServer().getPizzas();
        String[] pizzaNames = availablePizzas.stream()
                .map(p -> p.name() + " (" + p.price() + " €)")
                .toArray(String[]::new);

        pizzaComboBox = new JComboBox<>(pizzaNames);
        pizzaComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pizzaComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        pizzaComboBox.setFont(AppTheme.BODY_FONT);

        // 2. Choix du Format
        JLabel formatLabel = new JLabel("2. Choisissez la taille :");
        formatLabel.setFont(AppTheme.SECTION_FONT);
        formatLabel.setForeground(AppTheme.TEXT);
        formatLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        availableFormats = mobileApp.getServer().getFormats();
        String[] formatNames = availableFormats.stream()
                .map(format -> format.nom() + " (" + format.pricePercetage() + "%)")
                .toArray(String[]::new);

        formatComboBox = new JComboBox<>(formatNames);
        formatComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formatComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formatComboBox.setFont(AppTheme.BODY_FONT);

        ingredientsLabel = new JLabel();
        ingredientsLabel.setFont(AppTheme.BODY_FONT);
        ingredientsLabel.setForeground(AppTheme.MUTED_TEXT);
        ingredientsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pricePreview = new JLabel();
        pricePreview.setFont(AppTheme.SECTION_FONT);
        pricePreview.setForeground(AppTheme.PRIMARY);
        pricePreview.setAlignmentX(Component.LEFT_ALIGNMENT);

        balancePreview = new JLabel();
        balancePreview.setFont(AppTheme.BODY_FONT);
        balancePreview.setForeground(AppTheme.MUTED_TEXT);
        balancePreview.setAlignmentX(Component.LEFT_ALIGNMENT);

        pizzaComboBox.addActionListener(e -> updatePreview());
        formatComboBox.addActionListener(e -> updatePreview());

        // Ajout au formulaire
        formPanel.add(pizzaLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(pizzaComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(ingredientsLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(formatLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(formatComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(pricePreview);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(balancePreview);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppTheme.BACKGROUND);
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, formPanel.getPreferredSize().height));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(createMenuPanel());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        // --- BOUTON DE VALIDATION ---
        JButton orderButton = new JButton("Confirmer la commande");
        AppTheme.styleSuccessButton(orderButton);

        orderButton.addActionListener(e -> submitOrder());

        this.add(orderButton, BorderLayout.SOUTH);
        updatePreview();
    }

    private void submitOrder() {
        int selectedIndex = pizzaComboBox.getSelectedIndex();
        int selectedFormatIndex = formatComboBox.getSelectedIndex();
        User user = mobileApp.getConnectedUser();

        if (selectedIndex == -1 || selectedFormatIndex == -1 || user == null) {
            return;
        }

        Pizza selectedPizza = availablePizzas.get(selectedIndex);
        Format selectedFormat = availableFormats.get(selectedFormatIndex);

        // Envoi au serveur (simulé)
        boolean success = mobileApp.getServer().createOrder(user.id(), selectedPizza, selectedFormat);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Commande confirmée !\nVotre " + selectedPizza.name() + " arrive bientôt.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // Retourner à l'accueil
            mobileApp.setScreen(new HomeScreen(mobileApp));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Solde insuffisant pour cette commande. Approvisionnez votre compte avant de commander.",
                    "Commande refusée",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePreview() {
        int selectedPizzaIndex = pizzaComboBox.getSelectedIndex();
        int selectedFormatIndex = formatComboBox.getSelectedIndex();
        if (selectedPizzaIndex == -1 || selectedFormatIndex == -1) {
            return;
        }

        Pizza pizza = availablePizzas.get(selectedPizzaIndex);
        Format format = availableFormats.get(selectedFormatIndex);
        int price = mobileApp.getServer().calculatePrice(pizza, format);

        ingredientsLabel.setText("Ingrédients : " + Arrays.stream(pizza.ingredients())
                .map(Ingredient::nom)
                .collect(Collectors.joining(", ")));
        pricePreview.setText("Total : " + price + " €");
        int balance = mobileApp.getConnectedUser() != null
                ? mobileApp.getServer().getCustomerBalance(mobileApp.getConnectedUser().id())
                : 0;
        balancePreview.setText("Solde disponible : " + balance + " €");
        pricePreview.setForeground(balance >= price ? AppTheme.PRIMARY : AppTheme.WARNING);
    }

    private JComponent createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        AppTheme.styleCard(menuPanel);
        menuPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel title = new JLabel("Carte des pizzas");
        title.setFont(AppTheme.SECTION_FONT);
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Pizza pizza : availablePizzas) {
            menuPanel.add(createMenuItem(pizza));
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return menuPanel;
    }

    private JComponent createMenuItem(Pizza pizza) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(AppTheme.CARD);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(pizza.name() + " - prix de base : " + pizza.price() + " €");
        name.setFont(AppTheme.BODY_FONT);
        name.setForeground(AppTheme.PRIMARY);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ingredients = new JLabel("Ingrédients : " + Arrays.stream(pizza.ingredients())
                .map(Ingredient::nom)
                .collect(Collectors.joining(", ")));
        ingredients.setFont(AppTheme.SMALL_FONT);
        ingredients.setForeground(AppTheme.MUTED_TEXT);
        ingredients.setAlignmentX(Component.LEFT_ALIGNMENT);

        item.add(name);
        item.add(ingredients);
        return item;
    }
}