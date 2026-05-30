package fr.rhumain.seeders;

import fr.rhumain.exceptions.DAOException;

public interface DataSeeder {
    String getName();
    int getOrder();
    boolean isAlreadySeeded() throws DAOException;
    void seed() throws DAOException;
}
