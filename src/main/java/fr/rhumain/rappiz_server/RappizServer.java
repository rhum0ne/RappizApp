package fr.rhumain.rappiz_server;

import fr.rhumain.seeders.FormatSeeder;
import fr.rhumain.seeders.IngredientSeeder;
import fr.rhumain.seeders.SeederRunner;
import lombok.Getter;

import java.util.List;

@Getter
public class RappizServer {

    private final RappizDataStore dataStore = new RappizDataStore();
    private final DashboardAppConnector dashboardAppConnector = new DashboardAppConnector(dataStore);
    private final ClientAppConnector clientAppConnector = new ClientAppConnector(dataStore);
    private SeederRunner seederRunner;

    public RappizServer() {
        this.seederRunner = new SeederRunner(
                List.of(
                        new FormatSeeder(),
                        new IngredientSeeder()
                )
        );
        this.seederRunner.runAll();
    }
}
