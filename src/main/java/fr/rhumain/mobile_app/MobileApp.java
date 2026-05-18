package fr.rhumain.mobile_app;

import fr.rhumain.mobile_app.screens.ConnexionScreen;
import fr.rhumain.rappiz_server.ClientAppConnector;
import fr.rhumain.structs.User;

import javax.swing.*;
import java.awt.*;

public class MobileApp extends JFrame {

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

    public void setScreen(JPanel screen) {
        if(this.screen != null) this.remove(this.screen);
        this.screen = screen;
        this.add(this.screen);

        this.revalidate();
    }
}
