package fr.rhumain.mobile_app;

import fr.rhumain.mobile_app.screens.ConnexionScreen;
import fr.rhumain.rappiz_server.ClientAppConnector;
import fr.rhumain.structs.User;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MobileApp extends JFrame {

    @Getter
    private final ClientAppConnector server;
    private User connectedUser;

    private JPanel screen;

    public MobileApp(ClientAppConnector server) {
        this.server = server;

        this.setTitle("Mobile App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,   700);

        this.setScreen(new ConnexionScreen(this));

        this.setVisible(true);
        this.setResizable(false);
    }


    public void styleButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(63, 81, 181)); // Bleu Indigo
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    public void setScreen(JPanel screen) {
        if(this.screen != null) this.remove(this.screen);
        this.screen = screen;
        this.add(this.screen);

        this.revalidate();
    }
}
