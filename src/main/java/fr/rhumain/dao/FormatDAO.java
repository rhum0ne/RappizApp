package fr.rhumain.dao;

import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Format;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormatDAO implements DAO<Format, Integer> {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM formats WHERE id=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM formats";
    private static final String SQL_INSERT = "INSERT INTO formats (name, percentage) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE formats SET name=?, percentage=? WHERE id=?";
    private static final String SQL_DELETE = "DELETE FROM formats WHERE id=?";

    @Override
    public Optional<Format> findById(Integer id) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("[FormatDAO] Impossible to find format by id", e);
        }
    }

    @Override
    public List<Format> findAll() throws DAOException {
        List<Format> formats = new ArrayList<>();
        try (Statement stm = ConnectionManager.getConnection().createStatement();
             ResultSet rs = stm.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                formats.add(mapRow(rs));
            }
            return formats;
        } catch (SQLException e) {
            throw new DAOException("[FormatDAO] Impossible to find all formats", e);
        }
    }

    @Override
    public Format save(Format entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, entity.nom());
            stm.setInt(2, entity.pricePercetage());
            stm.executeUpdate();
            try (ResultSet keys = stm.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Format(keys.getInt(1), entity.nom(), entity.pricePercetage());
                }
                throw new DAOException("[FormatDAO] No generated key returned when saving format");
            }
        } catch (SQLException e) {
            throw new DAOException("[FormatDAO] Impossible to save format", e);
        }
    }

    @Override
    public void update(Format entity) throws DAOException {
        if (entity.id() == null) {
            throw new DAOException("[FormatDAO] Impossible to update format with null id");
        }
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_UPDATE)) {
            stm.setString(1, entity.nom());
            stm.setInt(2, entity.pricePercetage());
            stm.setInt(3, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[FormatDAO] No format found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[FormatDAO] Impossible to update format", e);
        }
    }

    @Override
    public void delete(Format entity) throws DAOException {
        try (PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(SQL_DELETE)) {
            stm.setInt(1, entity.id());
            int rows = stm.executeUpdate();
            if (rows == 0) {
                throw new DAOException("[FormatDAO] No format found with id " + entity.id());
            }
        } catch (SQLException e) {
            throw new DAOException("[FormatDAO] Impossible to delete format", e);
        }
    }

    private Format mapRow(ResultSet rs) throws SQLException {
        return new Format(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("percentage")
        );
    }
}
