package fr.rhumain.seeders;

import fr.rhumain.dao.ConnectionManager;
import fr.rhumain.dao.VehiculeDAO;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.seeders.references.IngredientName;
import fr.rhumain.seeders.references.LivreurDefinition;
import fr.rhumain.seeders.references.VehiculeReference;
import fr.rhumain.structs.Livreur;
import fr.rhumain.structs.Vehicule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static fr.rhumain.seeders.references.VehiculeReference.*;

public class LivreurSeeder implements DataSeeder {
    private final VehiculeDAO vehiculeDAO;

    public LivreurSeeder(VehiculeDAO vehiculeDAO) {
        this.vehiculeDAO = vehiculeDAO;
    }

    private static final List<LivreurDefinition> LIVREURS = List.of(
        new LivreurDefinition("Marc", "Lefèvre", "marc.lefevre@rappiz.fr", "rappiz", SCOOTER),
        new LivreurDefinition("Nora", "Bailly", "nora.bailly@rappiz.fr", "rappiz", VELO),
        new LivreurDefinition("Hugo", "Bernard", "hugo.bernard@rappiz.fr", "rappiz", VOITURE)
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
        String sql = "INSERT INTO delivers (first_name, last_name, email, password, id_vehicule) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement stm = ConnectionManager.getConnection().prepareStatement(sql)) {
            for(LivreurDefinition livreurDef : LIVREURS) {
                Vehicule vehicule = this.vehiculeDAO.findByBrandAndModel(
                    livreurDef.vehicule().getBrand(),
                    livreurDef.vehicule().getModel()
                ).orElseThrow(() -> new DAOException("Vehicule not found for livreur " + livreurDef.firstName()));
                stm.setString(1, livreurDef.firstName());
                stm.setString(2, livreurDef.lastName());
                stm.setString(3, livreurDef.email());
                stm.setString(4, livreurDef.password());
                stm.setInt(5, vehicule.id());
                stm.addBatch();
            }
            stm.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Impossible to seed ingredients", e);
        }
    }
}
