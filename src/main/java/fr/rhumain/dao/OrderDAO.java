package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO implements DAO<Order, Integer> {

    private static final String SQL_FIND_BY_ID =
            "SELECT o.*, " +
                    "u.first_name as u_first_name, u.last_name as u_last_name, u.email as u_email, u.password as u_password, u.balance, " +
                    "p.name as pizza_name, p.price as pizza_price," +
                    "d.first_name as d_first_name, d.last_name as d_last_name, d.email as d_email, d.password as d_password, " +
                    "v.brand, v.model " +
                    "FROM orders o " +
                    "JOIN users u ON o.id_user = u.id " +
                    "JOIN pizzas p ON o.id_pizza = p.id " +
                    "LEFT JOIN delivers d ON o.id_deliver = d.id " +
                    "LEFT JOIN vehicules v ON o.id_vehicule = v.id " +
                    "WHERE o.id=?";

    private static final String SQL_FIND_ALL =
            "SELECT o.*, " +
                    "u.first_name as u_first_name, u.last_name as u_last_name, u.email as u_email, u.password as u_password, u.balance, " +
                    "p.name as pizza_name, " +
                    "d.first_name as d_first_name, d.last_name as d_last_name, d.email as d_email, d.password as d_password, " +
                    "v.brand, v.model " +
                    "FROM orders o " +
                    "JOIN users u ON o.id_user = u.id " +
                    "JOIN pizzas p ON o.id_pizza = p.id " +
                    "LEFT JOIN delivers d ON o.id_deliver = d.id " +
                    "LEFT JOIN vehicules v ON o.id_vehicule = v.id";

    private static final String SQL_FIND_BY_USER =
            SQL_FIND_ALL + " WHERE o.id_user=?";

    private static final String SQL_INSERT =
            "INSERT INTO orders (id_user, id_pizza, id_format, timestamp_order, timestamp_deliver, final_price, id_deliver, id_vehicule) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE orders SET id_user=?, id_pizza=?, id_format=?, timestamp_order=?, timestamp_deliver=?, final_price=?, id_deliver=?, id_vehicule=? " +
                    "WHERE id=?";

    private static final String SQL_DELETE = "DELETE FROM orders WHERE id=?";

    @Override
    public Optional<Order> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to find order by id", e);
        }
    }

    @Override
    public List<Order> findAll() throws DAOException {
        List<Order> orders = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
            return orders;
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to find all orders", e);
        }
    }

    @Override
    public Order save(Order entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, entity.User().id());
            stm.setInt(2, entity.pizza().id());
            stm.setInt(3, entity.format().id());
            stm.setTimestamp(4, entity.timeStamp());
            stm.setTimestamp(5, entity.timeStampLivraison());
            stm.setDouble(6, entity.price());
            if (entity.livreur() != null) {
                stm.setInt(7, entity.livreur().id());
            } else {
                stm.setNull(7, Types.INTEGER);
            }
            if (entity.vehicule() != null) {
                stm.setInt(8, entity.vehicule().id());
            } else {
                stm.setNull(8, Types.INTEGER);
            }
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Order(keys.getInt(1), entity.User(), entity.pizza(), entity.format(),
                            entity.timeStamp(), entity.timeStampLivraison(), entity.price(),
                            entity.livreur(), entity.vehicule());
                }
                throw new DAOException("[OrderDAO] No generated key returned when saving order");
            }
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to save order", e);
        }
    }

    @Override
    public void update(Order entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[OrderDAO] Impossible to update order with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setInt(1, entity.User().id());
            stm.setInt(2, entity.pizza().id());
            stm.setInt(3, entity.format().id());
            stm.setTimestamp(4, entity.timeStamp());
            stm.setTimestamp(5, entity.timeStampLivraison());
            stm.setDouble(6, entity.price());
            if (entity.livreur() != null) {
                stm.setInt(7, entity.livreur().id());
            } else {
                stm.setNull(7, Types.INTEGER);
            }
            if (entity.vehicule() != null) {
                stm.setInt(8, entity.vehicule().id());
            } else {
                stm.setNull(8, Types.INTEGER);
            }
            stm.setInt(9, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[OrderDAO] No order found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to update order", e);
        }
    }

    @Override
    public void delete(Order entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[OrderDAO] No order found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to delete order", e);
        }
    }

    public List<Order> findByUser(Integer idUser) throws DAOException {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_USER)) {
            stm.setInt(1, idUser);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new DAOException("[OrderDAO] Impossible to find orders by user", e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getInt("id_user"),
                rs.getString("u_first_name"),
                rs.getString("u_last_name"),
                rs.getString("u_email"),
                rs.getString("u_password"),
                rs.getInt("balance")
        );
        // Pizza sans ingrédients ici (chargement léger) — utiliser PizzaDAO.findById pour la version complète
        Pizza pizza = new Pizza(rs.getInt("id_pizza"), rs.getString("pizza_name"), List.of());

        Livreur livreur = null;
        int idDeliver = rs.getInt("id_deliver");
        if (!rs.wasNull()) {
            Vehicule vehicule = null;
            int idVehicule = rs.getInt("id_vehicule");
            if (!rs.wasNull()) {
                vehicule = new Vehicule(idVehicule, rs.getString("brand"), rs.getString("model"));
            }
            livreur = new Livreur(idDeliver, rs.getString("d_first_name"), rs.getString("d_last_name"),
                    rs.getString("d_email"), rs.getString("d_password"), vehicule);
        }

        Vehicule vehicule = null;
        int idVehicule = rs.getInt("id_vehicule");
        if (!rs.wasNull()) {
            vehicule = new Vehicule(idVehicule, rs.getString("brand"), rs.getString("model"));
        }

        return new Order(
                rs.getInt("id"),
                user,
                pizza,
                rs.getInt("id_format"),
                rs.getTimestamp("timestamp_order"),
                rs.getTimestamp("timestamp_deliver"),
                rs.getInt("final_price"),
                livreur,
                vehicule
        );
    }
}
