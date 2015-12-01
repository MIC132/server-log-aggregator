import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 */
public class H2DatabaseAccessor {
    private final H2DatabaseConnector m_connector;
    private static final String CREATE_TABLE_STATEMENT = "create table \"%s\" (id int unsigned not null auto_increment, primary key (id)";
    private static final String CREATE_TABLE_COLUMN = ", %s varchar(255) ";

    private static final String DROP_TABLE_STATEMENT = "drop table if exists \"%s\";";

    private static final String INSERT_INTO_STATEMENT =  "insert into \"%s\" values (default ";

    private static final String ESCAPE_CHARACTER_REGEX = "[\\n\\r\\t\\']";

    /**
     * Basic constructor of @see H2DatabaseAccessor
     *
     * @param username    - name of user account used for connecting with database
     * @param password    - password credentials
     * @param databaseURL - full URL that identifies database (host/directory + database name)
     * @throws NullPointerException - when one of given parameters is null or empty
     */
    public H2DatabaseAccessor(String username, String password, String databaseURL) {
        m_connector = new H2DatabaseConnector(username, password, String.format("jdbc:h2:%s;IFEXISTS=TRUE;DATABASE_TO_UPPER=false", databaseURL));
    }

    /**
     * Creates a new table in a database specified in <code>m_connector</code>.
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
            // Check if user passed any column names
            if (columnNames.isEmpty()) {
                throw new IllegalArgumentException("No column names specified");
            }

            // Create string with parametrized SQL statement - it will be used in PreparedStatement
            StringBuilder statementBuilder = new StringBuilder(String.format(CREATE_TABLE_STATEMENT, tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

            for (String columnName : columnNames) {
                statementBuilder.append(String.format(CREATE_TABLE_COLUMN, columnName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));
            }

            statementBuilder.append(");");

            // Create PreparedStatement and insert parameters
            connection = m_connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());

            // Execute
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
            StringBuilder statementBuilder = new StringBuilder(String.format(DROP_TABLE_STATEMENT, tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

            // Execute
            connection = m_connector.getConnection();
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
     * @param columnNames - list of column names in table (except default primary key ID)
     * @param rowsValues - list of list with values of each new row in table
     * @return boolean value, <code>true</code> if row was inserted succesfully, <code>false</code> otherwise
     */
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

            // Create string with SQL statement
            StringBuilder statementBuilder = new StringBuilder(String.format(INSERT_INTO_STATEMENT,tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

            for (int index = 0; index < rowsValues.get(0).size(); index++) {
                statementBuilder.append(", ?");
            }
            statementBuilder.append(");");
            // Execute
            connection = m_connector.getConnection();
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
     * Executes a SELECT query on specified table with given list of columns as parameters. Optionally user can specify regular expression for some of the columns
     *
     * @param tableName   - name of table
     * @param columnNames - list of column names
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @return ResultSet of executed query
     */
    // TODO selectValuesFromTable and countValuesFromTable are very similar pieces of code consider refactoring
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

            StringBuilder statementBuilder = new StringBuilder("select ");

            statementBuilder.append(columnNames.get(0).replaceAll(ESCAPE_CHARACTER_REGEX, ""));
            for (int i = 1; i < columnNames.size(); i++) {
                statementBuilder.append(", ")
                        .append(columnNames.get(i).replaceAll(ESCAPE_CHARACTER_REGEX, ""));
            }

            statementBuilder.append(" from ").append(tableName.replaceAll(ESCAPE_CHARACTER_REGEX, ""));

            if (columnRegexMap != null && !columnRegexMap.isEmpty()) {
                statementBuilder.append(" where ");
                Set<Entry<String, String>> entries = columnRegexMap.entrySet();
                Iterator<Entry<String, String>> iterator = entries.iterator();
                Entry<String, String> entry = iterator.next();
                statementBuilder.append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                        .append(" regexp \'")
                        .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                        .append('\'');

                while(iterator.hasNext()) {
                    entry = iterator.next();
                    statementBuilder.append(" and ")
                            .append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                            .append(" regexp \'")
                            .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                            .append('\'');
                }
            }

            statementBuilder.append(';');

            connection = m_connector.getConnection();
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

    /**
     * Executes a SELECT COUNT(*) query on specified table. Optionally user can specify regular expression for some of the columns
     *
     * @param tableName   - name of table
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @return ResultSet of executed query
     */
    public int countValuesFromTable(String tableName, Map<String, String> columnRegexMap) throws SQLException {
        Integer result = null;
        Connection connection = null;

        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        try {
            StringBuilder statementBuilder = new StringBuilder("select count(*) from ").append(tableName.replaceAll(ESCAPE_CHARACTER_REGEX, ""));

            if (columnRegexMap != null && !columnRegexMap.isEmpty()) {
                statementBuilder.append(" where ");
                Set<Entry<String, String>> entries = columnRegexMap.entrySet();
                Iterator<Entry<String, String>> iterator = entries.iterator();
                Entry<String, String> entry = iterator.next();
                statementBuilder.append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                        .append(" regexp \'")
                        .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                        .append('\'');

                while(iterator.hasNext()) {
                    entry = iterator.next();
                    statementBuilder.append(" and ")
                            .append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                            .append(" regexp \'")
                            .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                            .append('\'');
                }
            }

            statementBuilder.append(';');

            connection = m_connector.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            result = Integer.parseInt(resultSet.getString("COUNT(*)"));
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
     * Returns a list of column names
     *
     * @param tableName - name of the table
     * @return list of column names
     */
    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new LinkedList<>();
        Connection connection = null;
        try {
            connection = m_connector.getConnection();
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
            connection = m_connector.getConnection();
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