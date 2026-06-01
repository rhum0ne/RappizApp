package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IngredientsDAO {
    public static String findByName(String name) throws DAOException {
        String sql = "SELECT * FROM ingredients WHERE name = " + name;
        try(Statement con = ConnectionManager.getConnection().createStatement();
            ResultSet rq = con.executeQuery(sql)) {
            if(rq.next()) {
                return rq.getString("name");
            }
            return null;
        } catch(SQLException e) {
            throw new DAOException("[IngredientDAO] Impossible to find ingredient with the name " + name);
        }
    }
}
