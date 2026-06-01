package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.dao.IngredientsDAO;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.IngredientName;
import fr.rhumain.seeders.references.PizzaDefinition;
import fr.rhumain.seeders.references.PizzaDefinitions;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Pizza;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PizzasSeeder implements DataSeeder {
    private final IngredientsDAO ingredientsDAO;

    public PizzasSeeder(IngredientsDAO ingredientsDAO) {
        this.ingredientsDAO = ingredientsDAO;
    }

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
        String sql = "INSERT INTO pizzas (name, price) VALUES (?, ?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(PizzaDefinition pizzaDef : PizzaDefinitions.ALL) {
                stm.setString(1, pizzaDef.name());
                stm.setInt(2, pizzaDef.price());
            }
        } catch (SQLException e) {
            throw new DAOException("[PizzasDAO] Impossible to seed pizzas");
        }
    }

    private Map<IngredientName, Ingredient> loadIngredientsCache() throws DAOException {
        Map<IngredientName, Ingredient> ingredients = new EnumMap<>(IngredientName.class);
        for(IngredientName ingredientName : IngredientName.values()) {
            Ingredient ingredient = this.ingredientsDAO.findByName(ingredientName.getName()).orElseThrow(
                    () -> new DAOException("[PizzasSeeder] Database ingredient missing : " + ingredientName.getName() + "\nRun IngredientSeeder before")
            );
            ingredients.put(ingredientName, ingredient);
        }
        return ingredients;
    }
}
