import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * Class responsible for establishing and validating connection with database.
 */
public class H2DatabaseConnector {
    private static final String dbDriver = "org.h2.Driver";
    private final String username;
    private final String password;
    private final String databaseURL;

    /**
     * Constructs a new @see H2DatabaseConnector with username, password and databaseURL
     *
     * @param username    - name of user account used for connecting with database
     * @param password    - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     *
     * @throws NullPointerException - when one of given parameters is null or empty
     */
    public H2DatabaseConnector(String username, String password, String databaseURL) {
        requireNonNull(username, "username cannot be null");
        requireNonNull(password, "password cannot be null");
        requireNonNull(databaseURL, "databaseURL cannot be null");

        this.username = username;
        this.password = password;
        this.databaseURL = databaseURL;

        /*TODO Add a connection validation?
        * connection = DriverManager.getConnection(databaseURL, username, password); ?
        * */
    }

    /**
     * Returns the username
     *
     * @return Value of <code>username</code> field
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the password
     *
     * @return Value of <code>password</code> field
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the database URL
     *
     * @return Value of <code>databaseURL</code> field
     */
    public String getDatabaseURL() {
        return this.databaseURL;
    }

    /**
     * Returns the instance of <code>Connection</code> class using values of fields username, password and databaseURL
     *
     * @return New instance of <code>Connection</code>
     */
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
