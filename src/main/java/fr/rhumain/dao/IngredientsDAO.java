package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Ingredient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class IngredientsDAO implements DAO<Ingredient, Integer> {

    @Override
    public Optional<Ingredient> findById(Integer id) throws DAOException {
        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() throws DAOException {
        return List.of();
    }

    @Override
    public Ingredient save(Ingredient entity) throws DAOException {
        return null;
    }

    @Override
    public void update(Ingredient entity) throws DAOException {

    }

    @Override
    public void delete(Ingredient entity) throws DAOException {

    }

    public Optional<Ingredient> findByName(String name) throws DAOException {
        String sql = "SELECT * FROM ingredients WHERE name='"+name+"'";
        try(Statement con = ConnectionManager.getConnection().createStatement();
            ResultSet rq = con.executeQuery(sql)) {

            return rq.next() ? Optional.of(new Ingredient(rq.getInt("id"), rq.getString("name"))) : Optional.empty();
        } catch(SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find ingredient with the name " + name, e);
        }
    }
}
