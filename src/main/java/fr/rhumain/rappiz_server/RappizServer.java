package fr.rhumain.rappiz_server;

import lombok.Getter;

@Getter
public class RappizServer {

    private final DashboardAppConnector dashboardAppConnector = new DashboardAppConnector();
    private final ClientAppConnector clientAppConnector = new ClientAppConnector();

    public RappizServer() {

    }
}
