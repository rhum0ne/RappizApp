package fr.rhumain.rappiz_server;

import fr.rhumain.dao.IngredientsDAO;
import fr.rhumain.dao.VehiculeDAO;
import fr.rhumain.seeders.*;
import lombok.Getter;

import java.util.List;

@Getter
public class RappizServer {

    private final RappizDataStore dataStore = new RappizDataStore();
    private final DashboardAppConnector dashboardAppConnector = new DashboardAppConnector(dataStore);
    private final ClientAppConnector clientAppConnector = new ClientAppConnector(dataStore);
    private SeederRunner seederRunner;
    private IngredientsDAO ingredientsDAO;
    private VehiculeDAO vehiculeDAO;

    public RappizServer() {
        this.ingredientsDAO = new IngredientsDAO();
        this.vehiculeDAO = new VehiculeDAO();
        this.seederRunner = new SeederRunner(
                List.of(
                        new FormatSeeder(),
                        new IngredientSeeder(),
                        new PizzasSeeder(this.ingredientsDAO),
                        new VehiculeSeeder(),
                        new LivreurSeeder(this.vehiculeDAO)
                )
        );
        this.seederRunner.runAll();
    }
}
