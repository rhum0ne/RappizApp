package fr.rhumain.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and expose the database config
 * from the file resources/config/database.properties.
 *
 * Unique load at the beginning (lazy + thread-safe via class loader).
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "/database.properties";
    private final String url;
    private final String user;
    private final String password;
    private final String driver;
    private final boolean autoCommit;
    private final int connectionTimeout;

    private static class Holder {
        private static final DatabaseConfig INSTANCE = load();
    }

    public static DatabaseConfig getInstance() {
        return Holder.INSTANCE;
    }

    private DatabaseConfig(Properties props) {
        this.url = require(props, "db.url");
        this.user = require(props, "db.user");
        this.password = props.getProperty("db.password", "");
        this.driver = require(props, "db.driver");
        this.autoCommit = Boolean.parseBoolean(props.getProperty("db.autoCommit", "true"));
        this.connectionTimeout = Integer.parseInt(props.getProperty("db.connectionTimeoutSeconds", "5"));
    }

    private static DatabaseConfig load() {
        Properties props = new Properties();
        try(InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if(input == null) {
                throw new IllegalStateException("Config file unreachable: " + CONFIG_FILE + "\nVerify its classpath (resources/config)");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
        return new DatabaseConfig(props);
    }

    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if(value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value.trim();
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public boolean isAutoCommit() {
        return this.autoCommit;
    }

    public String getDriver() {
        return this.driver;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUser() {
        return this.user;
    }

    public String getUrl() {
        return this.url;
    }
}
