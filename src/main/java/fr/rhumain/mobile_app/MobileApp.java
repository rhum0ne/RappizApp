package fr.rhumain.mobile_app;

import fr.rhumain.mobile_app.screens.ConnexionScreen;
import fr.rhumain.rappiz_server.ClientAppConnector;
import fr.rhumain.structs.User;
import fr.rhumain.ui.AppTheme;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MobileApp extends JFrame {

    @Getter
    private final ClientAppConnector server;
    private User connectedUser;

    private JPanel screen;

    public MobileApp(ClientAppConnector server) {
        this.server = server;

        this.setTitle("Rappiz - Client");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 700);

        this.setScreen(new ConnexionScreen(this));

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }


    public void styleButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        AppTheme.stylePrimaryButton(btn);
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

    public void setScreen(JPanel screen) {
        if(this.screen != null) this.remove(this.screen);
        this.screen = screen;
        this.add(this.screen);

        this.revalidate();
        this.repaint();
    }
}
