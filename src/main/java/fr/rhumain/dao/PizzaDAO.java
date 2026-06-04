package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Ingredient;
import fr.rhumain.structs.Pizza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PizzaDAO implements DAO<Pizza, Integer> {

    // Jointure triple : pizzas → pizza_ingredients → ingredients
    private static final String SQL_FIND_BY_ID =
            "SELECT p.id, p.name, p.price, i.id as ing_id, i.name as ing_name " +
                    "FROM pizzas p " +
                    "LEFT JOIN pizza_ingredients pi ON p.id = pi.id_pizza " +
                    "LEFT JOIN ingredients i ON pi.id_ingredient = i.id " +
                    "WHERE p.id=?";

    private static final String SQL_FIND_ALL =
            "SELECT p.id, p.name, p.price, i.id as ing_id, i.name as ing_name " +
                    "FROM pizzas p " +
                    "LEFT JOIN pizza_ingredients pi ON p.id = pi.id_pizza " +
                    "LEFT JOIN ingredients i ON pi.id_ingredient = i.id " +
                    "ORDER BY p.id";

    private static final String SQL_INSERT_PIZZA =
            "INSERT INTO pizzas (name, price) VALUES (?, ?)";

    private static final String SQL_INSERT_PIZZA_INGREDIENT =
            "INSERT INTO pizza_ingredients (id_pizza, id_ingredient) VALUES (?, ?)";

    private static final String SQL_DELETE_PIZZA_INGREDIENTS =
            "DELETE FROM pizza_ingredients WHERE id_pizza=?";

    private static final String SQL_UPDATE =
            "UPDATE pizzas SET name=?, price=? WHERE id=?";

    private static final String SQL_DELETE =
            "DELETE FROM pizzas WHERE id=?";

    private static final String SQL_FIND_BY_NAME =
            "SELECT p.id, p.name, p.price, i.id as ing_id, i.name as ing_name " +
                    "FROM pizzas p " +
                    "LEFT JOIN pizza_ingredients pi ON p.id = pi.id_pizza " +
                    "LEFT JOIN ingredients i ON pi.id_ingredient = i.id " +
                    "WHERE p.name=?";

    @Override
    public Optional<Pizza> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return buildPizzasFromResultSet(rs).stream().findFirst();
            }
        } catch (SQLException e) {
            throw new DAOException("[PizzaDAO] Impossible to find pizza by id", e);
        }
    }

    @Override
    public List<Pizza> findAll() throws DAOException {
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            return buildPizzasFromResultSet(rs);
        } catch (SQLException e) {
            throw new DAOException("[PizzaDAO] Impossible to find all pizzas", e);
        }
    }

    @Override
    public Pizza save(Pizza entity) throws DAOException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            int newId;
            try (PreparedStatement stm = conn.prepareStatement(SQL_INSERT_PIZZA, Statement.RETURN_GENERATED_KEYS)) {
                stm.setString(1, entity.name());
                stm.setInt(2, entity.price());
                stm.executeUpdate();
                try (ResultSet keys = stm.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new DAOException("[PizzaDAO] No generated key returned when saving pizza");
                    }
                    newId = keys.getInt(1);
                }
            }

            insertIngredients(conn, newId, entity.ingredients());
            conn.commit();
            return new Pizza(newId, entity.name(), entity.price(), entity.ingredients());

        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DAOException("[PizzaDAO] Impossible to save pizza", e);
        } finally {
            resetAutoCommit(conn);
        }
    }

    @Override
    public void update(Pizza entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[PizzaDAO] Impossible to update pizza with null id");
        }
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stm = conn.prepareStatement(SQL_UPDATE)) {
                stm.setString(1, entity.name());
                stm.setInt(2, entity.price());
                stm.setInt(3, entity.id());
                int rows = stm.executeUpdate();
                if (rows == 0) {
                    throw new DAOException("[PizzaDAO] No pizza found with id " + entity.id());
                }
            }

            // Remplacement complet des ingrédients (delete + insert)
            try (PreparedStatement stm = conn.prepareStatement(SQL_DELETE_PIZZA_INGREDIENTS)) {
                stm.setInt(1, entity.id());
                stm.executeUpdate();
            }
            insertIngredients(conn, entity.id(), entity.ingredients());

            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DAOException("[PizzaDAO] Impossible to update pizza", e);
        } finally {
            resetAutoCommit(conn);
        }
    }

    @Override
    public void delete(Pizza entity) throws DAOException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stm = conn.prepareStatement(SQL_DELETE_PIZZA_INGREDIENTS)) {
                stm.setInt(1, entity.id());
                stm.executeUpdate();
            }
            try (PreparedStatement stm = conn.prepareStatement(SQL_DELETE)) {
                stm.setInt(1, entity.id());
                int rows = stm.executeUpdate();
                if (rows == 0) {
                    throw new DAOException("[PizzaDAO] No pizza found with id " + entity.id());
                }
            }
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DAOException("[PizzaDAO] Impossible to delete pizza", e);
        } finally {
            resetAutoCommit(conn);
        }
    }

    public Optional<Pizza> findByName(String name) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_NAME)) {
            stm.setString(1, name);
            try (ResultSet rs = stm.executeQuery()) {
                return buildPizzasFromResultSet(rs).stream().findFirst();
            }
        } catch (SQLException e) {
            throw new DAOException("[PizzaDAO] Impossible to find pizza by name", e);
        }
    }

    // --- Helpers ---

    /**
     * Regroupe les lignes du ResultSet (une par ingrédient) en objets Pizza complets.
     * Maintient l'ordre d'insertion grâce à LinkedHashMap.
     */
    private List<Pizza> buildPizzasFromResultSet(ResultSet rs) throws SQLException {
        Map<Integer, Pizza> pizzaMap = new LinkedHashMap<>();
        Map<Integer, List<Ingredient>> ingredientMap = new LinkedHashMap<>();

        while (rs.next()) {
            int pizzaId = rs.getInt("id");

            if (!pizzaMap.containsKey(pizzaId)) {
                pizzaMap.put(pizzaId, new Pizza(pizzaId, rs.getString("name"), rs.getInt("price"), new Ingredient[]{}));
                ingredientMap.put(pizzaId, new ArrayList<>());
            }

            int ingId = rs.getInt("ing_id");
            if (!rs.wasNull()) {
                ingredientMap.get(pizzaId).add(new Ingredient(ingId, rs.getString("ing_name")));
            }
        }

        List<Pizza> result = new ArrayList<>();
        for (Map.Entry<Integer, Pizza> entry : pizzaMap.entrySet()) {
            Pizza p = entry.getValue();
            result.add(new Pizza(
                    p.id(),
                    p.name(),
                    p.price(),
                    ingredientMap.get(p.id()).toArray(new Ingredient[0])
            ));
        }
        return result;
    }

    private void insertIngredients(Connection conn, int pizzaId, Ingredient[] ingredients) throws SQLException {
        try (PreparedStatement stm = conn.prepareStatement(SQL_INSERT_PIZZA_INGREDIENT)) {
            for (Ingredient ingredient : ingredients) {
                stm.setInt(1, pizzaId);
                stm.setInt(2, ingredient.id());
                stm.addBatch();
            }
            stm.executeBatch();
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ignored) {}
    }

    private void resetAutoCommit(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ignored) {}
    }
}