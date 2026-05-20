package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.Format;
import fr.rhumain.structs.Pizza;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderScreen extends JPanel {

    private final MobileApp mobileApp;
    private JComboBox<String> pizzaComboBox;
    private JRadioButton classiqueRadio;
    private JRadioButton largeRadio;
    private List<Pizza> availablePizzas;

    public OrderScreen(MobileApp mobileApp) {
        this.mobileApp = mobileApp;
        this.setLayout(new BorderLayout(0, 20)); // Espacement
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JButton backButton = new JButton("← Retour");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        mobileApp.styleButton(backButton);
        backButton.addActionListener(e -> mobileApp.setScreen(new HomeScreen(mobileApp))); // Supposant que tu as une méthode showScreen

        JLabel title = new JLabel("Composer votre commande");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(backButton);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(title);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- FORMULAIRE ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // 1. Choix de la Pizza
        JLabel pizzaLabel = new JLabel("1. Choisissez votre pizza :");
        pizzaLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Récupération des pizzas depuis le connecteur
        availablePizzas = mobileApp.getServer().getPizzas();
        String[] pizzaNames = availablePizzas.stream()
                .map(p -> p.name() + " (" + p.price() + "€)")
                .toArray(String[]::new);

        pizzaComboBox = new JComboBox<>(pizzaNames);
        pizzaComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pizzaComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Choix du Format
        JLabel formatLabel = new JLabel("2. Choisissez la taille :");
        formatLabel.setFont(new Font("Arial", Font.BOLD, 14));

        classiqueRadio = new JRadioButton("Classique (Prix normal)");
        largeRadio = new JRadioButton("Large (+30%)");
        classiqueRadio.setSelected(true); // Sélection par défaut

        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(classiqueRadio);
        formatGroup.add(largeRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.add(classiqueRadio);
        radioPanel.add(largeRadio);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ajout au formulaire
        formPanel.add(pizzaLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(pizzaComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(formatLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(radioPanel);

        this.add(formPanel, BorderLayout.CENTER);

        // --- BOUTON DE VALIDATION ---
        JButton orderButton = new JButton("Confirmer la commande");
        orderButton.setFont(new Font("Arial", Font.BOLD, 16));
        orderButton.setBackground(new Color(46, 204, 113)); // Vert
        orderButton.setForeground(Color.WHITE);
        orderButton.setFocusPainted(false);

        orderButton.addActionListener(e -> submitOrder());

        this.add(orderButton, BorderLayout.SOUTH);
    }

    private void submitOrder() {
        int selectedIndex = pizzaComboBox.getSelectedIndex();
        if (selectedIndex == -1) return;

        Pizza selectedPizza = availablePizzas.get(selectedIndex);

        // Création du format selon le choix
        Format selectedFormat;
        if (largeRadio.isSelected()) {
            selectedFormat = new Format("Large", 130);
        } else {
            selectedFormat = new Format("Classique", 100);
        }

        // Simuler un ID utilisateur (ex: 1)
        int idUser = 1;

        // Envoi au serveur (simulé)
        boolean success = mobileApp.getServer().createOrder(idUser, selectedPizza, selectedFormat);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Commande confirmée !\nVotre " + selectedPizza.name() + " arrive bientôt.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            // Retourner à l'accueil
            mobileApp.setScreen(new HomeScreen(mobileApp));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la commande. Veuillez réessayer.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}