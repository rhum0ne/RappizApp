package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Livreur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivreurDAO implements DAO<Livreur, Integer> {

    private static final String SQL_FIND_BY_ID =
            "SELECT * FROM delivers WHERE id=?";

    private static final String SQL_FIND_ALL =
            "SELECT * FROM delivers";

    private static final String SQL_INSERT =
            "INSERT INTO delivers (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE delivers SET first_name=?, last_name=?, email=?, password=? WHERE id=?";

    private static final String SQL_DELETE = "DELETE FROM delivers WHERE id=?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT * FROM delivers WHERE email=?";

    @Override
    public Optional<Livreur> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to find livreur by id", e);
        }
    }

    @Override
    public List<Livreur> findAll() throws DAOException {
        List<Livreur> livreurs = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                livreurs.add(mapRow(rs));
            }
            return livreurs;
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to find all livreurs", e);
        }
    }

    @Override
    public Livreur save(Livreur entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, entity.firstName());
            stm.setString(2, entity.lastName());
            stm.setString(3, entity.email());
            stm.setString(4, entity.password());
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Livreur(keys.getInt(1), entity.firstName(), entity.lastName(),
                            entity.email(), entity.password());
                }
                throw new DAOException("[LivreurDAO] No generated key returned when saving livreur");
            }
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to save livreur", e);
        }
    }

    @Override
    public void update(Livreur entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[LivreurDAO] Impossible to update livreur with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setString(1, entity.firstName());
            stm.setString(2, entity.lastName());
            stm.setString(3, entity.email());
            stm.setString(4, entity.password());
            stm.setInt(5, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[LivreurDAO] No livreur found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to update livreur", e);
        }
    }

    @Override
    public void delete(Livreur entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[LivreurDAO] No livreur found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to delete livreur", e);
        }
    }

    public Optional<Livreur> findByEmail(String email) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_EMAIL)) {
            stm.setString(1, email);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[LivreurDAO] Impossible to find livreur by email", e);
        }
    }

    private Livreur mapRow(ResultSet rs) throws SQLException {
        return new Livreur(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
