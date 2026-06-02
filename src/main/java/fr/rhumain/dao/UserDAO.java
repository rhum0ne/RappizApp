package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User, Integer> {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM users WHERE id=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM users";
    private static final String SQL_INSERT =
            "INSERT INTO users (first_name, last_name, email, password, balance) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE users SET first_name=?, last_name=?, email=?, password=?, balance=? WHERE id=?";
    private static final String SQL_DELETE = "DELETE FROM users WHERE id=?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM users WHERE email=?";
    private static final String SQL_UPDATE_BALANCE = "UPDATE users SET balance=? WHERE id=?";

    @Override
    public Optional<User> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to find user by id", e);
        }
    }

    @Override
    public List<User> findAll() throws DAOException {
        List<User> users = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to find all users", e);
        }
    }

    @Override
    public User save(User entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, entity.firstName());
            stm.setString(2, entity.lastName());
            stm.setString(3, entity.email());
            stm.setString(4, entity.password());
            stm.setDouble(5, entity.balance());
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), entity.firstName(), entity.lastName(),
                            entity.email(), entity.password(), entity.balance());
                }
                throw new DAOException("[UserDAO] No generated key returned when saving user");
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to save user", e);
        }
    }

    @Override
    public void update(User entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[UserDAO] Impossible to update user with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setString(1, entity.firstName());
            stm.setString(2, entity.lastName());
            stm.setString(3, entity.email());
            stm.setString(4, entity.password());
            stm.setDouble(5, entity.balance());
            stm.setInt(6, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[UserDAO] No user found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to update user", e);
        }
    }

    @Override
    public void delete(User entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[UserDAO] No user found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to delete user", e);
        }
    }

    public Optional<User> findByEmail(String email) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_EMAIL)) {
            stm.setString(1, email);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to find user by email", e);
        }
    }

    /**
     * Met à jour uniquement le solde du compte — opération fréquente dans RaPizz.
     */
    public void updateBalance(Integer id, double newBalance) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE_BALANCE)) {
            stm.setDouble(1, newBalance);
            stm.setInt(2, id);
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[UserDAO] No user found with id " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("[UserDAO] Impossible to update balance", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getInt("balance")
        );
    }
}
