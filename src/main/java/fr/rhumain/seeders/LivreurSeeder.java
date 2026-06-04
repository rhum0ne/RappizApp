package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.LivreurDefinition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class LivreurSeeder implements DataSeeder {

    public LivreurSeeder() {}

    private static final List<LivreurDefinition> LIVREURS = List.of(
        new LivreurDefinition("Marc", "Lefèvre", "marc.lefevre@rappiz.fr", "rappiz"),
        new LivreurDefinition("Nora", "Bailly", "nora.bailly@rappiz.fr", "rappiz"),
        new LivreurDefinition("Hugo", "Bernard", "hugo.bernard@rappiz.fr", "rappiz")
    );

    @Override
    public String getName() {
        return "Livreurs";
    }

    @Override
    public int getOrder() {
        return 15;
    }

    @Override
    public boolean isAlreadySeeded() throws DAOException {
        String sql = "SELECT COUNT(*) FROM delivers";
        try(Statement stm = ConnectionManager.getConnection().createStatement();
            ResultSet rq = stm.executeQuery(sql)) {
            return rq.next() && rq.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DAOException("Impossible to verify delivers", e);
        }
    }

    @Override
    public void seed() throws DAOException, SQLException {
        String sql = "INSERT INTO delivers (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(LivreurDefinition livreurDef : LIVREURS) {
                stm.setString(1, livreurDef.firstName());
                stm.setString(2, livreurDef.lastName());
                stm.setString(3, livreurDef.email());
                stm.setString(4, livreurDef.password());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Impossible to seed ingredients", e);
        }
    }
}
