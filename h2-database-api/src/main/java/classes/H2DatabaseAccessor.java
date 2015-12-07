package classes;/*
Copyright (c) 2015, AGH University of Science and Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.
*/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Provides access to specified database.
 * Requires informations about database name and host/domain, also needs user security credentials (username, password).
 */
public class H2DatabaseAccessor {
    private final H2DatabaseConnector m_connector;
    private static final String CREATE_TABLE_STATEMENT = "create table \"%s\" (id int unsigned not null auto_increment, primary key (id)";
    private static final String CREATE_TABLE_COLUMN = ", %s varchar(65025) ";

    private static final String DROP_TABLE_STATEMENT = "drop table if exists \"%s\";";

    private static final String INSERT_INTO_STATEMENT = "insert into \"%s\" values (default ";

    private static final String ESCAPE_CHARACTER_REGEX = "[\\n\\r\\t\\']";

    /**
     * Basic constructor of @see classes.H2DatabaseAccessor
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
     * @return boolean value, <code>true</code> if table was created successfully, <code>false</code> otherwise
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public boolean addTable(String tableName, List<String> columnNames) throws SQLException {
        dropTable(tableName);

        boolean result = true;
        Connection connection = null;

        // Check if user passed any column names
        if (columnNames.isEmpty()) {
            throw new IllegalArgumentException("No column names specified");
        }

        // Create SQL statement
        StringBuilder statementBuilder = new StringBuilder(String.format(CREATE_TABLE_STATEMENT, tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

        for (String columnName : columnNames) {
            statementBuilder.append(String.format(CREATE_TABLE_COLUMN, columnName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));
        }

        statementBuilder.append(");");


        try {
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
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public void dropTable(String tableName) throws SQLException {
        Connection connection = null;

        // Create SQL statement
        StringBuilder statementBuilder = new StringBuilder(String.format(DROP_TABLE_STATEMENT, tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

        try {
            // Execute
            connection = m_connector.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(statementBuilder.toString());
            statement.executeUpdate();
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
    }

    /**
     * Adds a new row to specified table.
     *
     * @param columnNames - list of column names in table (except default primary key ID)
     * @param rowsValues  - list of list with values of each new row in table
     * @return boolean value, <code>true</code> if row was inserted succesfully, <code>false</code> otherwise
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public boolean addRowsToTable(String tableName, List<String> columnNames, List<List<String>> rowsValues) throws SQLException {
        boolean result = true;
        Connection connection = null;

        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        // Check arguments validity
        if (columnNames.isEmpty()) {
            throw new IllegalArgumentException("No column names specified");
        }
        if (rowsValues.isEmpty()) {
            throw new IllegalArgumentException("No row values specified");
        }

        // Create string with SQL statement
        StringBuilder statementBuilder = new StringBuilder(String.format(INSERT_INTO_STATEMENT, tableName.replaceAll(ESCAPE_CHARACTER_REGEX, "")));

        for (int index = 0; index < rowsValues.get(0).size(); index++) {
            statementBuilder.append(", ?");
        }
        statementBuilder.append(");");

        try {
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
     * @param tableName       - name of table
     * @param columnNames     - list of column names
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @return ResultSet of executed query
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public List<List<String>> selectValuesFromTable(String tableName, List<String> columnNames,
                                                    Map<String, String> columnRegexMap) throws SQLException {
        return selectValuesFromTable(tableName, columnNames, columnRegexMap, null, null);
    }

    /**
     * Executes a SELECT query on specified table with given list of columns as parameters. Optionally user can specify regular expression for some of the columns
     *
     * @param tableName       - name of table
     * @param columnNames     - list of column names
     * @param columnRegexMap  - contains pairs of column name (key) and associated regex (value).
     * @param limit           - maximum number of rows that should be
     * @param offset          - skip that many rows before beginning to return row
     * @return ResultSet of executed query
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public List<List<String>> selectValuesFromTable(String tableName, List<String> columnNames,
                                                    Map<String, String> columnRegexMap,
                                                    Integer limit, Integer offset) throws SQLException {
        List<List<String>> result = null;
        Connection connection = null;

        // Check arguments validity
        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }
        if (columnNames.isEmpty()) {
            throw new IllegalArgumentException("No column names specified");
        }

        // Create string with SQL statement
        StringBuilder statementBuilder = new StringBuilder("select ");
        statementBuilder.append(columnNames.get(0).replaceAll(ESCAPE_CHARACTER_REGEX, ""));
        for (int i = 1; i < columnNames.size(); i++) {
            statementBuilder.append(", ")
                    .append(columnNames.get(i).replaceAll(ESCAPE_CHARACTER_REGEX, ""));
        }
        statementBuilder.append(" from ").append(tableName.replaceAll(ESCAPE_CHARACTER_REGEX, ""));
        if (columnRegexMap != null && !columnRegexMap.isEmpty()) {
            addRegexesToStatement(statementBuilder, columnRegexMap);
        }
        if(limit != null && limit > 0) {
            addOffsetAndLimitToStatement(statementBuilder, limit, offset);
        }
        statementBuilder.append(';');

        try {
            // Execute
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
     * @param tableName       - name of table
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @return ResultSet of executed query
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public int countValuesFromTable(String tableName, Map<String, String> columnRegexMap) throws SQLException {
        return countValuesFromTable(tableName, columnRegexMap, null, null);
    }

    /**
     * Executes a SELECT COUNT(*) query on specified table. Optionally user can specify regular expression for some of the columns
     *
     * @param tableName       - name of table
     * @param columnRegexMap- contains pairs of column name (key) and associated regex (value).
     * @param limit           - maximum number of rows that should be
     * @param offset          - skip that many rows before beginning to return row
     * @return ResultSet of executed query
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public int countValuesFromTable(String tableName, Map<String, String> columnRegexMap,
                                    Integer limit, Integer offset) throws SQLException {
        Integer result = null;
        Connection connection = null;

        // Check arguments validity
        if (!checkIfTableExists(tableName)) {
            throw new SQLException(String.format("Table %s do not exists in database", tableName));
        }

        // Create string with SQL statement
        StringBuilder statementBuilder = new StringBuilder("select count(*) from ").append(tableName.replaceAll(ESCAPE_CHARACTER_REGEX, ""));
        if (columnRegexMap != null && !columnRegexMap.isEmpty()) {
            addRegexesToStatement(statementBuilder, columnRegexMap);
        }
        if(limit != null && limit > 0 && offset != null && offset > 0) {
            addOffsetAndLimitToStatement(statementBuilder, limit, offset);
        }
        statementBuilder.append(';');

        try {
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
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new LinkedList<>();
        Connection connection = null;
        try {
            connection = m_connector.getConnection();
            connection.setAutoCommit(false);

            ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, null);
            connection.commit();
            while (columns.next()) {
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
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    public boolean checkIfTableExists(String tableName) throws SQLException {
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

    /**
     * Adds columns regexes to SELECT query. This method is used to avoid code duplication
     *
     * @param statementBuilder - reference to StringBuilder used to create statement
     * @param columnRegexMap-  contains pairs of column name (key) and associated regex (value).
     * @return true if table exists, false otherwise
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    private void addRegexesToStatement(StringBuilder statementBuilder, Map<String, String> columnRegexMap) {
        statementBuilder.append(" where ");
        Set<Entry<String, String>> entries = columnRegexMap.entrySet();
        Iterator<Entry<String, String>> iterator = entries.iterator();
        Entry<String, String> entry = iterator.next();
        statementBuilder.append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                .append(" regexp \'")
                .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                .append('\'');

        while (iterator.hasNext()) {
            entry = iterator.next();
            statementBuilder.append(" and ")
                    .append(entry.getKey().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                    .append(" regexp \'")
                    .append(entry.getValue().replaceAll(ESCAPE_CHARACTER_REGEX, ""))
                    .append('\'');
        }
    }

    /**
     * Adds columns LIMIT and OFFSET parameters to SELECT query. This method is used to avoid code duplication
     *
     * @param statementBuilder - reference to StringBuilder used to create statement
     * @param limit -  value for the LIMIT parameter
     * @param offset - value for the OFFSET parameter
     * @return true if table exists, false otherwise
     * @exception SQLException - when operation wasn't successfull or connection was impossible
     */
    private void addOffsetAndLimitToStatement(StringBuilder statementBuilder, Integer limit, Integer offset) {
        statementBuilder.append(" limit ").append(limit);
        if (offset != null && offset > 0) {
            statementBuilder.append(" offset ").append(offset);
        }
    }
}