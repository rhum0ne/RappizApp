package fr.rhumain;

import fr.rhumain.dashboard_app.DashboardApp;
import fr.rhumain.mobile_app.MobileApp;
import fr.rhumain.rappiz_server.RappizServer;

public class Main {

    static void main(String[] args) {
        RappizServer server = new RappizServer();

        new DashboardApp(server.getDashboardAppConnector());
        new MobileApp(server.getClientAppConnector());
    }
}