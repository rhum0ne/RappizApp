package fr.rhumain.rappiz_server;

import fr.rhumain.dao.IngredientsDAO;
import fr.rhumain.seeders.FormatSeeder;
import fr.rhumain.seeders.IngredientSeeder;
import fr.rhumain.seeders.PizzasSeeder;
import fr.rhumain.seeders.SeederRunner;
import lombok.Getter;

import java.util.List;

@Getter
public class RappizServer {

    private final DashboardAppConnector dashboardAppConnector = new DashboardAppConnector();
    private final ClientAppConnector clientAppConnector = new ClientAppConnector();
    private SeederRunner seederRunner;
    private IngredientsDAO ingredientsDAO;

    public RappizServer() {
        this.ingredientsDAO = new IngredientsDAO();
        this.seederRunner = new SeederRunner(
                List.of(
                        new FormatSeeder(),
                        new IngredientSeeder(),
                        new PizzasSeeder(ingredientsDAO)
                )
        );
        this.seederRunner.runAll();
    }
}
