package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.Format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class FormatSeeder implements DataSeeder {
    public static List<Format> FORMATS = List.of(
            new Format("Naine", 67),
            new Format("Humaine", 100),
            new Format("Ogresse", 134)
    );

    @Override
    public String getName() {
        return "Formats";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        String sql = "SELECT COUNT(*) FROM formats";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify formats", e);
        }
    }

    @Override
    public void seed() throws DAOException {

    }
}
