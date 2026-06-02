package fr.rhumain.mobile_app.screens;

import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.structs.User;
import fr.rhumain.ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnexionScreen extends JPanel implements ActionListener {

    private final MobileApp app;
    private final JTextField emailField;
    private final JPasswordField passField;

    public ConnexionScreen(MobileApp app) {
        this.app = app;

        // Configuration du panneau principal
        setLayout(new GridBagLayout());
        setBackground(AppTheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panneau de la carte de connexion (le rectangle blanc)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        AppTheme.styleCard(card);

        // --- Titre ---
        JLabel title = new JLabel("Rappiz");
        title.setFont(AppTheme.TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(AppTheme.TEXT);

        JLabel subtitle = new JLabel("Espace client");
        subtitle.setFont(AppTheme.BODY_FONT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(AppTheme.MUTED_TEXT);

        // --- Champs de saisie ---
        emailField = createStyledTextField();
        emailField.setText("jean.dupont@rappiz.fr");
        passField = createStyledPasswordField();
        passField.setText("rappiz");

        // --- Bouton ---
        JButton loginBtn = new JButton("Se connecter");
        loginBtn.setActionCommand("connect");
        loginBtn.addActionListener(this);
        app.styleButton(loginBtn);

        // --- Assemblage ---
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(createLabel("Email"));
        card.add(emailField);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(createLabel("Mot de passe"));
        card.add(passField);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(createHintLabel("Compte de démo : jean.dupont@rappiz.fr / rappiz"));

        // Ajout de la carte au centre du GridBagLayout
        add(card);
    }

    // Méthode utilitaire pour les labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.SMALL_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setForeground(AppTheme.MUTED_TEXT);
        return label;
    }

    // Méthode pour styliser le champ texte
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(AppTheme.BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(AppTheme.BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JLabel createHintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.SMALL_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(AppTheme.MUTED_TEXT);
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passField.getPassword());

        User user = app.getServer()
                .authenticateUser(email, password)
                .orElse(null);

        if (user == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Identifiants incorrects. Utilisez le compte de démo affiché.",
                    "Connexion impossible",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        app.setConnectedUser(user);
        app.setScreen(new HomeScreen(app));
    }
}
