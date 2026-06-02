package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Ingredient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngredientsDAO implements DAO<Ingredient, Integer> {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM ingredients WHERE id=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM ingredients";
    private static final String SQL_INSERT = "INSERT INTO ingredients (name) VALUES (?)";
    private static final String SQL_UPDATE = "UPDATE ingredients SET name=? WHERE id=?";
    private static final String SQL_DELETE = "DELETE FROM ingredients WHERE id=?";
    private static final String SQL_FIND_BY_NAME = "SELECT * FROM ingredients WHERE name=?";

    @Override
    public Optional<Ingredient> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find ingredient by id", e);
        }
    }

    @Override
    public List<Ingredient> findAll() throws DAOException {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                ingredients.add(mapRow(rs));
            }
            return ingredients;
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find all ingredients", e);
        }
    }

    @Override
    public Ingredient save(Ingredient entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, entity.nom());
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Ingredient(keys.getInt(1), entity.nom());
                }
                throw new DAOException("[IngredientDAO] No generated key returned when saving ingredient");
            }
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to save ingredient", e);
        }
    }

    @Override
    public void update(Ingredient entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[IngredientDAO] Impossible to update ingredient with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setString(1, entity.nom());
            stm.setInt(2, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[IngredientDAO] No ingredient found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to update ingredient", e);
        }
    }

    @Override
    public void delete(Ingredient entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[IngredientDAO] No ingredient found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to delete ingredient", e);
        }
    }

    public Optional<Ingredient> findByName(String name) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_NAME)) {
            stm.setString(1, name);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find ingredient by name", e);
        }
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
