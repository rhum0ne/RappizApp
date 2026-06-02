package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Vehicule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class VehiculeDAO implements DAO<Vehicule, Integer> {
    @Override
    public Optional<Vehicule> findById(Integer id) throws DAOException {
        return Optional.empty();
    }

    @Override
    public List<Vehicule> findAll() throws DAOException {
        return List.of();
    }

    @Override
    public Vehicule save(Vehicule entity) throws DAOException {
        return null;
    }

    @Override
    public void update(Vehicule entity) throws DAOException {

    }

    @Override
    public void delete(Vehicule entity) throws DAOException {

    }

    public Optional<Vehicule> findByBrandAndModel(String brand, String model) throws DAOException {
        String sql = "SELECT * FROM vehicules WHERE brand='" + brand + "' AND model='" + model + "'";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() ?
                    Optional.of(new Vehicule(rq.getInt("id"), rq.getString("brand"), rq.getString("model"))) :
                    Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to find vehicule by brand and model", e);
        }
    }
}
