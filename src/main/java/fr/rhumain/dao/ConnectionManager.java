package fr.rhumain.dao;

import fr.rhumain.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;
    private static boolean driverLoaded = false;

    private ConnectionManager() {}

    public static synchronized Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            connection = open();
        }
        return connection;
    }

    private static void loadDriver(String driverClassName) throws SQLException {
        if(driverLoaded) return;
        try {
            Class.forName(driverClassName);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "JDBC Driver not found : " + driverClassName
                            + ". Verify if the driver JAR is in the folder lib/.",
                    e);
        }
    }

    public static Connection open() throws SQLException {
        DatabaseConfig config = DatabaseConfig.getInstance();
        loadDriver(config.getDriver());

        DriverManager.setLoginTimeout(config.getConnectionTimeout());
        Connection conn = DriverManager.getConnection(
                config.getUrl(),
                config.getUser(),
                config.getPassword()
        );
        conn.setAutoCommit(config.isAutoCommit());
        System.out.println("[ConnectionManager] Connection established " + config);
        return conn;
    }

    public static synchronized void close() throws SQLException {
        if(connection != null) {
            try {
                if(!connection.isClosed()) {
                    connection.close();
                    System.out.println("[ConnectionManager] Connection closed " + connection);
                }
            } catch (SQLException e) {
                System.out.println("[ConnectionManager] Error closing connection " + connection);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Test the connection
     *
     * @return true if connection is valid
     */
    public static synchronized boolean test() {
        try {
            Connection conn = getConnection();
            return conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("[ConnectionManager] Test failed : " + e.getMessage());
            return false;
        }
    }
}
