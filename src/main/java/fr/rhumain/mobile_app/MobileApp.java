package fr.rhumain.mobile_app;

import fr.rhumain.rappiz_server.ClientAppConnector;

import javax.swing.*;

public class MobileApp extends JFrame {

    private final ClientAppConnector server;

    public MobileApp(ClientAppConnector server) {
        this.server = server;

        this.setTitle("Mobile App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,   700);

        this.setVisible(true);
        this.setResizable(false);
    }
}
