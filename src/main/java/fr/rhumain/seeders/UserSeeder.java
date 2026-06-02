package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class UserSeeder implements DataSeeder {
    private static List<User> DEFAULT_USERS = List.of(
            User.of("Jean", "Dupont", "jean.dupont@rappiz.fr", "rappiz", 5000),
            User.of("Sarah", "Martin", "sarah.martin@rappiz.fr", "rappiz", 2750),
            User.of("Yanis", "Petit", "yanis.petit@rappiz.fr", "rappiz", 230)
    );

    @Override
    public String getName() {
        return "Users";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        String sql = "SELECT COUNT(*) FROM users";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify users", e);
        }
    }

    @Override
    public void seed() throws DAOException, SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password, balance) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(User user : DEFAULT_USERS) {
                stm.setString(1, user.firstName());
                stm.setString(2, user.lastName());
                stm.setString(3, user.email());
                stm.setString(4, user.password());
                stm.setInt(5, user.balance());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("[UserSeeder] Impossible to seed pizzas", e);
        }
    }
}
