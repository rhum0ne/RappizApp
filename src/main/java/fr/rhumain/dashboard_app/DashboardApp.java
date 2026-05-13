package fr.rhumain.dashboard_app;

import fr.rhumain.rappiz_server.DashboardAppConnector;

import javax.swing.*;

public class DashboardApp extends JFrame{

    private final DashboardAppConnector server;

    public DashboardApp(DashboardAppConnector server) {
        this.server = server;

        this.setTitle("Dashboard App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900,700);

        this.setResizable(false);
        this.setVisible(true);
    }
}
