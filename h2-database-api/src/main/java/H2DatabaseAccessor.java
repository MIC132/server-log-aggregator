import java.sql.*;
import java.util.List;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 */
public class H2DatabaseAccessor {
    private static final String ifExistsQuery = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = (?)";
    private final H2DatabaseConnector connector;

    /**
     * Basic constructor of @see H2DatabaseAccessor
     *
     * @param username    - name of user account used for connecting with database
     * @param password    - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     * @throws NullPointerException - when one of given parameters is null or empty
     */
    public H2DatabaseAccessor(String username, String password, String databaseURL) {
        connector = new H2DatabaseConnector(username, password, String.format("jdbc:h2:%s;IFEXISTS=TRUE;DATABASE_TO_UPPER=false", databaseURL));
    }

    /**
     * Creates a new table in a database specified in <code>connector</code>.
     * If table already exists it is removed before creating a new one.
     * Table always uses integer sequence named id as primary key. User cannot change that.
     * User only specifies names of data columns. All of them are of type <code>varchar(255)</code>
     *
     * @param tableName   - name of a new table
     * @param columnNames - list of column names
     * @return boolean value, <code>true</code> if table was created succesfully, <code>false</code> otherwise
     */
    public boolean addTable(String tableName, List<String> columnNames) throws SQLException {
        dropTable(tableName);

        boolean result = true;
        Connection connection = null;

        try {
            // Check arguments validity
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("No column names specified");
            }

            // Create query
            StringBuilder columnsBuilder = new StringBuilder();
            StringBuilder queryBuilder = new StringBuilder("create table ").append(tableName).append(" (id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (id)");

            for (String columnName : columnNames) {
                columnsBuilder.append(", ").append(columnName).append(" varchar(255) ");
            }

            queryBuilder.append(columnsBuilder)
                    .append(");");

            // Execute query
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            statement.executeUpdate();
            statement.close();

            connection.commit();
        } catch (SQLException e) {
            result = false;
            throw e;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
        return result;
    }

    /**
     * Removes table of given name (if exists) from database
     *
     * @param tableName - name of a table
     */
    public boolean dropTable(String tableName) throws SQLException {
        boolean result = true;
        Connection connection = null;

        try {
            // Create query
            StringBuilder queryBuilder = new StringBuilder("drop table if exists ").append(tableName).append(';');

            // Execute query
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
            statement.executeUpdate();
            statement.close();

            connection.commit();
        } catch (SQLException e) {
            /* TODO Need to create sensible exception handling - throw all the way up to GUI? */
            e.printStackTrace();
            result = false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
        return result;
    }

    /**
     * Adds a new row to specified table.
     *
     * @param columnNames
     * @param rowValues
     * @return boolean value, <code>true</code> if row was inserted succesfully, <code>false</code> otherwise
     */
    public boolean addRowToTable(String tableName, List<String> columnNames, List<String> rowValues) throws SQLException {
        boolean result = true;
        Connection connection = null;

        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        try {
            // Check arguments validity
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("No column names specified");
            }
            if (rowValues.isEmpty()) {
                throw new IllegalArgumentException("No row values specified");
            }
            if (rowValues.size() != columnNames.size()) {
                throw new IllegalArgumentException("Number of values must be equal to number of column names");
            }

            // Create query
            StringBuilder queryBuilder = new StringBuilder("insert into ")
                    .append(tableName).append(" (");
            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder rowValuesBuilder = new StringBuilder();

            columnNamesBuilder.append(columnNames.get(0));
            for (int index = 1; index < columnNames.size(); index++) {
                columnNamesBuilder.append(", ").append(columnNames.get(index));
            }

            rowValuesBuilder.append("\'").append(rowValues.get(0)).append("\'");
            for (int index = 1; index < rowValues.size(); index++) {
                rowValuesBuilder.append(", \'").append(rowValues.get(index)).append("\'");
            }
            queryBuilder.append(columnNamesBuilder).append(") values (")
                    .append(rowValuesBuilder).append(");");

            // Execute query
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            int i = statement.executeUpdate();
            statement.close();

            connection.commit();
        } catch (SQLException e) {
            result = false;
            throw e;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

        return result;
    }

    /**
     * Executes a SELECT query on specified table with given list of columns as parameters
     *
     * @param tableName   - name of table
     * @param columnNames - list of column names
     * @return ResultSet of executed query
     */
    public List<List<String>> selectValuesFromTable(String tableName, List<String> columnNames) throws SQLException {
        List<List<String>> result = null;
        Connection connection = null;

        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        try {
            // TODO Column names might be empty strings. Well... fuck it?
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("No column names specified");
            }

            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder queryBuilder = new StringBuilder("select ");

            queryBuilder.append(columnNames.get(0));
            for (int index = 1; index < columnNames.size(); index++) {
                columnNamesBuilder.append(", ").append(columnNames.get(index));
            }

            queryBuilder.append(columnNamesBuilder).append(" from ").append(tableName).append(';');

            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            ResultSet resultSet = statement.executeQuery();

            // TODO Get ze rezultz into da choppa!!!

            statement.close();

            connection.commit();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

        return result;
    }

    // TODO Do we even need this method?
    // TODO Returning a ResultSet is a bad idea. Need better solution

    /**
     * Executes any valid SQL query
     *
     * @param query - string with a valid SQL query
     * @return ResultSset
     */
    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet result = null;
        Connection connection = null;

        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);

            statement.close();
            connection.commit();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

        return result;
    }

    /**
     * Ensures that given table exists in database
     *
     * @param tableName - string with a valid SQL query
     *
     * @return true if table exists, false otherwise
     */
    private boolean checkIfTableExists(String tableName) throws SQLException {
        Connection connection = null;
        boolean result = false;
        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null);
            connection.commit();
            result = tables.first();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

        return result;
    }
}