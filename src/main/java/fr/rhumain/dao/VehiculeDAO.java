package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Vehicule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiculeDAO implements DAO<Vehicule, Integer> {
    private static final String SQL_FIND_BY_ID = "SELECT * FROM vehicules WHERE id=?";

    private static final String SQL_FIND_ALL = "SELECT * FROM vehicules";

    private static final String SQL_INSERT = "INSERT INTO vehicules (brand, model) VALUES (?, ?)";

    private static final String SQL_UPDATE = "UPDATE vehicules SET brand=?, model=? WHERE id=?";

    private static final String SQL_DELETE = "DELETE FROM vehicules WHERE id=?";

    private static final String SQL_FIND_BY_BRAND_AND_MODEL = "SELECT * FROM vehicules WHERE brand=? AND model=?";

    @Override
    public Optional<Vehicule> findById(Integer id) throws DAOException {
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try(ResultSet rs = stm.executeQuery()) {
                return rs.next() ?
                        Optional.of(new Vehicule(rs.getInt("id"), rs.getString("brand"), rs.getString("model"))) :
                        Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to find vehicule by id ", e);
        }
    }

    @Override
    public List<Vehicule> findAll() throws DAOException {
        List<Vehicule> vehicules = new ArrayList<>();
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while(rs.next()) {
                Vehicule vehicule = mapRow(rs);
                vehicules.add(vehicule);
            }
            return vehicules;
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to find all vehicules ", e);
        }
    }

    @Override
    public Vehicule save(Vehicule entity) throws DAOException {
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, entity.brand());
            stm.setString(2, entity.model());
            stm.executeUpdate();
            try(ResultSet keys = stm.getGeneratedKeys()) {
                if(keys.next()) {
                    return new Vehicule(keys.getInt("id"), entity.brand(), entity.model());
                }
            }
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to save vehicule ", e);
        }
    }

    @Override
    public void update(Vehicule entity) throws DAOException {
        if(entity.id() == null) {
            throw new DAOException("[VehiculeDAO] Impossible to update vehicule with null id");
        }
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setString(1, entity.brand());
            stm.setString(2, entity.model());
            stm.setInt(3, entity.id());
            int rows = stm.executeUpdate();
            if(rows == 0) {
                throw new DAOException("[VehiculeDAO] No vehicule found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to update vehicule ", e);
        }
    }

    @Override
    public void delete(Vehicule entity) throws DAOException {
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if(rows == 0) {
                throw new DAOException("[VehiculeDAO] No vehicle found with id " + entity.id());
            }
        } catch(SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to delete vehicle ", e);
        }
    }

    public Optional<Vehicule> findByBrandAndModel(String brand, String model) throws DAOException {
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_BRAND_AND_MODEL)) {
            stm.setString(1, brand);
            stm.setString(2, model);
            try(ResultSet rs = stm.executeQuery()) {
                return rs.next() ?
                        Optional.of(new Vehicule(rs.getInt("id"), rs.getString("brand"), rs.getString("model"))) :
                        Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[VehiculeDAO] Impossible to find vehicule by brand and model", e);
        }
    }

    private Vehicule mapRow(ResultSet rs) throws SQLException {
        return new Vehicule(
                rs.getInt("id"),
                rs.getString("brand"),
                rs.getString("model")
        );
    }
}
