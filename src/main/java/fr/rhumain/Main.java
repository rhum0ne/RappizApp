package fr.rhumain;

import fr.rhumain.config.DatabaseConfig;
import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.dashboard_app.DashboardApp;
import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.rappiz_server.RappizServer;

import javax.swing.*;

public class Main {

    static void main(String[] args) {
        System.out.println("=== DEBUG CLASSPATH ===");
        System.out.println("Working dir : " + System.getProperty("user.dir"));
        System.out.println("Classpath   : " + System.getProperty("java.class.path"));
        System.out.println("Root URL    : " + DatabaseConfig.class.getResource("/"));
        System.out.println("File URL /  : " + DatabaseConfig.class.getResource("/database.properties"));
        System.out.println("File URL    : " + DatabaseConfig.class.getResource("database.properties"));

        if(!ConnectionManager.test()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Impossible to connect to database\nVerify if MySQL is launched (MySQL80 service)\nConnection Error.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE,
                    null);
            System.exit(1);
        }

        System.out.println("Connected to database");

        RappizServer server = new RappizServer();

        new DashboardApp(server.getDashboardAppConnector());
        new MobileApp(server.getClientAppConnector());
    }
}