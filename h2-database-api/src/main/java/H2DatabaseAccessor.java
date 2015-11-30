import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 */
public class H2DatabaseAccessor {
    private static final String ifExistsQuery = "select * from INFORMATION_SCHEMA.TABLES where TABLE_NAME = (?)";
    private static final String insertStatement = "insert into (?) values (";
    private final H2DatabaseConnector connector;
    private static final String primaryKey = "id";

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

            // Create
            StringBuilder columnsBuilder = new StringBuilder();
            StringBuilder statementBuilder = new StringBuilder("create table \"")
                    .append(tableName)
                    .append(String.format("\" (%s int unsigned not null auto_increment, primary key (%s)", primaryKey, primaryKey));

            for (String columnName : columnNames) {
                columnsBuilder.append(", ").append(columnName).append(" varchar(255) ");
            }

            statementBuilder.append(columnsBuilder)
                    .append(");");

            // Execute
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());
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
            // Create
            StringBuilder statementBuilder = new StringBuilder("drop table if exists \"").append(tableName).append("\"");

            // Execute
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());
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

            // Create
            StringBuilder statementBuilder = new StringBuilder("insert into \"")
                    .append(tableName).append("\" values (default");

            for (int index = 0; index < rowValues.size(); index++) {
                statementBuilder.append(", ?");
            }
            statementBuilder.append(");");
            // Execute
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());
            for (int index = 0; index < rowValues.size(); index++) {
                statement.setString(index + 1, rowValues.get(index));
            }

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

    public boolean addRowsToTable(String tableName, List<String> columnNames, List<List<String>> rowsValues) throws SQLException {
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
            if (rowsValues.isEmpty()) {
                throw new IllegalArgumentException("No row values specified");
            }

            // Create
            StringBuilder statementBuilder = new StringBuilder("insert into \"")
                    .append(tableName).append("\" values (default");

            for (int index = 0; index < rowsValues.get(0).size(); index++) {
                statementBuilder.append(", ?");
            }
            statementBuilder.append(");");
            // Execute
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());
            for (List<String> rowValues : rowsValues) {
                for (int index = 0; index < rowValues.size(); index++) {
                    statement.setString(index + 1, rowValues.get(index));
                }
                statement.addBatch();
            }

            statement.executeBatch();
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
     * Executes a SELECT query on specified table with given list of columns as parameters.
     *
     * @param tableName   - name of table
     * @param columnNames - list of column names
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @return ResultSet of executed query
     */
    /* TODO Allow user to specify multiple regexes for one column?
     Map requires that keys are unique. In this situation the only way to specify multiple regexes is to use alternative operator (|) in regex.
     Should we implement other solution? More user-friendly? */
    public List<List<String>> selectValuesFromTable(String tableName, List<String> columnNames, Map<String, String> columnRegexMap) throws SQLException {
        List<List<String>> result = null;
        Connection connection = null;

        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        try {
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("No column names specified");
            }

            StringBuilder columnNamesBuilder = new StringBuilder();
            StringBuilder statementBuilder = new StringBuilder("select ");

            statementBuilder.append(columnNames.get(0));
            for (int index = 1; index < columnNames.size(); index++) {
                columnNamesBuilder.append(", ").append(columnNames.get(index));
            }

            statementBuilder.append(columnNamesBuilder).append(" from ").append(tableName);

            if (columnRegexMap != null && !columnRegexMap.isEmpty()) {
                statementBuilder.append(" where ");
                Set<Entry<String, String>> entries = columnRegexMap.entrySet();
                Iterator<Entry<String, String>> iterator = entries.iterator();
                Entry<String, String> entry = iterator.next();
                statementBuilder.append(entry.getKey()).append(" regexp \'").append(entry.getValue()).append('\'');

                while(iterator.hasNext()) {
                    entry = iterator.next();
                    statementBuilder.append(" AND ").append(entry.getKey()).append(" regexp \'").append(entry.getValue()).append('\'');
                }
            }

            statementBuilder.append(';');

            connection = connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());

            ResultSet resultSet = statement.executeQuery();

            result = new LinkedList<List<String>>();
            int columnsNum = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                int index = 1;
                List<String> rowValues = new ArrayList<String>(columnsNum);
                while (index <= columnsNum) {
                    rowValues.add(resultSet.getString(index));
                    index++;
                }
                result.add(rowValues);
            }

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
     * Executes any valid SQL statement
     *
     * @param statement - string with a valid SQL statement
     * @return ResultSset
     */
    public ResultSet executeQuery(String statement) throws SQLException {
        ResultSet result = null;
        Connection connection = null;

        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);
            Statement connectionStatement = connection.createStatement();
            result = connectionStatement.executeQuery(statement);

            connectionStatement.close();
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
     * Returns a list of column names
     *
     * @param tableName - name of the table
     * @return list of column names
     */
    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new LinkedList<>();
        Connection connection = null;
        try {
            connection = connector.getConnection();
            connection.setAutoCommit(false);

            ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, null);
            connection.commit();
            while(columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
            }
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

        return columnNames;
    }

    /**
     * Ensures that given table exists in database
     *
     * @param tableName - name of the table
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