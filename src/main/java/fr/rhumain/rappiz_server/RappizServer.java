package fr.rhumain.rappiz_server;

import fr.rhumain.dao.FormatDAO;
import fr.rhumain.dao.IngredientsDAO;
import fr.rhumain.dao.LivreurDAO;
import fr.rhumain.dao.OrderDAO;
import fr.rhumain.dao.PizzaDAO;
import fr.rhumain.dao.ReceiptDAO;
import fr.rhumain.dao.ReportDAO;
import fr.rhumain.dao.UserDAO;
import fr.rhumain.dao.VehiculeDAO;
import fr.rhumain.seeders.*;
import lombok.Getter;

import java.util.List;

@Getter
public class RappizServer {

    private final PizzaDAO pizzaDAO = new PizzaDAO();
    private final FormatDAO formatDAO = new FormatDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ReceiptDAO receiptDAO = new ReceiptDAO();
    private final LivreurDAO livreurDAO = new LivreurDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final IngredientsDAO ingredientsDAO = new IngredientsDAO();
    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();

    private final DashboardAppConnector dashboardAppConnector = new DashboardAppConnector(orderDAO, userDAO, vehiculeDAO, livreurDAO, pizzaDAO, reportDAO, receiptDAO);
    private final ClientAppConnector clientAppConnector = new ClientAppConnector(pizzaDAO, formatDAO, userDAO, orderDAO, receiptDAO);
    private SeederRunner seederRunner;

    public RappizServer() {
        this.seederRunner = new SeederRunner(
                List.of(
                        new FormatSeeder(),
                        new IngredientSeeder(),
                        new PizzasSeeder(this.ingredientsDAO),
                        new VehiculeSeeder(),
                        new LivreurSeeder(),
                        new UserSeeder()
                )
        );
        this.seederRunner.runAll();
    }
}
