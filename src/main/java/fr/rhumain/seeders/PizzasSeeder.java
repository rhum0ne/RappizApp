package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.dao.IngredientsDAO;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.IngredientName;
import fr.rhumain.seeders.references.PizzaDefinition;
import fr.rhumain.seeders.references.PizzaDefinitions;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Pizza;

import java.sql.*;
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
        String sql = "SELECT COUNT(*) FROM pizzas";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify pizzas", e);
        }
    }

    @Override
    public void seed() throws DAOException, SQLException {
        String sql = "INSERT INTO pizzas (name, price) VALUES (?, ?)";
        Map<IngredientName, Ingredient> ingredientsByName = loadIngredientsCache();
        Connection conn = ConnectionManager.getConnection();
        try {
            conn.setAutoCommit(false);
            for(PizzaDefinition pizzaDef : PizzaDefinitions.ALL) {
                int pizzaId = insertPizza(conn, pizzaDef);
                insertPizzaIngredients(conn, pizzaId, pizzaDef, ingredientsByName);
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {}
            throw new DAOException("[PizzasDAO] Impossible to seed pizzas", e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {}
        }
    }

    private int insertPizza(Connection conn, PizzaDefinition pizzaDef) throws SQLException {
        String sql = "INSERT INTO pizzas (name, price) VALUES (?, ?)";
        PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stm.setString(1, pizzaDef.name());
        stm.setInt(2, pizzaDef.price());
        stm.executeUpdate();
        try (ResultSet keys = stm.getGeneratedKeys()) {
            if (keys.next()) return keys.getInt(1);
            throw new SQLException("Aucun ID genere pour la pizza " + pizzaDef.name());
        }
    }

    private void insertPizzaIngredients(Connection conn, int pizzaId, PizzaDefinition pizzaDef, Map<IngredientName, Ingredient> ingredientsByName) throws SQLException {
        String sql = "INSERT INTO pizza_ingredients (id_pizza, id_ingredient) VALUES (?, ?)";
        try(PreparedStatement stm = conn.prepareStatement(sql)) {
            for(IngredientName name : pizzaDef.ingredients()) {
                stm.setInt(1, pizzaId);
                stm.setInt(2, ingredientsByName.get(name).id());
                stm.addBatch();
            }
            stm.executeBatch();
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
