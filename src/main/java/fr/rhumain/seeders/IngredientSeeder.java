package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.IngredientName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class IngredientSeeder implements DataSeeder{

    @Override
    public String getName() {
        return "Ingredients";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        String sql = "SELECT COUNT(*) FROM ingredients";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify ingredients", e);
        }
    }

    @Override
    public void seed() throws DAOException {
        String sql = "INSERT INTO ingredients (name) VALUES (?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(IngredientName ingredient : IngredientName.values()) {
                stm.setString(1, ingredient.getName());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Impossible to seed ingredients", e);
        }
    }
}
