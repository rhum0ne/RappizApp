package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Ingredient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class IngredientsDAO {
    public Optional<Ingredient> findByName(String name) throws DAOException {
        String sql = "SELECT * FROM ingredients WHERE name=" + name;
        try(Statement con = ConnectionManager.getConnection().createStatement();
            ResultSet rq = con.executeQuery(sql)) {
            return rq.next() ? Optional.of(new Ingredient(rq.getString("name"))) : Optional.empty();
        } catch(SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find ingredient with the name " + name);
        }
    }
}
