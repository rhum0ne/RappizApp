package fr.rhumain.seeders;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Pizza;

import java.util.List;

public class PizzasSeeder implements DataSeeder {


    @Override
    public String getName() {
        return "Pizzas";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        return false;
    }

    @Override
    public void seed() throws DAOException {

    }
}
