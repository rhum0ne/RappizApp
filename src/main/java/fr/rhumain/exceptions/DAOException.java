package fr.rhumain.exceptions;

import java.sql.SQLException;

public class DAOException extends SQLException{
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
