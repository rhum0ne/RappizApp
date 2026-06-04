package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Vehicule;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private static final String SQL_UNUSED_VEHICLES = "{CALL sp_unused_vehicles()}";

    public List<Vehicule> findUnusedVehicles() throws DAOException {
        List<Vehicule> vehicles = new ArrayList<>();
        try (CallableStatement stm = ConnectionManager.getConnection().prepareCall(SQL_UNUSED_VEHICLES);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                vehicles.add(new Vehicule(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model")
                ));
            }
            return vehicles;
        } catch (SQLException e) {
            throw new DAOException("[ReportDAO] Impossible to call sp_unused_vehicles", e);
        }
    }
}
