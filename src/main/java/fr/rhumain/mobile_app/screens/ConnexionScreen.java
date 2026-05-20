package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnexionScreen extends JPanel implements ActionListener {

    private final MobileApp app;

    public ConnexionScreen(MobileApp app) {
        this.app = app;

        // Configuration du panneau principal
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245)); // Gris très clair
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panneau de la carte de connexion (le rectangle blanc)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // --- Titre ---
        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));

        // --- Champs de saisie ---
        JTextField userField = createStyledTextField("Nom d'utilisateur");
        JPasswordField passField = createStyledPasswordField();

        // --- Bouton ---
        JButton loginBtn = new JButton("Se connecter");
        loginBtn.setActionCommand("connect");
        loginBtn.addActionListener(this);
        app.styleButton(loginBtn);

        // --- Assemblage ---
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25))); // Espacement
        card.add(createLabel("Utilisateur"));
        card.add(userField);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(createLabel("Mot de passe"));
        card.add(passField);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(loginBtn);

        // Ajout de la carte au centre du GridBagLayout
        add(card);
    }

    // Méthode utilitaire pour les labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(new Color(100, 100, 100));
        return label;
    }

    // Méthode pour styliser le champ texte
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(15);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.app.setScreen(new HomeScreen(app));
    }
}
