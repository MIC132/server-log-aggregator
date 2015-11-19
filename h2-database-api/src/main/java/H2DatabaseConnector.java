import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.*;

/**
 * class responsible for establishing and validating connection with database.
 * */
public class H2DatabaseConnector {
    private final String dbDriver = "org.h2.Driver";
    private String username;
    private String password;
    private String databaseURL;
    private Connection connection;

    public H2DatabaseConnector(String username, String password, String databaseURL) {
        requireNonNull(username, "username cannot be null");
        requireNonNull(password, "password cannot be null");
        requireNonNull(databaseURL, "databaseURL cannot be null");

        this.username = username;
        this.password = password;
        this.databaseURL = databaseURL;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        requireNonNull(username, "username cannot be null");
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        requireNonNull(password, "password cannot be null");
        this.password = password;
    }

    public String getDatabaseURL() {
        return this.databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        requireNonNull(databaseURL, "databaseURL cannot be null");
        this.databaseURL = databaseURL;
    }

    public Connection getConnection() {
        Connection connection;
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 driver is missing", e);
        }

        try {
            connection = DriverManager.getConnection(databaseURL, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Could not open connection to database", e);
        }

        return connection;
    }
}
