package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.IngredientName;
import fr.rhumain.seeders.references.VehiculeReference;
import fr.rhumain.structs.Vehicule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VehiculeSeeder implements DataSeeder {
    @Override
    public String getName() {
        return "Vehicules";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        String sql = "SELECT COUNT(*) FROM vehicules";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify vehicules", e);
        }
    }

    @Override
    public void seed() throws DAOException, SQLException {
        String sql = "INSERT INTO vehicules (brand, model) VALUES (?, ?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(VehiculeReference vehicule : VehiculeReference.values()) {
                stm.setString(1, vehicule.getBrand());
                stm.setString(2, vehicule.getModel());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Impossible to seed vehicules", e);
        }
    }
}
