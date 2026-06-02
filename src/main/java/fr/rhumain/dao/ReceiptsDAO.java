package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Order;
import fr.rhumain.structs.Receipt;
import fr.rhumain.structs.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReceiptDAO implements DAO<Receipt, Integer> {

    private static final String SQL_FIND_BY_ID =
            "SELECT r.*, " +
                    "u.first_name, u.last_name, u.email, u.password, u.balance " +
                    "FROM receipts r " +
                    "JOIN users u ON r.id_user = u.id " +
                    "WHERE r.id=?";

    private static final String SQL_FIND_ALL =
            "SELECT r.*, " +
                    "u.first_name, u.last_name, u.email, u.password, u.balance " +
                    "FROM receipts r " +
                    "JOIN users u ON r.id_user = u.id";

    private static final String SQL_FIND_BY_USER =
            SQL_FIND_ALL + " WHERE r.id_user=?";

    private static final String SQL_FIND_BY_ORDER =
            "SELECT r.*, " +
                    "u.first_name, u.last_name, u.email, u.password, u.balance " +
                    "FROM receipts r " +
                    "JOIN users u ON r.id_user = u.id " +
                    "WHERE r.id_order=?";

    private static final String SQL_INSERT =
            "INSERT INTO receipts (id_order, id_user, final_price) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE receipts SET id_order=?, id_user=?, final_price=? WHERE id=?";

    private static final String SQL_DELETE = "DELETE FROM receipts WHERE id=?";

    @Override
    public Optional<Receipt> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to find receipt by id", e);
        }
    }

    @Override
    public List<Receipt> findAll() throws DAOException {
        List<Receipt> receipts = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                receipts.add(mapRow(rs));
            }
            return receipts;
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to find all receipts", e);
        }
    }

    @Override
    public Receipt save(Receipt entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, entity.idOrder());
            stm.setInt(2, entity.idUser());
            stm.setDouble(3, entity.price());
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Receipt(keys.getInt(1), entity.idOrder(), entity.idUser(), entity.price());
                }
                throw new DAOException("[ReceiptDAO] No generated key returned when saving receipt");
            }
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to save receipt", e);
        }
    }

    @Override
    public void update(Receipt entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[ReceiptDAO] Impossible to update receipt with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setInt(1, entity.idOrder());
            stm.setInt(2, entity.idUser());
            stm.setDouble(3, entity.price());
            stm.setInt(4, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[ReceiptDAO] No receipt found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to update receipt", e);
        }
    }

    @Override
    public void delete(Receipt entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[ReceiptDAO] No receipt found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to delete receipt", e);
        }
    }

    public List<Receipt> findByUser(Integer idUser) throws DAOException {
        List<Receipt> receipts = new ArrayList<>();
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_USER)) {
            stm.setInt(1, idUser);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    receipts.add(mapRow(rs));
                }
            }
            return receipts;
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to find receipts by user", e);
        }
    }

    public Optional<Receipt> findByOrder(Integer idOrder) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ORDER)) {
            stm.setInt(1, idOrder);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[ReceiptDAO] Impossible to find receipt by order", e);
        }
    }

    private Receipt mapRow(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getInt("id_user"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getInt("balance")
        );
        // Order chargé en mode léger (id uniquement) — utiliser OrderDAO pour la version complète
        Order order = new Order(rs.getInt("id_order"), null, null, 0, null, null, 0, null, null);
        return new Receipt(
                rs.getInt("id"),
                order.id(),
                user.id(),
                rs.getInt("final_price")
        );
    }
}