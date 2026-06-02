package fr.rhumain.seeders;

import fr.rhumain.exceptions.DAOException;

import java.sql.SQLException;

public interface DataSeeder {
    String getName();
    int getOrder();
    boolean isAlreadySeeded() throws DAOException;
    void seed() throws DAOException, SQLException;
}
