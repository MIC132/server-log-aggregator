import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.*;

/**
 * class responsible for establishing and validating connection with database.
 * */
public class H2DatabaseConnector {
    private String username;
    private String password;
    private String databaseURL;
    private Connection connection;

    public H2DatabaseConnector(String username, String password, String databaseURL) throws SQLException {
        requireNonNull(username, "username cannot be null");
        requireNonNull(password, "password cannot be null");
        requireNonNull(databaseURL, "databaseURL cannot be null");

        this.username = username;
        this.password = password;
        this.databaseURL = databaseURL;
        connection = DriverManager.getConnection(databaseURL, username, password);
        if (!connection.isValid(1000)) {
            throw new SQLException(String.format("Could not connect to database %s with given credentials.", databaseURL));
        }
        connection.close();
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

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseURL, username, password);
    }
}
